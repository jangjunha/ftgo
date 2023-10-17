package me.jangjunha.ftgo.kitchen_service.grpc;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import me.jangjunha.ftgo.common.UnsupportedStateTransitionException;
import me.jangjunha.ftgo.common.auth.*;
import me.jangjunha.ftgo.common.protobuf.TimestampUtils;
import me.jangjunha.ftgo.kitchen_service.api.*;
import me.jangjunha.ftgo.kitchen_service.domain.AlreadyAcceptedException;
import me.jangjunha.ftgo.kitchen_service.domain.InvalidParameterException;
import me.jangjunha.ftgo.kitchen_service.domain.TicketNotFoundException;
import me.jangjunha.ftgo.kitchen_service.service.KitchenService;
import me.jangjunha.ftgo.order_service.api.GetOrderPayload;
import me.jangjunha.ftgo.order_service.api.Order;
import me.jangjunha.ftgo.order_service.api.OrderServiceGrpc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.UUID;

public class KitchenServiceImpl extends KitchenServiceGrpc.KitchenServiceImplBase {

    private final KitchenService kitchenService;

    private final OrderServiceGrpc.OrderServiceBlockingStub orderService;

    @Autowired
    public KitchenServiceImpl(KitchenService kitchenService, OrderServiceGrpc.OrderServiceBlockingStub orderService) {
        this.kitchenService = kitchenService;
        this.orderService = orderService;
    }

    @Override
    public void listTickets(ListTicketPayload request, StreamObserver<ListTicketResponse> responseObserver) {
        UUID restaurantId = UUID.fromString(request.getRestaurantId());

        AuthenticatedID authenticatedID = AuthInterceptor.getAUTHENTICATED_ID().get();
        if (authenticatedID instanceof AuthenticatedClient) {
        } else if (authenticatedID instanceof AuthenticatedRestaurantID) {
            if (!((AuthenticatedRestaurantID) authenticatedID).getRestaurantId().equals(restaurantId)) {
                responseObserver.onError(new StatusRuntimeException(Status.PERMISSION_DENIED));
                return;
            }
        } else if (authenticatedID instanceof AuthenticatedConsumerID) {
            responseObserver.onError(new StatusRuntimeException(Status.PERMISSION_DENIED));
            return;
        } else {
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL));
            return;
        }

        try {
            responseObserver.onNext(
                    ListTicketResponse.newBuilder()
                            .addAllEdges(
                                    kitchenService.listTickets(restaurantId,
                                            request.getFirst(),
                                            request.getAfter(),
                                            request.getLast(),
                                            request.getBefore()
                                    ).stream().map(edge ->
                                            TicketEdge.newBuilder()
                                                    .setNode(edge.getNode().toAPI())
                                                    .setCursor(edge.getCursor())
                                                    .build()
                                    ).toList()
                            )
                            .build()
            );
        } catch (InvalidParameterException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withCause(e).withDescription(e.getMessage()).asRuntimeException());
            return;
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getTicket(GetTicketPayload request, StreamObserver<Ticket> responseObserver) {
        UUID ticketId = UUID.fromString(request.getTicketId());
        AuthenticatedID authenticatedId = AuthInterceptor.getAUTHENTICATED_ID().get();
        if (!hasPermission(ticketId, authenticatedId, true)) {
            responseObserver.onError(new StatusRuntimeException(Status.PERMISSION_DENIED));
            return;
        }

        me.jangjunha.ftgo.kitchen_service.domain.Ticket ticket;
        try {
            ticket = kitchenService.getTicket(ticketId);
        } catch (TicketNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND.withCause(e).withDescription("ticket %s not found".formatted(ticketId)).asRuntimeException());
            return;
        }

        responseObserver.onNext(ticket.toAPI());
        responseObserver.onCompleted();
    }

    @Override
    public void acceptTicket(AcceptTicketPayload request, StreamObserver<Empty> responseObserver) {
        UUID ticketId = UUID.fromString(request.getTicketId());
        AuthenticatedID authenticatedId = AuthInterceptor.getAUTHENTICATED_ID().get();
        if (!hasPermission(ticketId, authenticatedId, false)) {
            responseObserver.onError(new StatusRuntimeException(Status.PERMISSION_DENIED));
            return;
        }

        OffsetDateTime readyBy = TimestampUtils.fromTimestamp(request.getReadyBy());

        try {
            kitchenService.accept(ticketId, readyBy);
        } catch (TicketNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND.withCause(e).withDescription("ticket %s not found".formatted(ticketId)).asRuntimeException());
            return;
        } catch (AlreadyAcceptedException e) {
            responseObserver.onError(Status.ALREADY_EXISTS.asRuntimeException());
            return;
        } catch (UnsupportedStateTransitionException e) {
            responseObserver.onError(Status.FAILED_PRECONDITION.asRuntimeException());
            return;
        }

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void preparingTicket(PreparingTicketPayload request, StreamObserver<Ticket> responseObserver) {
        UUID ticketId = UUID.fromString(request.getTicketId());
        AuthenticatedID authenticatedId = AuthInterceptor.getAUTHENTICATED_ID().get();
        if (!hasPermission(ticketId, authenticatedId, true)) {
            responseObserver.onError(new StatusRuntimeException(Status.PERMISSION_DENIED));
            return;
        }

        try {
            Ticket ticket = kitchenService.preparingTicket(ticketId).toAPI();
            responseObserver.onNext(ticket);
            responseObserver.onCompleted();
        } catch (TicketNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND.withCause(e).withDescription("ticket %s not found".formatted(ticketId)).asRuntimeException());
        } catch (UnsupportedStateTransitionException e) {
            responseObserver.onError(Status.FAILED_PRECONDITION.asRuntimeException());
        }
    }

    @Override
    public void readyForPickupTicket(ReadyForPickupTicketPayload request, StreamObserver<Ticket> responseObserver) {
        UUID ticketId = UUID.fromString(request.getTicketId());
        AuthenticatedID authenticatedId = AuthInterceptor.getAUTHENTICATED_ID().get();
        if (!hasPermission(ticketId, authenticatedId, true)) {
            responseObserver.onError(new StatusRuntimeException(Status.PERMISSION_DENIED));
            return;
        }

        try {
            Ticket ticket = kitchenService.readyForPickupTicket(ticketId).toAPI();
            responseObserver.onNext(ticket);
            responseObserver.onCompleted();
        } catch (TicketNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND.withCause(e).withDescription("ticket %s not found".formatted(ticketId)).asRuntimeException());
        } catch (UnsupportedStateTransitionException e) {
            responseObserver.onError(Status.FAILED_PRECONDITION.asRuntimeException());
        }
    }

    private boolean hasPermission(UUID ticketId, AuthenticatedID id, boolean isRead) {
        if (id == null) {
            return false;
        } else if (id instanceof AuthenticatedClient) {
            return true;
        } else if (id instanceof AuthenticatedRestaurantID) {
            me.jangjunha.ftgo.kitchen_service.domain.Ticket ticket;
            try {
                ticket = kitchenService.getTicket(ticketId);
            } catch (TicketNotFoundException e) {
                return false;
            }
            return ((AuthenticatedRestaurantID) id).getRestaurantId().equals(ticket.getRestaurantId());
        } else if (id instanceof AuthenticatedConsumerID) {
            if (isRead) {
                Order order = orderService
                        .withCallCredentials(new ClientCallCredentials())
                        .getOrder(
                                GetOrderPayload.newBuilder()
                                        .setId(ticketId.toString())
                                        .build()
                        );
                UUID consumerId = UUID.fromString(order.getConsumerId());
                return ((AuthenticatedConsumerID) id).getConsumerId().equals(consumerId);
            } else {
                return false;
            }
        }
        return false;
    }
}
