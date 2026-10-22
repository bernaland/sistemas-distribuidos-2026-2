export interface User {
  id?: number;
  cedula: string;
  username: string;
  name: string;
  email: string;
  roles: string;
  enabled?: boolean;
}

export interface CreateUserRequest {
  cedula: string;
  username: string;
  password: string;
  name: string;
  email: string;
  roles?: string;
}

export interface UpdateUserRequest {
  username?: string;
  password?: string;
  name?: string;
  email?: string;
  roles?: string;
  enabled?: boolean;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  user: User;
}
