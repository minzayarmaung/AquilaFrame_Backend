package com.nexusforge.AquilaFramework.Dao;

import com.nexusforge.AquilaFramework.Controller.NotificationWebSocketController;
import com.nexusforge.AquilaFramework.Dto.ColumnDto;
import com.nexusforge.AquilaFramework.Dto.TableDetailsDto;
import com.nexusforge.AquilaFramework.Entity.CreateTable;
import com.nexusforge.AquilaFramework.Entity.Result;
import com.nexusforge.AquilaFramework.Mgr.UserTableMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Service
public class UserTableDao {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private NotificationWebSocketController wsController;

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
            wsController.sendNotification("Table '" + tableName + "' created successfully.");
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

    public Map<String, UserTableMgr.ColumnMeta> getExistingColumnsFromDB(String tableName, Set<String> existingCols) {
        Map<String , UserTableMgr.ColumnMeta> columnMetaMap = new HashMap<>();

        try(Connection conn = dataSource.getConnection()){
            DatabaseMetaData meta = conn.getMetaData();
            try(ResultSet rs = meta.getColumns(null, null , tableName, null)){
                while (rs.next()){
                    String colName = rs.getString("COLUMN_NAME").toLowerCase();
                    String colType = rs.getString("TYPE_NAME");
                    boolean notNull = "NO".equalsIgnoreCase(rs.getString("IS_NULLABLE"));

                    columnMetaMap.put(colName, new UserTableMgr.ColumnMeta(colType, notNull));
                    existingCols.add(colName);
                }
            }

        } catch(SQLException e){
            throw new RuntimeException("Error fetching column metadata: " + e.getMessage(), e);
        }
        return columnMetaMap;
    }

    public Result updateTableDao(
            String tableName,
            Set<String> existingCols,
            Map<String, UserTableMgr.ColumnMeta> existingMeta,
            List<CreateTable.ColumnDefinition> newColumns
    ) {
        Result res = new Result();
        List<String> alterStatements = new ArrayList<>();

        // Lowercase for consistent matching
        Set<String> newColNames = new HashSet<>();
        for (CreateTable.ColumnDefinition col : newColumns) {
            newColNames.add(col.getName().toLowerCase());
        }

        // 1. Drop columns not present in newColumns and NOT primary key
        for (String existing : existingCols) {
            boolean isStillPresent = newColNames.contains(existing);
            boolean isPK = false;

            for (CreateTable.ColumnDefinition col : newColumns) {
                if (col.getName().equalsIgnoreCase(existing) && col.isPrimaryKey()) {
                    isPK = true;
                    break;
                }
            }

            if (!isStillPresent && !isPK) {
                alterStatements.add("DROP COLUMN \"" + existing + "\"");
            }
        }

        // 2. Add or Alter columns
        for (CreateTable.ColumnDefinition col : newColumns) {
            String name = col.getName().toLowerCase();
            String quotedName = "\"" + name + "\"";
            String type = col.getType();
            boolean notNull = col.isNotNull();

            if (col.isPrimaryKey()) {
                System.out.println("Skipping PK column: " + name);
                continue; // Don’t alter PK columns
            }

            if (!existingCols.contains(name)) {
                String addStmt = "ADD COLUMN " + quotedName + " " + type;
                if (notNull) addStmt += " NOT NULL";
                alterStatements.add(addStmt);
            } else {
                UserTableMgr.ColumnMeta oldMeta = existingMeta.get(name);
                if (oldMeta == null) continue;

                if (!oldMeta.type.equalsIgnoreCase(type)) {
                    alterStatements.add("ALTER COLUMN " + quotedName + " TYPE " + type);
                }

                if (oldMeta.notNull != notNull) {
                    alterStatements.add("ALTER COLUMN " + quotedName + (notNull ? " SET" : " DROP") + " NOT NULL");
                }
            }
        }

        // 3. Execute
        if (alterStatements.isEmpty()) {
            res.setState(true);
            res.setMsgCode("NO_CHANGES");
            res.setMsgDesc("No changes detected for table: " + tableName);
            return res;
        }

        String sql = "ALTER TABLE \"" + tableName + "\" " + String.join(", ", alterStatements);
        System.out.println("Executing SQL: " + sql);

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
        wsController.sendNotification("Table '" + tableName + "' updated successfully.");
        return res;
    }


}
