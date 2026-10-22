import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

function handle401(auth: AuthService, router: Router): void {
  auth.logout();
  router.navigate(['/']);
}

function format503(error: HttpErrorResponse): HttpErrorResponse {
  const message = 'El servicio no está disponible temporalmente. Por favor, intente más tarde.';
  return new HttpErrorResponse({
    error: { message, original: error.error },
    status: 503,
    statusText: error.statusText,
    headers: error.headers,
    url: error.url ?? undefined
  });
}

function extractValidationMessage(error: HttpErrorResponse): string {
  if (error.error?.message) return error.error.message;
  if (Array.isArray(error.error?.errors)) return error.error.errors.join(', ');
  if (typeof error.error === 'string' && error.error.trim()) return error.error;
  return error.status === 409 ? 'Conflicto: registro duplicado' : 'Datos inválidos en el formulario';
}

function formatValidation(error: HttpErrorResponse): HttpErrorResponse {
  const message = extractValidationMessage(error);
  return new HttpErrorResponse({
    error: { message, validationErrors: error.error?.errors ?? error.error },
    status: error.status,
    statusText: error.statusText,
    headers: error.headers,
    url: error.url ?? undefined
  });
}

function dispatchError(err: HttpErrorResponse, auth: AuthService, router: Router) {
  if (err.status === 401) {
    handle401(auth, router);
    return throwError(() => err);
  }
  if (err.status === 503) {
    return throwError(() => format503(err));
  }
  if (err.status === 400 || err.status === 409) {
    return throwError(() => formatValidation(err));
  }
  return throwError(() => err);
}

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => dispatchError(error, auth, router))
  );
};
