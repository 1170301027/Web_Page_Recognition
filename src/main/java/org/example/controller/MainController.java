package org.example.controller;

import jdk.net.SocketFlow;
import org.example.result.Data;
import org.example.result.RestResult;
import org.example.result.Result;
import org.example.service.ServiceImpl;
import org.example.sql.mapper.BaseMapper;
import org.example.sql.mapper.MatchMapper;
import org.example.sql.pojo.Fingerprint;
import org.example.sql.pojo.InvertedIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @CLassname MainController
 * @Description TODO
 * @Date 2021/5/27 11:06
 * @Created by lenovo
 */
@RestController
@RequestMapping("/")
@CrossOrigin(origins = {"*","null"})
public class MainController {
    @Autowired
    private ServiceImpl service = null;

    @GetMapping(value = "")
    public Result<RestResult> index() {
        Result<RestResult> result = new Result<>();
        Data<RestResult> data = new Data<>();
        int page_id = 100;
        RestResult rest = this.service.details(page_id);
        result.setStatus(SocketFlow.Status.OK);
        data.setObject(rest);
        result.setData(data);
        return result;
    }
}
