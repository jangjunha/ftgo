package me.jangjunha.ftgo.restaurant_service.domain;

import jakarta.persistence.*;
import me.jangjunha.ftgo.common.Money;
import org.apache.commons.lang.builder.ToStringBuilder;

@Embeddable
@Access(AccessType.FIELD)
public class MenuItem {
    private String id;
    private String name;
    @Embedded
    @AttributeOverride(name="amount", column = @Column(name="price"))
    private Money price;

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
