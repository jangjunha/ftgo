package me.jangjunha.ftgo.kitchen_service;

import me.jangjunha.ftgo.kitchen_service.domain.Ticket;
import me.jangjunha.ftgo.kitchen_service.domain.TicketLineItem;
import me.jangjunha.ftgo.kitchen_service.domain.TicketState;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.UUID;

public class KitchenFixtures {
    public static final OffsetDateTime NOW = OffsetDateTime.parse("2023-01-01T00:00+09:00");
    public static final UUID A_CAFE_ID = UUID.fromString("040a7dc8-a8bc-45b6-8346-1673315985e1");
    public static final UUID SUBWAY_ID = UUID.fromString("e1902bf6-861b-4051-98be-20e4f496f726");
    public static Ticket makeTicket(UUID id, TicketState state) {
        Ticket t = new Ticket();
        t.setId(id);
        t.setState(state);
        t.setSequence(101L);
        t.setRestaurantId(A_CAFE_ID);
        t.setLineItems(Arrays.asList(
                new TicketLineItem(2, "latte", "Cafe Latte"),
                new TicketLineItem(1, "americano", "Americano")
        ));
        return t;
    }
}
