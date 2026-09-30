package com.uelbosque.catalogservice;

import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.*;

@Component
public class CatalogMutex {
    private final CatalogLockRepository repository;
    public CatalogMutex(CatalogLockRepository repository) { this.repository = repository; }
    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        if (repository.existsById(1L)) return;
        try { repository.saveAndFlush(new CatalogWriteLock(1L)); }
        catch (DataIntegrityViolationException concurrentInitialization) {
            if (!repository.existsById(1L)) throw concurrentInitialization;
        }
    }
    @Transactional(propagation=Propagation.MANDATORY)
    public void acquire() {
        repository.acquire().orElseThrow(() -> new IllegalStateException("Catalogo aun no inicializado"));
    }
}
