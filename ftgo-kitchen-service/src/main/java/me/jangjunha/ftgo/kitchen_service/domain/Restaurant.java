package me.jangjunha.ftgo.kitchen_service.domain;

import jakarta.persistence.*;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "restaurants")
@Access(AccessType.FIELD)
public class Restaurant {
    @Id
    private UUID id;

    @Embedded
    @ElementCollection
    @CollectionTable(name = "restaurant_menu_items")
    private List<MenuItem> menuItems;

    public Restaurant() {
    }

    public Restaurant(UUID id, List<MenuItem> menuItems) {
        this.id = id;
        this.menuItems = menuItems;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
