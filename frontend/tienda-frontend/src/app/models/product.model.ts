export interface Product {
  id?: number;
  code: string;
  name: string;
  description?: string;
  purchasePrice: number;
  salePrice: number;
  ivaRate: number;
  supplierNit: string;
  category?: string;
  imageUrl?: string;
  active?: boolean;
}

export interface ProductImportSummary {
  totalProcessed?: number;
  totalSuccess?: number;
  message?: string;
}
