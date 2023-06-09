package me.jangjunha.ftgo.kitchen_service.domain;


import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embeddable;
import me.jangjunha.ftgo.kitchen_service.api.TicketDetails;

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

    public static Stream<TicketLineItem> fromTicketDetails(me.jangjunha.ftgo.kitchen_service.api.TicketDetails details) {
        return details.getLineItems().stream()
                .map(li -> new TicketLineItem(
                        li.getQuantity(),
                        li.getMenuItemId(),
                        li.getName()
                ));
    }

    public static me.jangjunha.ftgo.kitchen_service.api.TicketDetails toTicketDetails(Stream<TicketLineItem> items) {
        return new TicketDetails(items.map(li -> new me.jangjunha.ftgo.kitchen_service.api.TicketLineItem(
            li.getQuantity(),
            li.getMenuItemId(),
            li.getName()
        )).collect(Collectors.toList()));
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
}
