package com.uelbosque.customerservice;

import jakarta.persistence.*;
@Entity
@Table(name="customers")
public class Customer {
    @Version private Long version;
    @Id private String cedula;
    private String name;
    private String address;
    private String phone;
    private String email;
    protected Customer() {}
    public Customer(CustomerData data) { this.cedula = data.cedula(); update(data); }
    public void update(CustomerData data) {
        this.name = data.name();
        this.address = data.address();
        this.phone = data.phone();
        this.email = data.email();
    }
    public CustomerData data() { return new CustomerData(cedula, name, address, phone, email); }
}
