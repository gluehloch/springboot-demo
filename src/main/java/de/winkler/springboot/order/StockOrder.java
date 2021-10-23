package de.winkler.springboot.order;

import de.winkler.springboot.user.Nickname;

public record StockOrder(Nickname nickname, ISIN isin, OrderQuantity orderQuantity) {
}
