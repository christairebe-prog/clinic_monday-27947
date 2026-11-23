
import OfficeDisplay from './components/OfficeDisplay'

function App() {
  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-icon">C</div>
          <div>
            <strong>ClinicCare</strong>
            <span>Management system</span>
          </div>
        </div>

        <p className="menu-label">MAIN MENU</p>
        <div className="menu-item active">
          <span className="menu-icon">▦</span>
          <span>Office directory</span>
        </div>

        <div className="sidebar-footer">
          <span className="footer-dot"></span>
          <div>
            <strong>Clinic Office Portal</strong>
            <small>2026</small>
          </div>
        </div>
      </aside>

      <main className="main-area">
        <header className="topbar">
          <span>Workspace <b>/</b> Office directory</span>
          <div className="admin">
            <div className="admin-avatar">CA</div>
            <div>
              <strong>Clinic Admin</strong>
              <small>Administrator</small>
            </div>
          </div>
        </header>

        <div className="page-content">
          <div className="page-title">
            <div>
              <p className="eyebrow">CLINIC MANAGEMENT</p>
              <h1>Office directory</h1>
              <p className="intro">
                View, search and manage the offices registered in your clinic.
              </p>
            </div>
            <span className="page-tag"><i></i> Office records</span>
          </div>

          <OfficeDisplay />

          <footer className="page-footer">
            <span>© 2026 Clinic Office Management System</span>
          </footer>
        </div>
      </main>
    </div>
  )
}

export default App
