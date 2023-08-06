package me.jangjunha.ftgo.kitchen_service.api;

import java.util.List;

public class TicketDetails {
    private List<LineItem> lineItems;

    public TicketDetails() {}

    public TicketDetails(List<LineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<LineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public static class LineItem {
        private int quantity;
        private String menuItemId;
        private String name;

        protected LineItem() {
        }

        public LineItem(int quantity, String menuItemId, String name) {
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
}
