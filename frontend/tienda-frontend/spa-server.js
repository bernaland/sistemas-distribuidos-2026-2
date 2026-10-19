const http = require('http');
const fs = require('fs');
const path = require('path');

const dist = path.join(__dirname, 'dist', 'tienda-frontend');
const mimeTypes = {
  '.html': 'text/html',
  '.js': 'application/javascript',
  '.css': 'text/css',
  '.json': 'application/json',
  '.ico': 'image/x-icon',
  '.woff': 'font/woff',
  '.woff2': 'font/woff2'
};

const server = http.createServer((req, res) => {
  const urlPath = req.url.split('?')[0];
  const filePath = path.join(dist, urlPath);
  if (fs.existsSync(filePath) && fs.statSync(filePath).isFile()) {
    const ext = path.extname(filePath);
    res.writeHead(200, { 'Content-Type': mimeTypes[ext] || 'application/octet-stream' });
    fs.createReadStream(filePath).pipe(res);
  } else {
    res.writeHead(200, { 'Content-Type': 'text/html' });
    fs.createReadStream(path.join(dist, 'index.html')).pipe(res);
  }
});

const port = process.env.PORT || 4200;
server.listen(port, () => {
  console.log(`Tienda Genérica SPA running on http://localhost:${port}`);
});
