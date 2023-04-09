package me.jangjunha.ftgo.restaurant_service.events;

import me.jangjunha.ftgo.restaurant_service.domain.MenuItem;

import java.util.List;

public class RestaurantCreated implements RestaurantDomainEvent {
    private String name;
    private List<MenuItem> menuItems;

    public RestaurantCreated() {
    }

    public RestaurantCreated(String name, List<MenuItem> menuItems) {
        if (menuItems == null) {
            throw new NullPointerException("Null menuItems");
        }

        this.name = name;
        this.menuItems = menuItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }
}
