package de.winkler.springboot.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @CrossOrigin
    @PostMapping("/order")
    @RolesAllowed("ROLE_USER")
    //@PreAuthorize("#nickname == authentication.name")
    public String order(@AuthenticationPrincipal Object customUser, @RequestParam String wkn) {
        Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // TODO: Remove me. Want to find out, what type customUser is.
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        // TODO Die Abfrage liefert 'null' zurueck.
        // AWUserDetails userDetails = (AWUserDetails) authentication.getDetails();

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        System.out.println(String.format("Order Nr: %s, %s, %s, %s", wkn, customUser, authentication.getName()));


        return "{'orderNr': 4711}";
    }

    /*
    @Transactional(readOnly = true)
    public Page<OrderItem> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    */

}
