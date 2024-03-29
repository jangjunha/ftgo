package me.jangjunha.ftgo.restaurant_service.domain;

import jakarta.persistence.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "restaurants")
@Access(AccessType.FIELD)
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String address;

    @ElementCollection
    @CollectionTable(name = "restaurant_menu_items")
    private List<MenuItem> menuItems;

    public Restaurant() {
    }

    public Restaurant(UUID id, String name, String address, List<MenuItem> menuItems) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.menuItems = menuItems;
    }

    public Restaurant(String name, String address, List<MenuItem> menuItems) {
        this.name = name;
        this.address = address;
        this.menuItems = menuItems;
    }

    public Optional<MenuItem> findMenuItem(String menuItemId) {
        return menuItems.stream().filter(e -> e.getId().equals(menuItemId)).findFirst();
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
}

