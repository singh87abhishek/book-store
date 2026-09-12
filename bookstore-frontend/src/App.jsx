import React, { useState } from 'react'
import { apiFetch } from './api'

export default function App() {
  const [creds, setCreds] = useState({ username: '', password: '' })
  const [loggedIn, setLoggedIn] = useState(null) // { username, password }
  const [view, setView] = useState('books')
  const [output, setOutput] = useState('')
  const [books, setBooks] = useState([])
  const [cart, setCart] = useState(null)
  const [orders, setOrders] = useState([])

  const [qtyModal, setQtyModal] = useState({ open: false, book: null, qty: 1 })

  async function handleRegister() {
    if (!loggedIn || loggedIn.username !== 'admin') return alert('Register allowed for admin only')
    const payload = { username: creds.username, password: creds.password, email: creds.username + '@example.com' }
    const res = await apiFetch('/auth/register', 'POST', payload, loggedIn.username, loggedIn.password)
    setOutput(JSON.stringify(res, null, 2))
  }

  async function handleLogin() {
    const payload = { userName: creds.username, password: creds.password }
    const res = await apiFetch('/auth/login', 'POST', payload, creds.username, creds.password)
    setOutput(JSON.stringify(res, null, 2))
    if (res && res.username) {
      setLoggedIn({ username: creds.username, password: creds.password })
      setView('books')
      fetchBooks({ username: creds.username, password: creds.password })
    }
  }

  function logout() {
    setLoggedIn(null)
    setBooks([])
    setCart(null)
    setOrders([])
    setOutput('')
  }

  async function fetchBooks(auth = loggedIn) {
    if (!auth) return setOutput('Not authenticated')
    const res = await apiFetch('/books', 'GET', null, auth.username, auth.password)
    setBooks(res)
    setOutput(JSON.stringify(res, null, 2))
  }

  async function fetchCart() {
    if (!loggedIn) return setOutput('Not authenticated')
    const res = await apiFetch('/cart', 'GET', null, loggedIn.username, loggedIn.password)
    setCart(res)
    setOutput(JSON.stringify(res, null, 2))
  }

  async function openAddToCartModal(book) {
    setQtyModal({ open: true, book, qty: 1 })
  }

  async function confirmAddToCart() {
    const { book, qty } = qtyModal
    setQtyModal({ open: false, book: null, qty: 1 })
    if (!loggedIn) return setOutput('Not authenticated')
    const res = await apiFetch(`/cart/add/${book.id}?quantity=${qty}`, 'POST', null, loggedIn.username, loggedIn.password)
    setCart(res)
    setOutput(JSON.stringify(res, null, 2))
  }

  async function updateCartQuantity(bookId, quantity) {
    if (!loggedIn) return setOutput('Not authenticated')
    const res = await apiFetch(`/cart/update/${bookId}?quantity=${quantity}`, 'PUT', null, loggedIn.username, loggedIn.password)
    setCart(res)
    setOutput(JSON.stringify(res, null, 2))
  }

  async function removeFromCart(bookId) {
    if (!loggedIn) return setOutput('Not authenticated')
    const res = await apiFetch(`/cart/remove/${bookId}`, 'DELETE', null, loggedIn.username, loggedIn.password)
    setCart(res)
    setOutput(JSON.stringify(res, null, 2))
  }

  async function checkout() {
    if (!loggedIn) return setOutput('Not authenticated')
    const res = await apiFetch('/orders/checkout', 'POST', null, loggedIn.username, loggedIn.password)
    setOutput(JSON.stringify(res, null, 2))
  }

  async function fetchOrders() {
    if (!loggedIn) return setOutput('Not authenticated')
    const res = await apiFetch('/orders', 'GET', null, loggedIn.username, loggedIn.password)
    setOrders(res)
    setOutput(JSON.stringify(res, null, 2))
  }

  // Render
  if (!loggedIn) {
    return (
      <div style={{ padding: 16, fontFamily: 'Arial, sans-serif' }}>
        <h2>Bookstore - Login</h2>
        <div style={{ marginBottom: 12 }}>
          <input placeholder="username" value={creds.username} onChange={e => setCreds({ ...creds, username: e.target.value })} />
          <input placeholder="password" type="password" value={creds.password} onChange={e => setCreds({ ...creds, password: e.target.value })} />
          <button onClick={handleLogin}>Login</button>
        </div>
        <div>
          <h3>Last Response</h3>
          <pre style={{ background: '#f6f6f6', padding: 8, height: 200, overflow: 'auto' }}>{output}</pre>
        </div>
      </div>
    )
  }

  return (
    <div style={{ padding: 16, fontFamily: 'Arial, sans-serif' }}>
      <h2>Bookstore Test UI</h2>

      <div style={{ marginBottom: 12 }}>
        <span>Logged in as: <strong>{loggedIn.username}</strong></span>
        <button style={{ marginLeft: 8 }} onClick={logout}>Logout</button>
        {loggedIn.username === 'admin' && (
          <button style={{ marginLeft: 8 }} onClick={() => setView('register')}>Register User</button>
        )}
      </div>

      <div style={{ marginBottom: 12 }}>
        <button onClick={() => { setView('books'); fetchBooks() }}>Books</button>
        <button onClick={() => { setView('cart'); fetchCart() }}>Cart</button>
        <button onClick={() => { setView('orders'); fetchOrders() }}>Orders</button>
        <button onClick={() => checkout()}>Checkout</button>
      </div>

      <div style={{ display: 'flex', gap: 16 }}>
        <div style={{ flex: 1 }}>
          {view === 'register' && loggedIn.username === 'admin' && (
            <div>
              <h3>Register New User</h3>
              <input placeholder="new username" value={creds.username} onChange={e => setCreds({ ...creds, username: e.target.value })} />
              <input placeholder="password" type="password" value={creds.password} onChange={e => setCreds({ ...creds, password: e.target.value })} />
              <button onClick={handleRegister}>Create User</button>
            </div>
          )}

          {view === 'books' && (
            <div>
              <button onClick={fetchBooks}>Refresh Books</button>
              <ul>
                {books && books.map(b => (
                  <li key={b.id}>{b.title} - {b.author} - {b.price}
                    <button style={{ marginLeft: 8 }} onClick={() => openAddToCartModal(b)}>Add to cart</button>
                  </li>
                ))}
              </ul>
            </div>
          )}

          {view === 'cart' && (
            <div>
              <h3>Your Cart</h3>
              {cart && cart.items && cart.items.length > 0 ? (
                <table>
                  <thead><tr><th>Title</th><th>Qty</th><th>Unit</th><th>Subtotal</th><th>Actions</th></tr></thead>
                  <tbody>
                  {cart.items.map(item => (
                    <tr key={item.bookId}>
                      <td>{item.title}</td>
                      <td>
                        <input type="number" defaultValue={item.quantity} min={1} style={{ width: 60 }} onBlur={e => updateCartQuantity(item.bookId, Number(e.target.value))} />
                      </td>
                      <td>{item.unitPrice}</td>
                      <td>{item.subtotal}</td>
                      <td><button onClick={() => removeFromCart(item.bookId)}>Remove</button></td>
                    </tr>
                  ))}
                  </tbody>
                </table>
              ) : (
                <div>Cart is empty</div>
              )}
            </div>
          )}

          {view === 'orders' && (
            <div>
              <pre>{JSON.stringify(orders, null, 2)}</pre>
            </div>
          )}
        </div>

        <div style={{ width: 420 }}>
          <h3>Last Response</h3>
          <pre style={{ background: '#f6f6f6', padding: 8, height: 400, overflow: 'auto' }}>{output}</pre>
        </div>
      </div>

      {qtyModal.open && (
        <div className="modal-backdrop">
          <div className="modal">
            <h4>Add "{qtyModal.book.title}" to cart</h4>
            <label>Quantity: <input type="number" value={qtyModal.qty} min={1} onChange={e => setQtyModal({ ...qtyModal, qty: Number(e.target.value) })} /></label>
            <div style={{ marginTop: 8 }}>
              <button onClick={confirmAddToCart}>Confirm</button>
              <button onClick={() => setQtyModal({ open: false, book: null, qty: 1 })}>Cancel</button>
            </div>
          </div>
        </div>
      )}

    </div>
  )
}
