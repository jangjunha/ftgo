package me.jangjunha.ftgo.restaurant_service.api.web;

import me.jangjunha.ftgo.restaurant_service.api.MenuItem;

import java.util.List;

public class ReviseMenuRequest {

    private List<MenuItem> menuItems;

    protected ReviseMenuRequest() {
    }

    public ReviseMenuRequest(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }
}
