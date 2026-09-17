package com.ticket.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticket.demo.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long>{

	long countByEventId(Long eventId);

}
