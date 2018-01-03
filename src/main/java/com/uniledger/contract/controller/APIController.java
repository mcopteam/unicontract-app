package com.uniledger.contract.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by wxcsdb88 on 2017/5/27 15:40.
 */
@ApiIgnore
@Controller
public class APIController {
    @RequestMapping(value = "/api-docs")
    public String apiDocs() {
        System.out.println("swagger-ui.html");
        return "redirect:swagger-ui.html";
    }
}
