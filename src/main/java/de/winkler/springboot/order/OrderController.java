package de.winkler.springboot.order;

import de.winkler.springboot.user.Nickname;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @CrossOrigin
    @PostMapping("/order")
    @RolesAllowed("ROLE_USER")
    //@PreAuthorize("#nickname == authentication.name")
    public ResponseEntity<OrderBasketJson> order(@AuthenticationPrincipal Object customUser, @RequestParam String wkn) {
        Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // TODO: Remove me. Want to find out, what type customUser is.
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        // TODO Die Abfrage liefert 'null' zurueck.
        // AWUserDetails userDetails = (AWUserDetails) authentication.getDetails();

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        if (token == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // TODO Das sollte jetzt nicht so sein, dass der Controller die Entities holt und dem Service uebergibt.
        // Oder ist das in Ordnung??? Es kommt in jedem Fall ein *Entity Objekt zurueck. Der Controller uebersetzt
        // das Entity Objekt in ein *Json Objekt.
        Nickname nickname = Nickname.of(token.getName());

        ISIN isin = ISIN.of(wkn);

        OrderBasketEntity newBasket = orderService.createNewBasket(nickname, isin, 1);// TODO quantity = 0?

        String string = String.format("Order Nr: %s, %s, %s", wkn, customUser, authentication.getName());
        logger.info(string);
        System.out.println(string);

        // TODO Entity to JSON .... how annoying ...

        OrderBasketJson json = new OrderBasketJson();
        json.setUuid(newBasket.getUuid());
        json.setNickname(nickname);

        return ResponseEntity.ok(json);
    }

    /*
    @Transactional(readOnly = true)
    public Page<OrderItem> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    */

}
