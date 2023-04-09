package me.jangjunha.ftgo.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyTest {
    @Test
    public void shouldCompare() {
        assertTrue(new Money(50).isGreaterThanOrEqual(new Money(50)));
        assertTrue(new Money(50).isGreaterThanOrEqual(new Money(10)));
        assertFalse(new Money(50).isGreaterThanOrEqual(new Money(100)));
    }
}
