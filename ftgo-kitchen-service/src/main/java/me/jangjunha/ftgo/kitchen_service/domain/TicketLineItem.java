package me.jangjunha.ftgo.kitchen_service.domain;


import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embeddable;
import me.jangjunha.ftgo.kitchen_service.api.TicketDetails;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
@Access(AccessType.FIELD)
public class TicketLineItem {

    private int quantity;
    private String menuItemId;
    private String name;

    public TicketLineItem() {
    }

    public TicketLineItem(int quantity, String menuItemId, String name) {
        this.quantity = quantity;
        this.menuItemId = menuItemId;
        this.name = name;
    }

    public me.jangjunha.ftgo.kitchen_service.api.TicketLineItem toAPI() {
        return me.jangjunha.ftgo.kitchen_service.api.TicketLineItem.newBuilder()
                .setQuantity(getQuantity())
                .setMenuItemId(getMenuItemId())
                .setName(getName())
                .build();
    }

    public static Stream<TicketLineItem> fromTicketDetails(me.jangjunha.ftgo.kitchen_service.api.TicketDetails details) {
        return details.getLineItems().stream()
                .map(li -> new TicketLineItem(
                        li.getQuantity(),
                        li.getMenuItemId(),
                        li.getName()
                ));
    }

    public static me.jangjunha.ftgo.kitchen_service.api.TicketDetails toTicketDetails(Stream<TicketLineItem> items) {
        return new TicketDetails(items.map(it -> new TicketDetails.LineItem(it.getQuantity(), it.getMenuItemId(), it.getName())).collect(Collectors.toList()));
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(String menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
