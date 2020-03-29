package de.winkler.springboot.datetime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DateTimeController {

    private final TimeService timeService;

    public DateTimeController(TimeService timeService) {
        this.timeService = timeService;
    }

    @GetMapping("/ping")
    public PingDateTime ping() {
        PingDateTime pdt = new PingDateTime();
        pdt.setDateTime(TimeService.convertToDateViaInstant(timeService.now()));
        return pdt;
    }

}
