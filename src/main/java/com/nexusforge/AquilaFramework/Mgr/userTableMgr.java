package com.nexusforge.AquilaFramework.Mgr;

import com.nexusforge.AquilaFramework.Dao.userTableDao;
import com.nexusforge.AquilaFramework.Dto.TableDetailsDto;
import com.nexusforge.AquilaFramework.Entity.CreateTable;
import com.nexusforge.AquilaFramework.Entity.Result;
import com.nexusforge.AquilaFramework.Util.serverUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class userTableMgr {

    @Autowired
    private serverUtil serverUtil;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private userTableDao userTableDao;

    public Result createNewTable(CreateTable requestData) {
        Result res = new Result();
        try {
            String tableName = requestData.getTableName();
            serverUtil.checkIllegalArgument(tableName);
            List<String> columnDefs = new ArrayList<>();
            List<String> primaryKeys = new ArrayList<>();

            if(!doesTableAlreadyExist(tableName)){
                for (CreateTable.ColumnDefinition col : requestData.getColumns()) {
                    String columnName = col.getName();
                    String columnType = col.getType();
                    String columnLine = columnName + " " + columnType;

                    // Validate column name
                    if (serverUtil.checkIllegalArgument(columnName)) {
                        throw new IllegalArgumentException("Invalid column name: " + columnName);
                    }

                    if (col.isNotNull()) {
                        columnLine += " NOT NULL";
                    }
                    columnDefs.add(columnLine);

                    if (col.isPrimaryKey()) {
                        primaryKeys.add(columnName);
                    }
                }
                res = userTableDao.createNewTableDao(tableName , columnDefs , primaryKeys );
            } else {
                res.setState(false);
                res.setMsgCode("500");
                res.setMsgDesc("Table '" + tableName + "' already exists.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public boolean doesTableAlreadyExist(String tableName) {
        boolean state = false;
        String sql = "SELECT EXISTS (" +
                "SELECT 1 FROM information_schema.tables " +
                "WHERE table_schema = 'public' AND table_name = ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tableName.toLowerCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                state = rs.getBoolean("exists");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking table existence", e);
        }
        return state;
    }

    
    public List<String> getAllTables() {
        String sql = "SELECT table_name\n" +
                "FROM information_schema.tables\n" +
                "WHERE table_schema = 'public'\n" +
                "  AND table_name NOT IN ('uvm001', 'password_verify_token');";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public Result dropTable(String tableName) {
        Result res = new Result();
        if(doesTableAlreadyExist(tableName)){
            res = userTableDao.dropTableDao(tableName);
        } else {
            res.setState(false);
            res.setMsgCode("500");
            res.setMsgDesc("Failed Dropping Table "+ tableName);
        }
        return res;
    }

    public TableDetailsDto getTableDetailsData(String tableName) {
        TableDetailsDto tableData = new TableDetailsDto();
        try {
            tableData = userTableDao.getTableDataDao(tableName);
        } catch(Exception e){
            e.printStackTrace();
        }
        return tableData;
    }

    public Result updateTable(CreateTable createTable) {
        Result res = new Result();

        return res;
    }
}
