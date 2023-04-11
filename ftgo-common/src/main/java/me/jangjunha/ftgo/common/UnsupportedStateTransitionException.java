package me.jangjunha.ftgo.common;

public class UnsupportedStateTransitionException extends RuntimeException {
    public UnsupportedStateTransitionException(Enum state) {
        super(String.format("current state: %s", state));
    }
}
