package com.nexusforge.OSMS.Controller;

import com.nexusforge.OSMS.Entity.CreateTable;
import com.nexusforge.OSMS.Entity.Result;
import com.nexusforge.OSMS.Mgr.createTableMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/tableController")
public class tableController {

    @Autowired
    private createTableMgr createTableMgr;

    @RequestMapping("/createTable")
    public Result createNewTable(@RequestBody CreateTable createTable){
        Result res = new Result();
        try {
            res = createTableMgr.createNewTable(createTable);

            res.setState(true);
            res.setMsgCode("200");
            res.setMsgDesc("Table created successfully.");
        } catch (Exception e) {
            res.setState(false);
            res.setMsgCode("500");
            res.setMsgDesc("Failed to create table: " + e.getMessage());
        }
        return res;
    }
}
