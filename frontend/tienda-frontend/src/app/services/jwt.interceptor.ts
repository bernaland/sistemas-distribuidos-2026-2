import { Injectable, inject } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpInterceptorFn } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

export const jwtInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();
  const isTargetApi = request.url.startsWith(environment.apiUrl);
  if (token && isTargetApi) {
    return next(request.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    }));
  }
  return next(request);
};

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  private readonly authService = inject(AuthService);

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = this.authService.getToken();
    const isTargetApi = request.url.startsWith(environment.apiUrl);
    if (token && isTargetApi) {
      return next.handle(request.clone({
        setHeaders: { Authorization: `Bearer ${token}` }
      }));
    }
    return next.handle(request);
  }
}
