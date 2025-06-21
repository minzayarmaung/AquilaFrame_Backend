package com.nexusforge.AquilaFramework.Controller;

import com.nexusforge.AquilaFramework.Entity.CreateTable;
import com.nexusforge.AquilaFramework.Entity.Result;
import com.nexusforge.AquilaFramework.Mgr.createTableMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tableController")
public class tableController {

    @Autowired
    private createTableMgr createTableMgr;

    @PostMapping("/createTable")
    public Result createNewTable(@RequestBody CreateTable createTable) {
        Result res = new Result();
        try {
            res = createTableMgr.createNewTable(createTable);
        } catch (Exception e) {
            res.setState(false);
            res.setMsgCode("500");
            res.setMsgDesc("Failed to create table: " + e.getMessage());
        }
        return res;
    }

    @GetMapping("/showTables")
    public List<String> getAllTables() {
        try {
            return createTableMgr.getAllTables();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList(); // Return empty list on error
        }
    }

    @PostMapping("/deleteTable")
    public Result deleteTable(@RequestBody Map<String , String> body){
        Result res = new Result();
        String tableName = body.get("tableName");
        res = createTableMgr.dropTable(tableName);
        return res;
    }
}


