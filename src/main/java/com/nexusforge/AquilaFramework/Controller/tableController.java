package com.nexusforge.AquilaFramework.Controller;

import com.nexusforge.AquilaFramework.Entity.CreateTable;
import com.nexusforge.AquilaFramework.Entity.Result;
import com.nexusforge.AquilaFramework.Mgr.createTableMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
