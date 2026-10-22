import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { SaleRequest, SaleResponse } from '../models';

@Injectable({
  providedIn: 'root'
})
export class SaleService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/sales`;
  private currentKey: string | null = null;


  getOrCreateKey(): string {
    if (!this.currentKey) {
      this.currentKey = this.generateKey();
    }
    return this.currentKey;
  }

  getCurrentKey(): string | null {
    return this.currentKey;
  }

  resetKey(): void {
    this.currentKey = null;
  }

  create(sale: SaleRequest, idempotencyKey?: string): Observable<SaleResponse> {
    const key = idempotencyKey || this.getOrCreateKey();
    const headers = new HttpHeaders({ 'Idempotency-Key': key });
    return this.http.post<SaleResponse>(this.baseUrl, sale, { headers }).pipe(
      tap(() => this.resetKey())
    );
  }

  list(): Observable<SaleResponse[]> {
    return this.http.get<SaleResponse[]>(this.baseUrl);
  }

  getById(id: number): Observable<SaleResponse> {
    return this.http.get<SaleResponse>(`${this.baseUrl}/${id}`);
  }

  private generateKey(): string {
    const time = Date.now().toString(36);
    const rnd = Math.random().toString(36).substring(2, 9);
    return `sale-${time}-${rnd}`;
  }
}
