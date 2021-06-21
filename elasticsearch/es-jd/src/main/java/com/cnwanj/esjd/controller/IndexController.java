package com.cnwanj.esjd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: cnwanj
 * @date: 2021-06-14 11:06:58
 * @version: 1.0
 * @desc:
 */
@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
