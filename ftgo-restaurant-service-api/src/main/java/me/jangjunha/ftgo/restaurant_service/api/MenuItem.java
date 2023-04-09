package me.jangjunha.ftgo.restaurant_service.api;

import me.jangjunha.ftgo.common.Money;
import org.apache.commons.lang.builder.ToStringBuilder;

public class MenuItem {
    private String id;
    private String name;
    private Money price;

    public MenuItem(String id, String name, Money price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }
}
