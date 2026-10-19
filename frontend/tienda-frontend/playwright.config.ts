import { defineConfig } from '@playwright/test';

export default defineConfig({
  testDir: './e2e',
  timeout: 30000,
  expect: {
    timeout: 5000
  },
  fullyParallel: false,
  retries: 0,
  workers: 1,
  reporter: 'list',
  use: {
    baseURL: 'http://localhost:4200',
    ...(process.env['CI'] ? {} : { channel: 'msedge' }),
    headless: true,
    trace: 'off',
    video: 'off',
    screenshot: 'only-on-failure'
  },
  webServer: {
    command: 'node spa-server.js',
    url: 'http://localhost:4200',
    reuseExistingServer: !process.env['CI'],
    timeout: 10000
  }
});
