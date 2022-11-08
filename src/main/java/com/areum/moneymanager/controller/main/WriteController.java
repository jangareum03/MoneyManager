package com.areum.moneymanager.controller.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/write")
public class WriteController {

    @GetMapping
    public String getWriteStep1View(){
        return "/main/write";
    }

}
