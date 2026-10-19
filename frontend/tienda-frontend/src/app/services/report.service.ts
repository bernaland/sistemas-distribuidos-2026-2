import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { UserReportRow, CustomerReportRow, SalesReport } from '../models';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/reports`;


  getUsers(): Observable<UserReportRow[]> {
    return this.http.get<UserReportRow[]>(`${this.baseUrl}/users`);
  }

  getCustomers(): Observable<CustomerReportRow[]> {
    return this.http.get<CustomerReportRow[]>(`${this.baseUrl}/customers`);
  }

  getSalesByCustomer(): Observable<SalesReport> {
    return this.http.get<SalesReport>(`${this.baseUrl}/sales-by-customer`);
  }
}
