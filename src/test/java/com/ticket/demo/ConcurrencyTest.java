package com.ticket.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ticket.demo.model.Event;
import com.ticket.demo.repository.EventRepository;
import com.ticket.demo.service.OptimisticLockService;
import com.ticket.demo.service.PessimisticLockService;

import lombok.RequiredArgsConstructor;

@SpringBootTest
public class ConcurrencyTest {

	@Autowired
	private PessimisticLockService pessimisticLockService;
	@Autowired
	private OptimisticLockService optimisticLockService;
	@Autowired
	private EventRepository eventRepository;

	@Test
	void pessimistic_lock_ile_sadece_1_kisi_son_bileti_almali() throws InterruptedException {
		// 1. HAZIRLIK: 1 biletlik etkinlik oluştur ve kaydet
		Event event = new Event();
		event.setName("Test Konseri");
		event.setAvailableTickets(1);
		event = eventRepository.save(event);
		// 2. PARALEL İSTEK: 10 thread oluştur
		int threadCount = 10;
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(1); // Hepsini aynı anda başlatmak için
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failCount = new AtomicInteger(0);
		// 3. Her thread bilet almayı dener
		for (int i = 0; i < threadCount; i++) {
			final String customerName = "Musteri-" + i;
			final Long eventId = event.getId();
			executor.submit(() -> {
				try {
					latch.await(); // Hepsi burada bekler
					pessimisticLockService.purchaseTicket(eventId, customerName);
					successCount.incrementAndGet();
				} catch (Exception e) {
					failCount.incrementAndGet();
				}
			});
		}
		latch.countDown(); // Hepsini aynı anda serbest bırak!
		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.SECONDS);
		// 4. DOĞRULAMA
		assertEquals(1, successCount.get()); // Sadece 1 kişi alabilmeli
		assertEquals(9, failCount.get()); // 9 kişi reddedilmeli
		Event updatedEvent = eventRepository.findById(event.getId()).get();
		assertEquals(0, updatedEvent.getAvailableTickets()); // Kalan bilet 0 olmalı
	}
	
	@Test
	void optimistic_lock_ile_sadece_1_kisi_son_bileti_almali() throws InterruptedException {
		// 1. HAZIRLIK: 1 biletlik etkinlik oluştur ve kaydet
		Event event = new Event();
		event.setName("Test Konseri");
		event.setAvailableTickets(1);
		event = eventRepository.save(event);
		// 2. PARALEL İSTEK: 10 thread oluştur
		int threadCount = 10;
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(1); // Hepsini aynı anda başlatmak için
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failCount = new AtomicInteger(0);
		// 3. Her thread bilet almayı dener
		for (int i = 0; i < threadCount; i++) {
			final String customerName = "Musteri-" + i;
			final Long eventId = event.getId();
			executor.submit(() -> {
				try {
					latch.await(); // Hepsi burada bekler
					optimisticLockService.purchaseTicket(eventId, customerName);
					successCount.incrementAndGet();
				} catch (Exception e) {
					failCount.incrementAndGet();
				}
			});
		}
		latch.countDown(); // Hepsini aynı anda serbest bırak!
		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.SECONDS);
		// 4. DOĞRULAMA
		assertEquals(1, successCount.get()); // Sadece 1 kişi alabilmeli
		assertEquals(9, failCount.get()); // 9 kişi reddedilmeli
		Event updatedEvent = eventRepository.findById(event.getId()).get();
		assertEquals(0, updatedEvent.getAvailableTickets()); // Kalan bilet 0 olmalı
	}
}
