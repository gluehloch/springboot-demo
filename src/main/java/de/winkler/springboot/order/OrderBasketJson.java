package de.winkler.springboot.order;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.winkler.springboot.user.Nickname;

public class OrderBasketJson {

    private UUID uuid;
    private Nickname nickname;
    private List<OrderItemJson> orderItems = new ArrayList<>();

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Nickname getNickname() {
        return nickname;
    }

    public void setNickname(Nickname nickname) {
        this.nickname = nickname;
    }

    public List<OrderItemJson> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemJson> orderItems) {
        this.orderItems = orderItems;
    }

}
