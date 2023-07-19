package me.jangjunha.ftgo.restaurant_service.api;

import java.util.List;
import java.util.UUID;

public class Restaurant {
    private UUID id;
    private String name;
    private List<MenuItem> menuItems;

    protected Restaurant() {}

    public Restaurant(UUID id, String name, List<MenuItem> menuItems) {
        this.id = id;
        this.name = name;
        this.menuItems = menuItems;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
