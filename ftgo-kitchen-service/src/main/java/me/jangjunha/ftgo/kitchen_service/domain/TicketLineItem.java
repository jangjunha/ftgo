package me.jangjunha.ftgo.kitchen_service.domain;


import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embeddable;

@Embeddable
@Access(AccessType.FIELD)
public class TicketLineItem extends me.jangjunha.ftgo.kitchen_service.api.TicketLineItem {
    public TicketLineItem() {
    }

    public TicketLineItem(int quantity, String menuItemId, String name) {
        super(quantity, menuItemId, name);
    }
}
