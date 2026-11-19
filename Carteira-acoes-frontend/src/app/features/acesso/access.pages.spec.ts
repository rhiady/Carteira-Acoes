import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import axe from 'axe-core';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AuthService } from '../../core/services/auth.service';
import { AdminPage } from './admin.page';

describe('role access pages', () => {
  const auth = { logout: vi.fn(), hasAuthority: vi.fn(() => false), currentUser: vi.fn() };

  beforeEach(() => {
    auth.logout.mockReset(); auth.hasAuthority.mockReset(); auth.hasAuthority.mockReturnValue(false); auth.currentUser.mockReset();
    auth.currentUser.mockReturnValue({ id: 1, login: 'admin@carteira.local', authorities: ['ROLE_ADMIN'] });
    TestBed.configureTestingModule({ providers: [provideRouter([]), { provide: AuthService, useValue: auth }] });
  });

  it('renders the administrative navigation without fabricated data', async () => {
    const fixture: ComponentFixture<AdminPage> = TestBed.createComponent(AdminPage);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Administração');
    expect(fixture.nativeElement.textContent).toContain('admin@carteira.local');
    expect(fixture.nativeElement.querySelectorAll('a')).toHaveLength(3);
    expect(fixture.nativeElement.querySelector('a.role-entry__link--primary')?.getAttribute('href')).toBe('/usuarios');
    expect((await axe.run(fixture.nativeElement, { rules: { 'color-contrast': { enabled: false } } })).violations).toEqual([]);
  });
});
