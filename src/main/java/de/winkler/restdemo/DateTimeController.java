package de.winkler.restdemo;

import java.util.Date;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DateTimeController {

    @GetMapping("/ping")
    public PingDateTime ping() {
        PingDateTime pdt = new PingDateTime();
        pdt.setDateTime(new Date());
        return pdt;
    }

}
