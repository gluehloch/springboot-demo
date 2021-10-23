package de.winkler.springboot.order;

import java.util.ArrayList;
import java.util.List;

public class OrderBasketJson {

    private String uuid;
    private String nickname;
    private List<OrderItemJson> orderItems = new ArrayList<>();

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<OrderItemJson> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemJson> orderItems) {
        this.orderItems = orderItems;
    }

}
