# Bookstore Frontend (minimal)

This is a minimal React + Vite frontend to exercise the Bookstore API for testing.

Prerequisites:
- Node.js and npm installed
- The backend running at `http://localhost:8080`

Quick start:

```bash
cd bookstore-frontend
npm install
npm run dev
```

Open http://localhost:5173 and use the simple UI to call endpoints. Default basic-auth in the app uses username/password fields from the UI; some actions use admin credentials by default.

Notes:
- This is intentionally minimal and not production-ready.
- If you hit CORS errors, allow CORS in the backend or use a proxy.
