package com.nexusforge.AquilaFramework.Dao;

import com.nexusforge.AquilaFramework.Entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Service
public class createTableDao {

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
}
