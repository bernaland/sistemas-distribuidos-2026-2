import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface GatewayHealth {
  status: string;
  service: string;
  timestamp?: string;
  routes?: Record<string, string>;
}

@Injectable({
  providedIn: 'root'
})
export class GatewayService {
  private readonly http = inject(HttpClient);
  private readonly healthUrl = `${environment.apiUrl}/api/gateway/health`;


  checkHealth(): Observable<GatewayHealth> {
    return this.http.get<GatewayHealth>(this.healthUrl);
  }
}
