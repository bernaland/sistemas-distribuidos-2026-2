import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { User, CreateUserRequest, UpdateUserRequest } from '../models';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/users`;


  list(): Observable<User[]> {
    return this.http.get<User[]>(this.baseUrl);
  }

  getByCedula(cedula: string): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/cedula/${cedula}`);
  }

  getById(id: number): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/${id}`);
  }

  create(user: CreateUserRequest): Observable<User> {
    return this.http.post<User>(this.baseUrl, user);
  }

  update(id: number, user: UpdateUserRequest): Observable<User> {
    return this.http.put<User>(`${this.baseUrl}/${id}`, user);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
