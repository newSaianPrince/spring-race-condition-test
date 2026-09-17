package com.ticket.demo.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueueTicketService {

	private final RabbitTemplate rabbitTemplate;
	
	public String purchaseTicket(Long eventId, String customerName)
	{
		String message = eventId + ":" + customerName;
		
		rabbitTemplate.convertAndSend("ticket-queue", message);
		return "İsteğiniz kuyruğa alındı, sıranız geldiğinde işlenecek.";	
	}
}
