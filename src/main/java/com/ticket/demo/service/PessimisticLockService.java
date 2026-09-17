package com.ticket.demo.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.ticket.demo.exception.TicketSoldOutException;
import com.ticket.demo.model.Event;
import com.ticket.demo.model.Reservation;
import com.ticket.demo.repository.EventRepository;
import com.ticket.demo.repository.ReservationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PessimisticLockService {

	private final EventRepository eventRepository;
	private final ReservationRepository reservationRepository;

	@Transactional
	public Reservation purchaseTicket(Long eventId, String customerName) {

		Event event = eventRepository.findByIdWithPessimisticLock(eventId)
				.orElseThrow(() -> new RuntimeException("Event bulunamadı"));

		if (event.getAvailableTickets() > 0) {

			event.setAvailableTickets(event.getAvailableTickets() - 1);
			eventRepository.save(event);

			Reservation reservation = new Reservation();
			reservation.setEventId(eventId);
			reservation.setCustomerName(customerName);
			reservation.setReservedAt(LocalDate.now());

			return reservationRepository.save(reservation);

		} else {
			throw new TicketSoldOutException("Maalesef, biletler tükendi!");
		}
	}
}
