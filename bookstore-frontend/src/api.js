// Use proxied relative path in dev (vite proxy configured in vite.config.js)
const BASE = '/bookstore-api'

export async function apiFetch(path, method = 'GET', body = null, username = 'admin', password = 'admin123') {
  const url = BASE + path
  const headers = { 'Content-Type': 'application/json' }
  if (username && password) headers['Authorization'] = 'Basic ' + btoa(username + ':' + password)
  const opts = { method, headers }
  if (body) opts.body = JSON.stringify(body)
  const res = await fetch(url, opts)
  const text = await res.text()
  try { return JSON.parse(text) } catch (e) { return text }
}

export default apiFetch
