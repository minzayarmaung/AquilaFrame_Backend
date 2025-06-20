package com.nexusforge.AquilaFramework.Dao;

import com.nexusforge.AquilaFramework.Entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Service
public class createTableDao {

    @Autowired
    private DataSource dataSource;

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

}
