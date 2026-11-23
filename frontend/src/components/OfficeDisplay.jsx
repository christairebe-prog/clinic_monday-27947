import { useEffect, useMemo, useState } from 'react'
import axios from 'axios'

function OfficeDisplay() {
  const [offices, setOffices] = useState([])
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [message, setMessage] = useState('')
  const [messageType, setMessageType] = useState('error')
  const [deletingId, setDeletingId] = useState(null)

  async function loadOffices() {
    setLoading(true)
    setMessage('')
    try {
      const response = await axios.get('/api/office/all')
      setOffices(Array.isArray(response.data) ? response.data : [])
    } catch (error) {
      console.error('Display offices error:', error)
      setMessage(
        error.response?.data?.message ||
        'Unable to display offices. Make sure the Clinic Final backend is running on port 8083.'
      )
      setMessageType('error')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadOffices()
  }, [])

  const filteredOffices = useMemo(() => {
    const query = search.trim().toLowerCase()
    if (!query) return offices

    return offices.filter((office) =>
      String(office.name ?? '').toLowerCase().includes(query) ||
      String(office.officeNumber ?? '').toLowerCase().includes(query)
    )
  }, [offices, search])

  async function deleteOffice(office) {
    const confirmed = window.confirm(
      `Are you sure you want to delete ${office.name} (Office ${office.officeNumber})?`
    )
    if (!confirmed) return

    setDeletingId(office.id)
    setMessage('')
    try {
      const response = await axios.delete(
        `/api/office/delete/${encodeURIComponent(office.id)}`
      )
      setOffices((current) => current.filter((item) => item.id !== office.id))
      setMessage(typeof response.data === 'string' ? response.data : 'Office deleted successfully.')
      setMessageType('success')
    } catch (error) {
      console.error('Delete office error:', error)
      const data = error.response?.data
      const errorText = typeof data === 'string' ? data : data?.message || data?.error
      setMessage(errorText || 'Unable to delete this office. It may be assigned to a doctor.')
      setMessageType('error')
    } finally {
      setDeletingId(null)
    }
  }

  return (
    <section className="office-panel">
      <div className="panel-heading">
        <div>
          <h2>All offices</h2>
          <p>Search by office name or office number.</p>
        </div>
        <span className="record-badge">{filteredOffices.length} {filteredOffices.length === 1 ? 'record' : 'records'}</span>
      </div>

      <div className="toolbar">
        <div className="search-wrap">
          <span className="search-symbol">⌕</span>
          <input
            type="search"
            placeholder="Search office name or number..."
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            aria-label="Search offices by name or number"
          />
          {search && <button className="clear-search" onClick={() => setSearch('')} aria-label="Clear search">×</button>}
        </div>
        <button className="refresh-button" onClick={loadOffices} disabled={loading}>
          <span>↻</span> Refresh
        </button>
      </div>

      {message && (
        <div className={`message ${messageType}`} role="status">
          {message}
          <button onClick={() => setMessage('')} aria-label="Dismiss message">×</button>
        </div>
      )}

      <div className="table-scroll">
        <table>
          <thead>
            <tr>
              <th>OFFICE NUMBER</th>
              <th>OFFICE NAME</th>
              <th className="action-head">ACTION</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan="3" className="empty-state"><span className="spinner"></span>Loading office records...</td></tr>
            ) : filteredOffices.length > 0 ? (
              filteredOffices.map((office) => (
                <tr key={office.id}>
                  <td><span className="office-number">OFF-{String(office.officeNumber).padStart(3, '0')}</span></td>
                  <td><div className="office-name"><span className="office-icon">⌂</span><strong>{office.name}</strong></div></td>
                  <td className="actions">
                    <button className="update-button" disabled title="Update will be covered in the next class">Update</button>
                    <button
                      className="delete-button"
                      onClick={() => deleteOffice(office)}
                      disabled={deletingId === office.id}
                    >
                      {deletingId === office.id ? 'Deleting...' : 'Delete'}
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="3" className="empty-state">
                  <span className="empty-icon">⌕</span>
                  <strong>{search ? 'No matching offices found' : 'No offices available'}</strong>
                  <small>{search ? 'Try searching with another name or number.' : 'There are no office records to display.'}</small>
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      <div className="panel-footer">
        <span>Showing {filteredOffices.length} of {offices.length} office records</span>
        <span><i className="connected-dot"></i> Clinic database</span>
      </div>
    </section>
  )
}

export default OfficeDisplay
