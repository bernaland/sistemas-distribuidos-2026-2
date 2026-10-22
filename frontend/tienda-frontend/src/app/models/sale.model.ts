export interface SaleItemRequest {
  productCode: string;
  quantity: number;
}

export interface SaleRequest {
  customerCedula: string;
  items: SaleItemRequest[];
}

export interface SaleLineResponse {
  productCode: string;
  productName: string;
  quantity: number;
  unitPrice: number;
  ivaRate: number;
  subtotal: number;
  tax: number;
}

export interface SaleResponse {
  id: number;
  customerCedula: string;
  customerName: string;
  userCedula: string;
  username: string;
  createdAt: string;
  items: SaleLineResponse[];
  subtotal: number;
  taxTotal: number;
  grandTotal: number;
}
