package site.binghai.biz.service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.biz.entity.turntable.Ticket;

import java.util.List;

public interface TicketDao extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCreatedBetween(Long start, Long end);

    List<Ticket> findByPlayedAndGameTimeBetween(Boolean played, Long start, Long end);
}
