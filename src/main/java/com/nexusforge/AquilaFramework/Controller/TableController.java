package com.nexusforge.AquilaFramework.controller;

import com.nexusforge.AquilaFramework.dto.TableDetailsDto;
import com.nexusforge.AquilaFramework.entity.CreateTable;
import com.nexusforge.AquilaFramework.entity.Result;
import com.nexusforge.AquilaFramework.Mgr.UserTableMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tableController")
public class TableController {

    @Autowired
    private UserTableMgr userTableMgr;

    @PostMapping("/createTable")
    public Result createNewTable(@RequestBody CreateTable createTable) {
        Result res = new Result();
        try {
            if(createTable.isUpdate()){
                res = userTableMgr.updateTable(createTable);
            } else {
                res = userTableMgr.createNewTable(createTable);
            }
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
            return userTableMgr.getAllTables();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList(); // Return empty list on error
        }
    }

    @PostMapping("/deleteTable")
    public Result deleteTable(@RequestBody Map<String , String> body){
        Result res = new Result();
        String tableName = body.get("tableName");
        res = userTableMgr.dropTable(tableName);
        return res;
    }

    @GetMapping("/getTableDetails")
    public TableDetailsDto getTableDetails(@RequestParam String name){
        return userTableMgr.getTableDetailsData(name);
    }
}


