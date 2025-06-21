package com.nexusforge.AquilaFramework.Dao;

import com.nexusforge.AquilaFramework.Dto.ColumnDto;
import com.nexusforge.AquilaFramework.Dto.TableDetailsDto;
import com.nexusforge.AquilaFramework.Entity.Result;
import jakarta.persistence.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

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
}
