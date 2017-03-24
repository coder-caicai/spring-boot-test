package com.hbc.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by cheng on 16/9/13.
 */

@Controller
@RequestMapping("/")
public class ShowController {

    @RequestMapping("")
    public String index(){
        return "index";
    }

}
