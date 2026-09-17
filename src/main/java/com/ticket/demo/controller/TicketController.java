package com.ticket.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ticket.demo.model.Reservation;
import com.ticket.demo.service.OptimisticLockService;
import com.ticket.demo.service.PessimisticLockService;
import com.ticket.demo.service.QueueTicketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

	private final OptimisticLockService optimisticLockService;
	private final PessimisticLockService pessimisticLockService;
	private final QueueTicketService queueTicketService;

	@PostMapping("/pessimistic/{eventId}")
	public ResponseEntity<?> pessimistic(@PathVariable Long eventId, @RequestParam String customer) {
		Reservation reservation = pessimisticLockService.purchaseTicket(eventId, customer);
		return ResponseEntity.ok(reservation);
	}

	@PostMapping("/optimistic/{eventId}")
	public ResponseEntity<?> optimistic(@PathVariable Long eventId, @RequestParam String customer) {
		Reservation reservation = optimisticLockService.purchaseTicket(eventId, customer);
		return ResponseEntity.ok(reservation);
	}

	@PostMapping("/queue/{eventId}")
	public ResponseEntity<?> queue(@PathVariable Long eventId, @RequestParam String customer) {
		String status = queueTicketService.purchaseTicket(eventId, customer);
		return ResponseEntity.ok(status);
	}
}
