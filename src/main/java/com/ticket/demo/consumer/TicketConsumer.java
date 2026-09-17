package com.ticket.demo.consumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ticket.demo.model.Event;
import com.ticket.demo.model.Reservation;
import com.ticket.demo.repository.EventRepository;
import com.ticket.demo.repository.ReservationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class TicketConsumer {
	private final EventRepository eventRepository;
	private final ReservationRepository reservationRepository;

	@RabbitListener(queues = "ticket-queue")
	@Transactional
	public void processTicketRequest(String message) {
		
		try {
	        Thread.sleep(5000); //(islemi yavaslatiyoruz)
	    } catch (InterruptedException e) {}

		
		String[] parts = message.split(":");
		Long eventId = Long.parseLong(parts[0]);
		String customerName = parts[1];

		Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event bulunamadı"));

		if (event.getAvailableTickets() > 0) {
			event.setAvailableTickets(event.getAvailableTickets() - 1);
			eventRepository.save(event);

			Reservation reservation = new Reservation();
			reservation.setEventId(eventId);
			reservation.setCustomerName(customerName);
			reservation.setReservedAt(LocalDate.now());
			reservationRepository.save(reservation);

			System.out.println("✅ Bilet başarıyla alındı: " + customerName);
		} else {
			System.out.println("❌ Bilet kalmadı! İşlem reddedildi: " + customerName);
		}
	}
}
