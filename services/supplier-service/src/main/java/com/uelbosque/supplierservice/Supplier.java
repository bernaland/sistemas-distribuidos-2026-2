package com.uelbosque.supplierservice;

import jakarta.persistence.*;
@Entity
@Table(name="suppliers")
public class Supplier {
    @Version private Long version;
    @Id private String nit;
    private String name;
    private String address;
    private String phone;
    private String city;
    protected Supplier() {}
    public Supplier(SupplierData data) { this.nit = data.nit(); update(data); }
    public void update(SupplierData data) {
        this.name = data.name();
        this.address = data.address();
        this.phone = data.phone();
        this.city = data.city();
    }
    public SupplierData data() { return new SupplierData(nit, name, address, phone, city); }
}
