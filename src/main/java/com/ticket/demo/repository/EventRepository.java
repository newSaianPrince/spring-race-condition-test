package com.ticket.demo.repository;

import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ticket.demo.model.Event;

public interface EventRepository extends JpaRepository<Event, Long>{
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT e FROM Event e WHERE e.id = :id")
	Optional<Event> findByIdWithPessimisticLock(@Param("id") Long id);
}
