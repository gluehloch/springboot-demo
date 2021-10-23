package de.winkler.springboot.order;

import java.util.UUID;

import de.winkler.springboot.user.Nickname;

public record StockOrder(Nickname nickname, ISIN isin, OrderQuantity orderQuantity, UUID basketID) {
}
