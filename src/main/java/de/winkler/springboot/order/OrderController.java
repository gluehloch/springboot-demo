package de.winkler.springboot.order;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import de.winkler.springboot.security.AWUserDetails;

@RestController("/order")
public class OrderController {

    @PutMapping
    public String order(@AuthenticationPrincipal AWUserDetails customUser, String orderNr) {
        System.out.println("Order Nr: " + orderNr);
        return orderNr;
    }

}
