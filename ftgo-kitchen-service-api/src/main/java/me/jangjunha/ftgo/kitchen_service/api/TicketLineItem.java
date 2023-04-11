package me.jangjunha.ftgo.kitchen_service.api;

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
