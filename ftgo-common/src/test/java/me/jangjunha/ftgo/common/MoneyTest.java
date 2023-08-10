package me.jangjunha.ftgo.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyTest {

    private Money m1 = new Money(10);
    private Money m2 = new Money("20.5");

    @Test
    public void shouldAdd() {
        assertEquals(new Money("30.5"), m1.add(m2));
    }

    @Test
    public void shouldMultiply() {
        assertEquals(new Money("61.5"), m2.multiply(3));
    }

    @Test
    public void shouldCompare() {
        assertTrue(new Money(50).isGreaterThanOrEqual(new Money(50)));
        assertTrue(new Money(50).isGreaterThanOrEqual(new Money(10)));
        assertFalse(new Money(50).isGreaterThanOrEqual(new Money(100)));
    }
}
