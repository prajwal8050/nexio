package com.example.nexio.repository;

import com.example.nexio.model.QueueTicket;
import com.example.nexio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<QueueTicket, Long> {
    List<QueueTicket> findByUserOrderByBookingDateDesc(User user);

    List<QueueTicket> findByServiceIdAndStatus(Long serviceId, String status);

    List<QueueTicket> findByServiceIdAndStatusOrderByBookingDateDesc(Long serviceId, String status);

    List<QueueTicket> findByServiceId(Long serviceId);
}
