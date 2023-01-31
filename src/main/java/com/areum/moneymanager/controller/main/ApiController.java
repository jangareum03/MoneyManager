package com.areum.moneymanager.controller.main;

import com.areum.moneymanager.dto.ResServiceDto;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ApiController {

    @PostMapping("/api/faq")
    public List<ResServiceDto.Faq> postFAQ( ) throws IOException, ParseException {
        List<ResServiceDto.Faq> resultList = new ArrayList<>();

        Reader reader = new FileReader("src/main/resources/json/faq.json");

        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(reader);

        for (Object json : jsonArray) {
            JSONObject object = (JSONObject) json;

            String question = (String) object.get("question");
            String answer = (String) object.get("answer");

            ResServiceDto.Faq faq = ResServiceDto.Faq.builder().question(question).answer(answer).build();
            resultList.add(faq);
        }

        return resultList;
    }

}
