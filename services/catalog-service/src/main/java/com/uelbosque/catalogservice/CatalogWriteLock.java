package com.uelbosque.catalogservice;

import jakarta.persistence.*;

@Entity
public class CatalogWriteLock {
    @Id private Long id;
    @Version private Long version;
    protected CatalogWriteLock() {}
    public CatalogWriteLock(Long id) { this.id = id; }
}
