package me.jangjunha.ftgo.kitchen_service.domain;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface TicketRepository extends CrudRepository<Ticket, UUID>, SequencedTicketRepository {
}

interface SequencedTicketRepository {
    long getLastSequence(UUID restaurantId);
}

class SequencedTicketRepositoryImpl extends QuerydslRepositorySupport implements SequencedTicketRepository {
    public SequencedTicketRepositoryImpl() {
        super(Ticket.class);
    }

    @Override
    public long getLastSequence(UUID restaurantId) {
        QTicket ticket = QTicket.ticket;
        Long latest = from(ticket)
                .where(ticket.restaurantId.eq(restaurantId))
                .orderBy(ticket.sequence.desc())
                .limit(1)
                .select(ticket.sequence)
                .fetchFirst();
        if (latest == null) {
            return 0;
        }
        return latest;
    }
}
