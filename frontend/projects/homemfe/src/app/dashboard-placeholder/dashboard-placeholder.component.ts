import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from 'shared';

@Component({
  selector: 'app-dashboard-placeholder',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="dashboard-container">
      <header class="dashboard-header">
        <h1>SouthernWave Bank Dashboard</h1>
        <div class="user-info">
          <span>Welcome, <strong>{{ role }}</strong></span>
          <button (click)="logout()" class="logout-btn">Logout</button>
        </div>
      </header>
      
      <main class="dashboard-content">
        <div class="welcome-card">
          <h2>Secure Area</h2>
          <p>This is a temporary placeholder for the upcoming <strong>Dashboard MFE</strong>.</p>
          <div class="status-badge">Authenticated as {{ role }}</div>
        </div>

        <div class="feature-grid">
          <div class="feature-card placeholder">
            <h3>Account Summary</h3>
            <div class="shimmer"></div>
          </div>
          <div class="feature-card placeholder">
            <h3>Recent Transactions</h3>
            <div class="shimmer"></div>
          </div>
          <div class="feature-card placeholder">
            <h3>Quick Actions</h3>
            <div class="shimmer"></div>
          </div>
        </div>
      </main>
    </div>
  `,
  styles: [`
    .dashboard-container {
      min-height: 100vh;
      background: #f8fafc;
      font-family: 'Inter', sans-serif;
    }
    .dashboard-header {
      background: #1e293b;
      color: white;
      padding: 1rem 2rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    .user-info {
      display: flex;
      align-items: center;
      gap: 1.5rem;
    }
    .logout-btn {
      background: #ef4444;
      color: white;
      border: none;
      padding: 0.5rem 1rem;
      border-radius: 6px;
      cursor: pointer;
      transition: background 0.2s;
    }
    .logout-btn:hover {
      background: #dc2626;
    }
    .dashboard-content {
      padding: 2rem;
      max-width: 1200px;
      margin: 0 auto;
    }
    .welcome-card {
      background: white;
      padding: 2rem;
      border-radius: 12px;
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
      margin-bottom: 2rem;
    }
    .status-badge {
      display: inline-block;
      background: #dcfce7;
      color: #166534;
      padding: 0.25rem 0.75rem;
      border-radius: 9999px;
      font-size: 0.875rem;
      font-weight: 600;
      margin-top: 1rem;
    }
    .feature-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: 1.5rem;
    }
    .feature-card {
      background: white;
      padding: 1.5rem;
      border-radius: 12px;
      height: 200px;
      box-shadow: 0 1px 3px rgba(0,0,0,0.1);
    }
    .shimmer {
      background: linear-gradient(90deg, #f1f5f9 25%, #e2e8f0 50%, #f1f5f9 75%);
      background-size: 200% 100%;
      animation: shimmer 1.5s infinite;
      height: 100px;
      border-radius: 8px;
      margin-top: 1rem;
    }
    @keyframes shimmer {
      0% { background-position: 200% 0; }
      100% { background-position: -200% 0; }
    }
  `]
})
export class DashboardPlaceholderComponent implements OnInit {
  role: string | null = '';

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.role = this.authService.getUserRole();
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/landing']);
  }
}
