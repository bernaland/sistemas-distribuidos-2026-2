import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { ProductsOrganism } from './products.organism';
import { ProductService } from '../services';
import { Product } from '../models';

describe('ProductsOrganism', () => {
  let component: ProductsOrganism;
  let fixture: ComponentFixture<ProductsOrganism>;
  let productServiceSpy: jasmine.SpyObj<ProductService>;

  const mockProducts: Product[] = [
    { id: 1, code: '1', name: 'Leche 1L', purchasePrice: 2000, ivaRate: 19, salePrice: 2800, supplierNit: '900112233' }
  ];

  beforeEach(async () => {
    productServiceSpy = jasmine.createSpyObj('ProductService', ['list', 'getByCode', 'create', 'update', 'delete', 'importCsv']);
    productServiceSpy.list.and.returnValue(of(mockProducts));

    await TestBed.configureTestingModule({
      imports: [ProductsOrganism],
      providers: [{ provide: ProductService, useValue: productServiceSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(ProductsOrganism);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse y cargar lista de productos', () => {
    expect(component).toBeTruthy();
    expect(component.productsList().length).toBe(1);
  });

  it('cargarProductos: maneja error al consultar catálogo', () => {
    productServiceSpy.list.and.returnValue(throwError(() => new Error('Error')));
    component.cargarProductos();
    expect(component.notification()?.text).toBe('Error al cargar catálogo de productos');
  });

  it('selectProduct: llena campos con el producto seleccionado', () => {
    component.selectProduct(mockProducts[0]);
    expect(component.selectedId).toBe(1);
    expect(component.codigo).toBe('1');
    expect(component.nombre).toBe('Leche 1L');
  });

  it('consultar: valida código y busca producto', () => {
    component.codigo = '';
    component.consultar();
    expect(component.notification()?.text).toContain('Ingrese un código');

    productServiceSpy.getByCode.and.returnValue(of(mockProducts[0]));
    component.codigo = '1';
    component.consultar();
    expect(component.nombre).toBe('Leche 1L');

    productServiceSpy.getByCode.and.returnValue(throwError(() => new Error('404')));
    component.consultar();
    expect(component.notification()?.text).toContain('no encontrado');
  });

  it('crear: valida datos obligatorios y agrega producto o expone error 400', () => {
    component.codigo = '';
    component.crear();
    expect(component.notification()?.text).toContain('Complete código');

    productServiceSpy.create.and.returnValue(of(mockProducts[0]));
    component.codigo = '1';
    component.nombre = 'Leche';
    component.nitProveedor = '900112233';
    component.crear();
    expect(component.notification()?.text).toContain('agregado');

    productServiceSpy.create.and.returnValue(throwError(() => ({
      error: { message: 'Código duplicado' }
    })));
    component.codigo = '1';
    component.nombre = 'Leche';
    component.nitProveedor = '900112233';
    component.crear();
    expect(component.notification()?.text).toBe('Código duplicado');
  });

  it('actualizar y borrar: valida selección previa y ejecuta operación', () => {
    component.selectedId = null;
    component.actualizar();
    expect(component.notification()?.text).toContain('Seleccione un producto');
    component.borrar();
    expect(component.notification()?.text).toContain('Seleccione un producto');

    component.selectedId = 1;
    productServiceSpy.update.and.returnValue(of(mockProducts[0]));
    component.actualizar();
    expect(component.notification()?.text).toContain('actualizado');

    productServiceSpy.delete.and.returnValue(of(undefined));
    component.borrar();
    expect(component.notification()?.text).toContain('eliminado');
  });

  it('importarCsv: selecciona archivo y ejecuta carga masiva exitosa o con error', () => {
    const file = new File(['1,Leche,900112233,1000,19,1500'], 'productos.csv');
    component.onFileSelected({ target: { files: [file] } } as any);
    expect(component.selectedFile).toBe(file);

    productServiceSpy.importCsv.and.returnValue(of({ message: 'OK' }));
    component.importarCsv();
    expect(component.notification()?.text).toContain('importado y actualizado');

    component.selectedFile = file;
    productServiceSpy.importCsv.and.returnValue(throwError(() => ({
      error: { message: 'Formato CSV inválido' }
    })));
    component.importarCsv();
    expect(component.notification()?.text).toBe('Formato CSV inválido');
  });
});
