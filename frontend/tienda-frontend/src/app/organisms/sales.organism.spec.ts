import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { SalesOrganism } from './sales.organism';
import { CustomerService, ProductService, SaleService } from '../services';

describe('SalesOrganism', () => {
  let component: SalesOrganism;
  let fixture: ComponentFixture<SalesOrganism>;
  let customerServiceSpy: jasmine.SpyObj<CustomerService>;
  let productServiceSpy: jasmine.SpyObj<ProductService>;
  let saleServiceSpy: jasmine.SpyObj<SaleService>;

  beforeEach(async () => {
    customerServiceSpy = jasmine.createSpyObj('CustomerService', ['getByCedula']);
    productServiceSpy = jasmine.createSpyObj('ProductService', ['getByCode']);
    saleServiceSpy = jasmine.createSpyObj('SaleService', ['create']);

    await TestBed.configureTestingModule({
      imports: [SalesOrganism],
      providers: [
        { provide: CustomerService, useValue: customerServiceSpy },
        { provide: ProductService, useValue: productServiceSpy },
        { provide: SaleService, useValue: saleServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SalesOrganism);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe inicializarse con un renglón de venta vacío', () => {
    expect(component).toBeTruthy();
    expect(component.items.length).toBe(1);
    expect(component.subtotal).toBe(0);
  });

  it('consultarCliente: valida cédula y busca cliente existente o no registrado', () => {
    component.cedulaCliente = '';
    component.consultarCliente();
    expect(component.notification()?.text).toContain('ingrese la cédula');

    customerServiceSpy.getByCedula.and.returnValue(of({
      cedula: '102030', name: 'Carlos Ruiz', address: 'Cra 7', phone: '123', email: 'c@t.com'
    }));
    component.cedulaCliente = '102030';
    component.consultarCliente();
    expect(component.nombreCliente).toBe('Carlos Ruiz');

    customerServiceSpy.getByCedula.and.returnValue(throwError(() => new Error('404')));
    component.consultarCliente();
    expect(component.nombreCliente).toBe('');
    expect(component.notification()?.text).toContain('no encontrado');
  });

  it('lookupItem: busca producto por código y recalcula importes', () => {
    productServiceSpy.getByCode.and.returnValue(of({
      code: '1', name: 'Leche', purchasePrice: 1000, salePrice: 2000, ivaRate: 19, supplierNit: 'N'
    }));
    component.items[0].codigo = '1';
    component.items[0].cantidad = 2;
    component.lookupItem(0);
    expect(component.items[0].nombre).toBe('Leche');
    expect(component.items[0].precioUnitario).toBe(2000);
    expect(component.subtotal).toBe(4000);
    expect(component.ivaTotal).toBe(760);
    expect(component.totalFinal).toBe(4760);

    productServiceSpy.getByCode.and.returnValue(throwError(() => new Error('404')));
    component.lookupItem(0);
    expect(component.items[0].nombre).toBe('Producto no encontrado');
  });

  it('gestión de renglones: agrega hasta 3 renglones y permite eliminar', () => {
    expect(component.items.length).toBe(1);
    component.agregarFila();
    component.agregarFila();
    expect(component.items.length).toBe(3);
    component.agregarFila();
    expect(component.items.length).toBe(3); // límite máximo 3 renglones

    component.quitarFila(1);
    expect(component.items.length).toBe(2);
  });

  it('confirmarVenta: valida cliente y productos antes de procesar', () => {
    component.cedulaCliente = '';
    component.confirmarVenta();
    expect(component.notification()?.text).toContain('validar primero un cliente');

    component.cedulaCliente = '102030';
    component.nombreCliente = 'Carlos';
    component.items = [{ codigo: '', nombre: '', cantidad: 0, precioUnitario: 0, ivaRate: 0, valorTotal: 0 }];
    component.confirmarVenta();
    expect(component.notification()?.text).toContain('al menos un producto válido');
  });

  it('confirmarVenta: registra venta exitosa con consecutivo y maneja error', () => {
    component.cedulaCliente = '102030';
    component.nombreCliente = 'Carlos';
    component.items = [{ codigo: '1', nombre: 'Leche', cantidad: 2, precioUnitario: 2000, ivaRate: 19, valorTotal: 4760 }];

    saleServiceSpy.create.and.returnValue(of({ id: 501, grandTotal: 4760 } as any));
    component.confirmarVenta();
    expect(component.consecutivoVenta).toBe(501);
    expect(component.notification()?.text).toContain('Venta #501 registrada');

    saleServiceSpy.create.and.returnValue(throwError(() => ({
      error: { message: 'Stock insuficiente' }
    })));
    component.cedulaCliente = '102030';
    component.nombreCliente = 'Carlos';
    component.items = [{ codigo: '1', nombre: 'Leche', cantidad: 2, precioUnitario: 2000, ivaRate: 19, valorTotal: 4760 }];
    component.confirmarVenta();
    expect(component.notification()?.text).toBe('Stock insuficiente');
  });

  it('limpiarVenta: restablece consecutivo, formulario y notificación', () => {
    component.consecutivoVenta = 999;
    component.cedulaCliente = '123';
    component.limpiarVenta();
    expect(component.consecutivoVenta).toBeNull();
    expect(component.cedulaCliente).toBe('');
    expect(component.notification()).toBeNull();
  });
});
