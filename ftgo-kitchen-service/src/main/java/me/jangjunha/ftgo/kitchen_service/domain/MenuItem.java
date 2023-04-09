package me.jangjunha.ftgo.kitchen_service.domain;

import jakarta.persistence.*;
import me.jangjunha.ftgo.common_jpa.Money;

@Embeddable
@Access(AccessType.FIELD)
public class MenuItem {
    private String id;
    private String name;

    @Embedded
    @AttributeOverride(name="amount", column = @Column(name="price"))
    private Money price;

    public MenuItem() {
    }

    public MenuItem(String id, String name, Money price) {
        this.id = id;
        this.name = name;
        this.price = price;
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
