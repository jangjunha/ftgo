package me.jangjunha.ftgo.restaurant_service.api.events;

import me.jangjunha.ftgo.restaurant_service.api.MenuItem;

import java.util.List;

public class RestaurantMenuRevised implements RestaurantDomainEvent {
    private List<MenuItem> menuItems;

    public RestaurantMenuRevised() {
    }

    public RestaurantMenuRevised(List<MenuItem> menuItems) {
        if (menuItems == null) {
            throw new NullPointerException("Null menuItems");
        }

        this.menuItems = menuItems;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }
}
