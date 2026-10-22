import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Product, ProductImportSummary } from '../models';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/products`;


  list(): Observable<Product[]> {
    return this.http.get<Product[]>(this.baseUrl);
  }

  getByCode(code: string): Observable<Product> {
    return this.http.get<Product>(`${this.baseUrl}/code/${code}`);
  }

  getById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.baseUrl}/${id}`);
  }

  create(product: Product): Observable<Product> {
    return this.http.post<Product>(this.baseUrl, product);
  }

  update(id: number, product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.baseUrl}/${id}`, product);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  importCsv(file: File): Observable<ProductImportSummary> {
    const formData = new FormData();
    formData.append('file', file, file.name);
    return this.http.post<ProductImportSummary>(`${this.baseUrl}/import`, formData);
  }
}
