package org.example.controller;

import jdk.net.SocketFlow;
import org.example.result.Data;
import org.example.result.RestResult;
import org.example.result.Result;
import org.example.service.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping(value = "/delete")
    public String delete() {
        this.service.delete();
        return "success";
    }

    @GetMapping(value = "index")
    public String index(Model model) {
        model.addAttribute("key","something wrong?");
        return "index";
    }
}
