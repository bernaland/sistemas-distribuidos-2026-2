export interface UserReportRow {
  cedula: string;
  username: string;
  name: string;
  email: string;
  roles: string;
  enabled: boolean;
}

export interface CustomerReportRow {
  cedula: string;
  name: string;
  address: string;
  phone: string;
  email: string;
}

export interface CustomerSaleRow {
  cedula: string;
  name: string;
  totalSales: number;
}

export interface SalesReport {
  customers: CustomerSaleRow[];
  grandTotal: number;
}
