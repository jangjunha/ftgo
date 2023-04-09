package me.jangjunha.ftgo.kitchen_service.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface RestaurantRepository extends CrudRepository<Restaurant, UUID> {
}
