package com.baidu.shop.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @ClassName PageController
 * @Description: TODO
 * @Author shenyaqi
 * @Date 2021/3/8
 * @Version V1.0
 **/
@Controller
public class PageController {


    @GetMapping(value = "123.html")
    public String test(){
        return "123";
    }
}
