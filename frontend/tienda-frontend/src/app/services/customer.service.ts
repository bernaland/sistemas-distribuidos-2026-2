import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Customer } from '../models';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/customers`;


  list(): Observable<Customer[]>{
    return this.http.get<Customer[]>(this.baseUrl);
  }

  getByCedula(cedula: string): Observable<Customer> {
    return this.http.get<Customer>(`${this.baseUrl}/${cedula}`);
  }

  create(customer: Customer): Observable<Customer> {
    return this.http.post<Customer>(this.baseUrl, customer);
  }

  update(cedula: string, customer: Customer): Observable<Customer> {
    return this.http.put<Customer>(`${this.baseUrl}/${cedula}`, customer);
  }

  delete(cedula: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${cedula}`);
  }
}
