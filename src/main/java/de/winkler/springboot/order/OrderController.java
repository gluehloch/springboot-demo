package de.winkler.springboot.order;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import de.winkler.springboot.security.AWUserDetails;

@RestController
public class OrderController {

    @CrossOrigin
    @PutMapping("/order")
    public String order(@AuthenticationPrincipal Object customUser, String orderNr) {

        // TODO: Remove me. Want to find out, what type customUser is.
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        System.out.println(String.format("Order Nr: %s, %s", orderNr, authentication.getName()).toString());
        return orderNr;
    }

}
