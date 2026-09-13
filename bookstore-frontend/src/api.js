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
  let data = text

  try {
    data = text ? JSON.parse(text) : null
  } catch (e) {
    // keep plain text response as-is
  }

  if (!res.ok) {
    const err = new Error(data?.message || data?.error || `Request failed with status ${res.status}`)
    err.status = res.status
    err.payload = data
    throw err
  }

  return data
}

export async function createBook(book, username = 'admin', password = 'admin123') {
  return apiFetch('/books', 'POST', book, username, password)
}

export async function updateBook(id, book, username = 'admin', password = 'admin123') {
  return apiFetch(`/books/${id}`, 'PUT', book, username, password)
}

export async function deleteBook(id, username = 'admin', password = 'admin123') {
  return apiFetch(`/books/${id}`, 'DELETE', null, username, password)
}

export default apiFetch
