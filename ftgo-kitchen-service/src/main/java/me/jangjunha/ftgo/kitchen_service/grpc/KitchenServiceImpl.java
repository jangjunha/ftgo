package me.jangjunha.ftgo.kitchen_service.grpc;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import me.jangjunha.ftgo.kitchen_service.api.GetTicketPayload;
import me.jangjunha.ftgo.kitchen_service.api.KitchenServiceGrpc;
import me.jangjunha.ftgo.kitchen_service.api.Ticket;
import me.jangjunha.ftgo.kitchen_service.domain.TicketLineItem;
import me.jangjunha.ftgo.kitchen_service.domain.TicketNotFoundException;
import me.jangjunha.ftgo.kitchen_service.service.KitchenService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

public class KitchenServiceImpl extends KitchenServiceGrpc.KitchenServiceImplBase {

    private final KitchenService kitchenService;

    @Autowired
    public KitchenServiceImpl(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    @Override
    public void getTicket(GetTicketPayload request, StreamObserver<Ticket> responseObserver) {
        UUID ticketId = UUID.fromString(request.getTicketId());
        me.jangjunha.ftgo.kitchen_service.domain.Ticket ticket;
        try {
            ticket = kitchenService.getTicket(ticketId);
        } catch (TicketNotFoundException e) {
            responseObserver.onError(e);
            return;
        }

        Ticket.Builder builder = Ticket.newBuilder()
                .setId(ticket.getId().toString())
                .setState(ticket.getState().toAPI())
                .addAllLineItems(ticket.getLineItems().stream().map(TicketLineItem::toAPI).collect(Collectors.toList()))
                .setRestaurantId(ticket.getRestaurantId().toString());
        if (ticket.getSequence() != null) {
            builder.setSequence(ticket.getSequence().intValue());
        }
        if (ticket.getReadyBy() != null) {
            builder.setReadyBy(toTimestamp(ticket.getReadyBy()));
        }
        if (ticket.getAcceptTime() != null) {
            builder.setAcceptTime(toTimestamp(ticket.getAcceptTime()));
        }
        if (ticket.getPreparingTime() != null) {
            builder.setPreparingTime(toTimestamp(ticket.getPreparingTime()));
        }
        if (ticket.getPickedUpTime() != null) {
            builder.setPickedUpTime(toTimestamp(ticket.getPickedUpTime()));
        }
        if (ticket.getReadyForPickupTime() != null) {
            builder.setReadyForPickupTime(toTimestamp(ticket.getReadyForPickupTime()));
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    private static Timestamp toTimestamp(OffsetDateTime dt) {
        Instant instant = dt.toInstant();
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}
