package de.winkler.springboot.order;

import de.winkler.springboot.security.AWUserDetails;
import de.winkler.springboot.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @CrossOrigin
    @PostMapping("/order")
    // @PreAuthorize()
    public String order(@AuthenticationPrincipal Object customUser, @RequestParam String orderNr) {
        Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // TODO: Remove me. Want to find out, what type customUser is.
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        AWUserDetails userDetails = (AWUserDetails) authentication.getDetails();

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        System.out.println(String.format("Order Nr: %s, %s", orderNr, authentication.getName()).toString());
        return "{'orderNr': 4711}";
    }

    /*
    @Transactional(readOnly = true)
    public Page<OrderItem> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    */

}
