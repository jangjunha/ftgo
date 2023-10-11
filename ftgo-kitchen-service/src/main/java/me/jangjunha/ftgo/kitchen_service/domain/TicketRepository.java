package me.jangjunha.ftgo.kitchen_service.domain;

import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface TicketRepository extends CrudRepository<Ticket, UUID>, SequencedTicketRepository, CustomTicketRepository {
}

interface SequencedTicketRepository {
    long getLastSequence(UUID restaurantId);
}

interface CustomTicketRepository {
    List<Ticket> findAllByFilter(UUID restaurantId, Long afterSequence, Long beforeSequence, int limit);
}

class TicketRepositoryImpl extends QuerydslRepositorySupport implements SequencedTicketRepository, CustomTicketRepository {
    public TicketRepositoryImpl() {
        super(Ticket.class);
    }

    @Override
    public List<Ticket> findAllByFilter(UUID restaurantId, Long afterSequence, Long beforeSequence, int limit) {
        QTicket ticket = QTicket.ticket;
        JPQLQuery<Ticket> query = from(ticket)
                .where(ticket.restaurantId.eq(restaurantId))
                .orderBy(ticket.sequence.desc());

        if (afterSequence != null) {
            query = query.where(ticket.sequence.lt(afterSequence));
        }
        if (beforeSequence != null) {
            query = query.where(ticket.sequence.gt(beforeSequence));
        }

        return query.limit(limit).fetch();
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
