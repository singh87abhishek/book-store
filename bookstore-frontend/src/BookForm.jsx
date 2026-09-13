import React, { useState, useEffect } from 'react'

export default function BookForm({ initial = {}, onSubmit, onCancel }) {
  const [form, setForm] = useState({
    title: '',
    author: '',
    description: '',
    price: 0,
    stock: 0,
    imageUrl: '',
  })

  useEffect(() => {
    if (initial) setForm({
      title: initial.title || '',
      author: initial.author || '',
      description: initial.description || '',
      price: initial.price || 0,
      stock: initial.stock || 0,
      imageUrl: initial.imageUrl || '',
    })
  }, [initial])

  function handleChange(e) {
    const { name, value } = e.target
    setForm(prev => ({ ...prev, [name]: name === 'price' || name === 'stock' ? Number(value) : value }))
  }

  function submit(e) {
    e.preventDefault()
    onSubmit(form)
  }

  return (
    <div style={{ position: 'fixed', left: 0, top: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.3)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <form onSubmit={submit} style={{ background: '#fff', padding: 16, borderRadius: 6, width: 420 }}>
        <h3>{initial && initial.id ? 'Edit Book' : 'Add Book'}</h3>
        <div><label>Title<br/><input name="title" value={form.title} onChange={handleChange} required /></label></div>
        <div><label>Author<br/><input name="author" value={form.author} onChange={handleChange} required /></label></div>
        <div><label>Description<br/><textarea name="description" value={form.description} onChange={handleChange} /></label></div>
        <div><label>Price<br/><input name="price" type="number" step="0.01" value={form.price} onChange={handleChange} required /></label></div>
        <div><label>Stock<br/><input name="stock" type="number" value={form.stock} onChange={handleChange} required /></label></div>
        <div><label>Image URL<br/><input name="imageUrl" value={form.imageUrl} onChange={handleChange} /></label></div>
        <div style={{ marginTop: 12 }}>
          <button type="submit">Save</button>
          <button type="button" onClick={onCancel} style={{ marginLeft: 8 }}>Cancel</button>
        </div>
      </form>
    </div>
  )
}
