package de.winkler.springboot.order;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import de.winkler.springboot.security.AWUserDetails;

@RestController("/order")
public class OrderController {

    @CrossOrigin
    @PutMapping
    public String order(@AuthenticationPrincipal AWUserDetails customUser, String orderNr) {
        System.out.println(String.format("Order Nr: %s, %s", orderNr, customUser).toString());
        return orderNr;
    }

}
