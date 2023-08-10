package me.jangjunha.ftgo.restaurant_service.api.web;

import me.jangjunha.ftgo.restaurant_service.api.MenuItem;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.List;
import java.util.UUID;

public class GetRestaurantResponse {
    public UUID id;
    public String name;
    public List<MenuItem> menuItems;
    public GetRestaurantResponse(UUID id, String name, List<MenuItem> menuItems) {
        this.id = id;
        this.name = name;
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
