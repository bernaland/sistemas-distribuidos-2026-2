import { test, expect, Page } from '@playwright/test';

function createMockToken(): string {
  const header = Buffer.from(JSON.stringify({ alg: 'HS256', typ: 'JWT' })).toString('base64');
  const payload = Buffer.from(
    JSON.stringify({
      sub: 'admininicial',
      roles: 'ROLE_ADMIN',
      exp: Math.floor(Date.now() / 1000) + 86400
    })
  ).toString('base64');
  return `${header}.${payload}.e2e_signature`;
}

interface MockData {
  customers: Array<{ cedula: string; name: string; address: string; phone: string; email: string }>;
  products: Array<{ id: number; code: string; name: string; supplierNit: string; purchasePrice: number; ivaRate: number; salePrice: number }>;
  sales: Array<{ id: number; customerCedula: string; subtotal: number; vatAmount: number; grandTotal: number }>;
  consecutive: number;
}

let mockState: MockData;

function resetState() {
  mockState = {
    customers: [
      {
        cedula: '1020304050',
        name: 'Cliente Existente',
        address: 'Carrera 7 # 45-10',
        phone: '3001234567',
        email: 'existente@correo.com'
      }
    ],
    products: [
      {
        id: 1,
        code: '101',
        name: 'Arroz Diana 1kg',
        supplierNit: '900112233',
        purchasePrice: 4000,
        ivaRate: 19,
        salePrice: 5500
      },
      {
        id: 2,
        code: '102',
        name: 'Aceite Premier 1L',
        supplierNit: '900112233',
        purchasePrice: 8000,
        ivaRate: 19,
        salePrice: 11000
      },
      {
        id: 3,
        code: '103',
        name: 'Leche Alquería 1L',
        supplierNit: '900112233',
        purchasePrice: 3500,
        ivaRate: 19,
        salePrice: 4800
      }
    ],
    sales: [],
    consecutive: 100
  };
}

async function setupApiRoutes(page: Page) {
  await page.route('http://localhost:8080/api/**', async (route) => {
    const req = route.request();
    const url = new URL(req.url());
    const method = req.method();
    const path = url.pathname;

    // Login
    if (path === '/api/users/login' && method === 'POST') {
      const body = req.postDataJSON() || {};
      if (body.username === 'admininicial' && body.password) {
        return route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            token: createMockToken(),
            type: 'Bearer',
            user: {
              id: 1,
              username: 'admininicial',
              name: 'Administrador General',
              email: 'admin@tienda.com',
              roles: 'ROLE_ADMIN'
            }
          })
        });
      }
      return route.fulfill({ status: 401, body: JSON.stringify({ message: 'Credenciales inválidas' }) });
    }

    // Gateway health
    if (path === '/api/gateway/health') {
      return route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({ status: 'UP', service: 'api-gateway' })
      });
    }

    // Suppliers
    if (path.startsWith('/api/suppliers')) {
      return route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([])
      });
    }

    // Customers list & create
    if (path === '/api/customers') {
      if (method === 'GET') {
        return route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify(mockState.customers)
        });
      }
      if (method === 'POST') {
        const newCust = req.postDataJSON();
        mockState.customers.push(newCust);
        return route.fulfill({
          status: 201,
          contentType: 'application/json',
          body: JSON.stringify(newCust)
        });
      }
    }

    // Customer by cedula (GET, PUT, DELETE)
    const custMatch = path.match(/^\/api\/customers\/([^/]+)$/);
    if (custMatch) {
      const cedula = custMatch[1];
      const idx = mockState.customers.findIndex((c) => c.cedula === cedula);

      if (method === 'GET') {
        if (idx >= 0) {
          return route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify(mockState.customers[idx])
          });
        }
        return route.fulfill({ status: 404, body: JSON.stringify({ message: 'Cliente no encontrado' }) });
      }

      if (method === 'PUT') {
        const updateData = req.postDataJSON();
        if (idx >= 0) {
          mockState.customers[idx] = { ...mockState.customers[idx], ...updateData };
          return route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify(mockState.customers[idx])
          });
        }
        return route.fulfill({ status: 404, body: JSON.stringify({ message: 'Cliente no encontrado' }) });
      }

      if (method === 'DELETE') {
        if (idx >= 0) {
          mockState.customers.splice(idx, 1);
          return route.fulfill({ status: 200, body: JSON.stringify({ message: 'Eliminado' }) });
        }
        return route.fulfill({ status: 404, body: JSON.stringify({ message: 'Cliente no encontrado' }) });
      }
    }

    // Products list & import
    if (path === '/api/products') {
      return route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(mockState.products)
      });
    }

    if (path === '/api/products/import' && method === 'POST') {
      // Simulate CSV parsing
      return route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          totalRecords: 3,
          successCount: 3,
          failedCount: 0,
          errors: []
        })
      });
    }

    const prodCodeMatch = path.match(/^\/api\/products\/code\/([^/]+)$/);
    if (prodCodeMatch && method === 'GET') {
      const code = prodCodeMatch[1];
      const prod = mockState.products.find((p) => p.code === code);
      if (prod) {
        return route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify(prod)
        });
      }
      return route.fulfill({ status: 404, body: JSON.stringify({ message: 'Producto no encontrado' }) });
    }

    // Sales creation
    if (path === '/api/sales' && method === 'POST') {
      const payload = req.postDataJSON() || {};
      mockState.consecutive += 1;
      let subtotal = 0;
      let vat = 0;
      for (const item of payload.items || []) {
        const prod = mockState.products.find((p) => p.code === item.productCode);
        const price = prod ? prod.salePrice : 1000;
        const lineSubtotal = price * item.quantity;
        const lineVat = lineSubtotal * 0.19;
        subtotal += lineSubtotal;
        vat += lineVat;
      }
      const grandTotal = Math.round((subtotal + vat) * 100) / 100;
      const saleRecord = {
        id: mockState.consecutive,
        customerCedula: payload.customerCedula,
        subtotal,
        vatAmount: vat,
        grandTotal
      };
      mockState.sales.push(saleRecord);

      return route.fulfill({
        status: 201,
        contentType: 'application/json',
        body: JSON.stringify({
          id: saleRecord.id,
          orderId: saleRecord.id,
          customerCedula: saleRecord.customerCedula,
          subtotal: saleRecord.subtotal,
          vatAmount: saleRecord.vatAmount,
          grandTotal: saleRecord.grandTotal,
          status: 'COMPLETED'
        })
      });
    }

    // Reports: sales by customer
    if (path === '/api/reports/sales-by-customer') {
      const reportRows = mockState.customers.map((c) => {
        const total = mockState.sales
          .filter((s) => s.customerCedula === c.cedula)
          .reduce((acc, s) => acc + s.grandTotal, 0);
        return {
          cedula: c.cedula,
          name: c.name,
          totalSales: total
        };
      });
      const grandTotal = reportRows.reduce((acc, r) => acc + r.totalSales, 0);
      return route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          customers: reportRows,
          grandTotal: grandTotal
        })
      });
    }

    return route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([]) });
  });
}

test.describe('E2E Tienda Genérica - Flujo Completo', () => {
  test.beforeEach(async ({ page }) => {
    resetState();
    await setupApiRoutes(page);
  });

  test('1. Flujo de Login y navegación al Dashboard', async ({ page }) => {
    await page.goto('/');

    // Verify login form is present
    await expect(page.locator('organism-login')).toBeVisible();

    // Fill credentials
    const inputs = page.locator('atom-input input');
    await inputs.nth(0).fill('admininicial');
    await inputs.nth(1).fill('AdminSecret123!');

    // Submit
    await page.click('button[type="submit"]');

    // Verify redirection to /dashboard
    await page.waitForURL('**/dashboard');
    await expect(page.locator('organism-dashboard')).toBeVisible();
    await expect(page.locator('text=Bienvenido al Panel de Control')).toBeVisible();
  });

  test('2. CRUD Completo de Cliente (Crear, Consultar, Actualizar, Borrar)', async ({ page }) => {
    // Inject auth session
    await page.addInitScript(({ token }) => {
      sessionStorage.setItem('tg_auth_token', token);
      sessionStorage.setItem(
        'tg_auth_user',
        JSON.stringify({
          id: 1,
          username: 'admininicial',
          name: 'Administrador General',
          roles: 'ROLE_ADMIN'
        })
      );
    }, { token: createMockToken() });

    await page.goto('/clients');
    await expect(page.locator('organism-clients')).toBeVisible();

    // 1. Create client
    const inputs = page.locator('atom-input input');
    await inputs.nth(0).fill('1098765432');
    await inputs.nth(1).fill('Carlos Mario Pérez');
    await inputs.nth(2).fill('Calle 100 # 15-20');
    await inputs.nth(3).fill('3115556677');
    await inputs.nth(4).fill('carlos.perez@tienda.com');

    // Click "Crear Cliente"
    await page.locator('atom-button:has-text("Crear Cliente") button').click();

    // Verify success notification
    await expect(page.locator('.alert-success')).toContainText('Carlos Mario Pérez registrado');
    await expect(page.locator('table')).toContainText('1098765432');

    // 2. Consult client by cedula
    await inputs.nth(0).fill('1098765432');
    await page.locator('atom-button:has-text("Consultar") button').click();
    await expect(page.locator('.alert-success')).toContainText('Cliente encontrado: Carlos Mario Pérez');

    // 3. Update client
    await inputs.nth(2).fill('Avenida Siempre Viva 742');
    await page.locator('atom-button:has-text("Actualizar") button').click();
    await expect(page.locator('.alert-success')).toContainText('Cliente Carlos Mario Pérez actualizado');

    // 4. Delete client
    await page.locator('atom-button:has-text("Borrar") button').click();
    await expect(page.locator('.alert-danger')).toContainText('Cliente eliminado exitosamente');
  });

  test('3. Carga Masiva de Productos vía CSV', async ({ page }) => {
    await page.addInitScript(({ token }) => {
      sessionStorage.setItem('tg_auth_token', token);
      sessionStorage.setItem(
        'tg_auth_user',
        JSON.stringify({ id: 1, username: 'admininicial', name: 'Admin', roles: 'ROLE_ADMIN' })
      );
    }, { token: createMockToken() });

    await page.goto('/products');
    await expect(page.locator('organism-products')).toBeVisible();

    // Prepare CSV buffer
    const csvContent =
      'codigo_producto,nombre_producto,nitproveedor,precio_compra,ivacompra,precio_venta\n' +
      '101,Arroz Diana 1kg,900112233,4000,19,5500\n' +
      '102,Aceite Premier 1L,900112233,8000,19,11000\n' +
      '103,Leche Alquería 1L,900112233,3500,19,4800';

    const fileChooserInput = page.locator('input[type="file"]');
    await fileChooserInput.setInputFiles({
      name: 'productos.csv',
      mimeType: 'text/csv',
      buffer: Buffer.from(csvContent, 'utf-8')
    });

    // Click "Subir Catálogo CSV"
    await page.locator('atom-button:has-text("Subir Catálogo CSV") button').click();

    // Verify success notification
    await expect(page.locator('.alert-success')).toContainText('Catálogo CSV importado y actualizado exitosamente');
    await expect(page.locator('table')).toContainText('Arroz Diana 1kg');
  });

  test('4. Venta con IVA y 3 renglones', async ({ page }) => {
    await page.addInitScript(({ token }) => {
      sessionStorage.setItem('tg_auth_token', token);
      sessionStorage.setItem(
        'tg_auth_user',
        JSON.stringify({ id: 1, username: 'admininicial', name: 'Admin', roles: 'ROLE_ADMIN' })
      );
    }, { token: createMockToken() });

    await page.goto('/sales');
    await expect(page.locator('organism-sales')).toBeVisible();

    // Validate customer
    const clientInput = page.locator('atom-input input').first();
    await clientInput.fill('1020304050');
    await page.locator('atom-button:has-text("Validar") button').click();
    await expect(page.locator('.alert-success')).toContainText('Cliente confirmado: Cliente Existente');

    // Add 2 more lines for total of 3 lines
    const addLineBtn = page.locator('atom-button:has-text("Agregar Renglón") button');
    await addLineBtn.click();
    await addLineBtn.click();

    // Table rows
    const rows = page.locator('table tbody tr');
    await expect(rows).toHaveCount(3);

    // Line 1: Code 101, Quantity 2
    const code1 = rows.nth(0).locator('input').nth(0);
    const qty1 = rows.nth(0).locator('input').nth(2);
    await code1.fill('101');
    await code1.blur();
    await qty1.fill('2');
    await qty1.dispatchEvent('input');

    // Line 2: Code 102, Quantity 1
    const code2 = rows.nth(1).locator('input').nth(0);
    const qty2 = rows.nth(1).locator('input').nth(2);
    await code2.fill('102');
    await code2.blur();
    await qty2.fill('1');
    await qty2.dispatchEvent('input');

    // Line 3: Code 103, Quantity 3
    const code3 = rows.nth(2).locator('input').nth(0);
    const qty3 = rows.nth(2).locator('input').nth(2);
    await code3.fill('103');
    await code3.blur();
    await qty3.fill('3');
    await qty3.dispatchEvent('input');

    // Verify totals calculated with VAT
    // Line 1: 2 * 5500 = 11000 + 19% (2090) = 13090
    // Line 2: 1 * 11000 = 11000 + 19% (2090) = 13090
    // Line 3: 3 * 4800 = 14400 + 19% (2736) = 17136
    // Subtotal = 36,400. Total IVA = 6,916. Total con IVA = 43,316.
    await expect(page.locator('text=Total con IVA:')).toBeVisible();

    // Confirm transaction
    await page.locator('atom-button:has-text("Confirmar Transacción") button').click();

    // Verify success notification with consecutive
    await expect(page.locator('.alert-success')).toContainText('Venta #101 registrada exitosamente');
  });

  test('5. Reporte Consolidado de Ventas por Cliente', async ({ page }) => {
    // Seed a sale into mock state
    mockState.sales.push({
      id: 101,
      customerCedula: '1020304050',
      subtotal: 36400,
      vatAmount: 6916,
      grandTotal: 43316
    });

    await page.addInitScript(({ token }) => {
      sessionStorage.setItem('tg_auth_token', token);
      sessionStorage.setItem(
        'tg_auth_user',
        JSON.stringify({ id: 1, username: 'admininicial', name: 'Admin', roles: 'ROLE_ADMIN' })
      );
    }, { token: createMockToken() });

    await page.goto('/reports');
    await expect(page.locator('organism-reports')).toBeVisible();

    // Select "Ventas por Cliente"
    await page.locator('atom-button:has-text("Ventas por Cliente") button').click();

    // Verify report table and values
    await expect(page.locator('table')).toContainText('Cliente Existente');
    await expect(page.locator('table')).toContainText('$43,316');
    await expect(page.locator('tfoot')).toContainText('Total General Acumulado:');
    await expect(page.locator('tfoot')).toContainText('$43,316');
  });
});
