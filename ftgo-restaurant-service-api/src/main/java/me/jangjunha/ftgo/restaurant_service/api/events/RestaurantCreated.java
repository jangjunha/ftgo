package me.jangjunha.ftgo.restaurant_service.api.events;

import me.jangjunha.ftgo.restaurant_service.api.MenuItem;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.List;

public class RestaurantCreated implements RestaurantDomainEvent {
    private String name;
    private String address;
    private List<MenuItem> menuItems;

    public RestaurantCreated() {
    }

    public RestaurantCreated(String name, String address, List<MenuItem> menuItems) {
        if (menuItems == null) {
            throw new NullPointerException("Null menuItems");
        }

        this.name = name;
        this.address = address;
        this.menuItems = menuItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
