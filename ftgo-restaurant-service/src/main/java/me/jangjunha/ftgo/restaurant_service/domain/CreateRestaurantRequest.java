package me.jangjunha.ftgo.restaurant_service.domain;

import java.util.List;

public class CreateRestaurantRequest {
    public String name;

    public String address;
    
    public List<MenuItem> menuItems;
}
