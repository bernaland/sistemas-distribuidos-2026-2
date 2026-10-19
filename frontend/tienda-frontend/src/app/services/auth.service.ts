import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { LoginRequest, AuthResponse, User } from '../models';

interface JwtPayload {
  exp?: number;
  roles?: string;
  sub?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/api/users`;
  private readonly tokenKey = 'tg_auth_token';
  private readonly userKey = 'tg_auth_user';

  readonly tokenSignal = signal<string | null>(this.getStoredToken());
  readonly currentUser = signal<User | null>(this.getStoredUser());

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, credentials).pipe(
      tap(res => this.saveSession(res))
    );
  }

  logout(): void {
    this.tokenSignal.set(null);
    this.currentUser.set(null);
    sessionStorage.removeItem(this.tokenKey);
    sessionStorage.removeItem(this.userKey);
  }

  getToken(): string | null {
    if (this.isTokenExpired()) {
      return null;
    }
    return this.tokenSignal();
  }

  getCurrentUser(): User | null {
    return this.currentUser();
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  hasRole(role: string): boolean {
    const user = this.currentUser();
    if (!user || !user.roles) return false;
    return user.roles.split(',').map(r => r.trim()).includes(role);
  }

  isTokenExpired(): boolean {
    const token = this.tokenSignal();
    if (!token) return true;
    const payload = this.decodeToken(token);
    if (!payload || !payload.exp) return false;
    return Math.floor(Date.now() / 1000) >= payload.exp;
  }

  private decodeToken(token: string): JwtPayload | null {
    try {
      const parts = token.split('.');
      if (parts.length < 2) return null;
      const payloadBase64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
      return JSON.parse(atob(payloadBase64)) as JwtPayload;
    } catch {
      return null;
    }
  }

  private saveSession(res: AuthResponse): void {
    if (res?.token) {
      this.tokenSignal.set(res.token);
      sessionStorage.setItem(this.tokenKey, res.token);
      if (res.user) {
        this.currentUser.set(res.user);
        sessionStorage.setItem(this.userKey, JSON.stringify(res.user));
      }
    }
  }

  private getStoredToken(): string | null {
    return sessionStorage.getItem(this.tokenKey);
  }

  private getStoredUser(): User | null {
    const raw = sessionStorage.getItem(this.userKey);
    return raw ? JSON.parse(raw) : null;
  }
}

