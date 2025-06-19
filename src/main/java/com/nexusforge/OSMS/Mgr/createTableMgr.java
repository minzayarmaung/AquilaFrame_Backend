package com.nexusforge.OSMS.Mgr;

import com.nexusforge.OSMS.Dao.createTableDao;
import com.nexusforge.OSMS.Entity.CreateTable;
import com.nexusforge.OSMS.Entity.Result;
import com.nexusforge.OSMS.Util.serverUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Service
public class createTableMgr {

    @Autowired
    private serverUtil serverUtil;

    @Autowired
    private createTableDao createTableDao;

    public Result createNewTable(CreateTable requestData) {
        Result res = new Result();
        try {
            String tableName = requestData.getTableName();
            serverUtil.checkIllegalArgument(tableName);
            List<String> columnDefs = new ArrayList<>();
            List<String> primaryKeys = new ArrayList<>();
            for (CreateTable.ColumnDefinition col : requestData.getColumns()) {
                String columnName = col.getName();
                String columnType = col.getType();
                String columnLine = col.getName() + " " + col.getType();

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
            res = createTableDao.createNewTableDao(tableName , columnDefs , primaryKeys );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return res;
    }
}
