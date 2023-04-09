package me.jangjunha.ftgo.kitchen_service.domain;

import jakarta.persistence.*;

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
}
