package me.jangjunha.ftgo.kitchen_service.domain;

public class InvalidParameterException extends RuntimeException {

    public InvalidParameterException(String message) {
        super(message);
    }
}
