package com.nexusforge.AquilaFramework.Dao;

import com.nexusforge.AquilaFramework.Dto.ColumnDto;
import com.nexusforge.AquilaFramework.Dto.TableDetailsDto;
import com.nexusforge.AquilaFramework.Entity.CreateTable;
import com.nexusforge.AquilaFramework.Entity.Result;
import com.nexusforge.AquilaFramework.Mgr.userTableMgr;
import jakarta.persistence.Table;
import org.hibernate.annotations.processing.SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Service
public class userTableDao {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Result createNewTableDao(String tableName, List<String> columnDefs, List<String> primaryKeys) {
        Result res = new Result();
        Connection conn = null;
        Statement stmt = null;
        String pkClause = "";
        if (!primaryKeys.isEmpty()) {
            pkClause = ", PRIMARY KEY (" + String.join(", ", primaryKeys) + ")";
        }
        String createSQL = "CREATE TABLE " + tableName + " (" + String.join(", ", columnDefs) + pkClause + ")";
        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(createSQL);
            res.setState(true);
            res.setMsgDesc("Table Name : "+ tableName +"Created Successfully.");
            res.setMsgCode("200");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close(); // VERY IMPORTANT
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }

    public Result dropTableDao(String tableName) {
        Result res = new Result();
        try {
            String sql = "Drop Table If Exists " + tableName;
            jdbcTemplate.execute(sql);
            res.setState(true);
            res.setMsgCode("200");
            res.setMsgDesc("Table : " +tableName+" Dropped Successfully.");
        } catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public TableDetailsDto getTableDataDao(String tableName) {
        TableDetailsDto tableData = new TableDetailsDto();

        String columnSql = """
        SELECT 
            c.column_name,
            c.data_type,
            c.is_nullable,
            EXISTS (
                SELECT 1 
                FROM information_schema.table_constraints tc
                JOIN information_schema.key_column_usage kcu 
                ON tc.constraint_name = kcu.constraint_name
                WHERE tc.table_name = c.table_name 
                AND kcu.column_name = c.column_name
                AND tc.constraint_type = 'PRIMARY KEY'
            ) AS is_primary_key
        FROM information_schema.columns c
        WHERE c.table_name = ?
        ORDER BY c.ordinal_position
    """;

        List<ColumnDto> columns = jdbcTemplate.query(columnSql, new Object[]{tableName}, (rs, rowNum) -> {
            ColumnDto column = new ColumnDto();
            column.setName(rs.getString("column_name"));
            column.setType(rs.getString("data_type").toUpperCase());
            column.setNotNull("NO".equals(rs.getString("is_nullable")));
            column.setPrimaryKey(rs.getBoolean("is_primary_key"));
            return column;
        });

        TableDetailsDto dto = new TableDetailsDto();
        dto.setTableName(tableName);
        dto.setColumns(columns);
        return dto;
    }

    public Map<String, userTableMgr.ColumnMeta> getExistingColumnsFromDB(String tableName, Set<String> existingCols) {
        Map<String , userTableMgr.ColumnMeta> columnMetaMap = new HashMap<>();

        try(Connection conn = dataSource.getConnection()){
            DatabaseMetaData meta = conn.getMetaData();
            try(ResultSet rs = meta.getColumns(null, null , tableName, null)){
                while (rs.next()){
                    String colName = rs.getString("COLUMN_NAME").toLowerCase();
                    String colType = rs.getString("TYPE_NAME");
                    boolean notNull = "NO".equalsIgnoreCase(rs.getString("IS_NULLABLE"));

                    columnMetaMap.put(colName, new userTableMgr.ColumnMeta(colType, notNull));
                    existingCols.add(colName);
                }
            }

        } catch(SQLException e){
            throw new RuntimeException("Error fetching column metadata: " + e.getMessage(), e);
        }
        return columnMetaMap;
    }

    public Result buildAndExecuteAlterSQL(
            String tableName,
            Set<String> existingCols,
            Map<String, userTableMgr.ColumnMeta> existingMeta,
            List<CreateTable.ColumnDefinition> newColumns
    ) {
        Result res = new Result();
        List<String> alterStatements = new ArrayList<>();

        // 1. Build new column names set manually
        Set<String> newColNames = new HashSet<>();
        for (int i = 0; i < newColumns.size(); i++) {
            newColNames.add(newColumns.get(i).getName().toLowerCase());
        }

        // 2. Drop columns not in newColNames
        for (String existing : existingCols) {
            if (!newColNames.contains(existing)) {
                alterStatements.add("DROP COLUMN " + existing);
            }
        }

        // 3. Handle add and alter
        for (int i = 0; i < newColumns.size(); i++) {
            CreateTable.ColumnDefinition col = newColumns.get(i);
            String name = col.getName().toLowerCase();
            String type = col.getType();
            boolean notNull = col.isNotNull();

            // ✅ SKIP altering PK column
            if (col.isPrimaryKey()) {
                System.out.println("Skipping PK column: " + name);
                continue;
            }

            if (!existingCols.contains(name)) {
                String addStmt = "ADD COLUMN " + name + " " + type;
                if (notNull) {
                    addStmt += " NOT NULL";
                }
                alterStatements.add(addStmt);
            } else {
                userTableMgr.ColumnMeta oldMeta = existingMeta.get(name);

                if (!oldMeta.type.equalsIgnoreCase(type)) {
                    alterStatements.add("ALTER COLUMN " + name + " TYPE " + type);
                }

                if (oldMeta.notNull != notNull) {
                    if (notNull) {
                        alterStatements.add("ALTER COLUMN " + name + " SET NOT NULL");
                    } else {
                        alterStatements.add("ALTER COLUMN " + name + " DROP NOT NULL");
                    }
                }
            }
        }

        // 4. Execute if needed
        if (alterStatements.isEmpty()) {
            res.setMsgCode("NO_CHANGES");
            res.setState(true);
            res.setMsgDesc("No changes detected for table: " + tableName);
            return res;
        }

        String sql = "ALTER TABLE " + tableName + " " + String.join(", ", alterStatements);
        System.out.println("Executing SQL: ALTER TABLE " + tableName + " " + String.join(", ", alterStatements));

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            res.setState(true);
            res.setMsgDesc("Table '" + tableName + "' updated successfully.");

        } catch (SQLException e) {
            res.setState(false);
            res.setMsgCode("500");
            res.setMsgDesc("Failed to update table: " + e.getMessage());
        }

        return res;
    }

}
