package me.jangjunha.ftgo.common;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.math.BigDecimal;

public class Money {
    public static Money ZERO = new Money(0);

    private BigDecimal amount;

    public Money(BigDecimal amount) {
        this.amount = amount;
    }

    public Money(Integer amount) {
        this.amount = new BigDecimal(amount);
    }

    public Money(String amount) {
        this.amount = new BigDecimal(amount);
    }

    private Money() { }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;
        Money rhs = (Money) o;
        return new EqualsBuilder()
                .append(amount, rhs.amount)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("amount", amount)
                .toString();
    }

    public me.jangjunha.ftgo.common.api.Money toAPI() {
        return me.jangjunha.ftgo.common.api.Money.newBuilder()
                .setAmount(amount.toString())
                .build();
    }

    public Money add(Money delta) {
        return new Money(amount.add(delta.amount));
    }

    public boolean isGreaterThanOrEqual(Money other) {
        return amount.compareTo(other.amount) >= 0;
    }

    public Money multiply(int x) {
        return new Money(amount.multiply(new BigDecimal(x)));
    }
}
