import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import axe from 'axe-core';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AuthService } from '../../core/services/auth.service';
import { AdminPage } from './admin.page';
import { UserAccessPage } from './user-access.page';

describe('role access pages', () => {
  const auth = { logout: vi.fn(), hasAuthority: vi.fn(() => false) };

  beforeEach(() => {
    auth.logout.mockReset(); auth.hasAuthority.mockReset(); auth.hasAuthority.mockReturnValue(false);
    TestBed.configureTestingModule({ providers: [provideRouter([]), { provide: AuthService, useValue: auth }] });
  });

  it('renders the administrative navigation without fabricated data', async () => {
    const fixture: ComponentFixture<AdminPage> = TestBed.createComponent(AdminPage);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Administração');
    expect(fixture.nativeElement.querySelectorAll('a')).toHaveLength(3);
    expect((await axe.run(fixture.nativeElement, { rules: { 'color-contrast': { enabled: false } } })).violations).toEqual([]);
  });

  it('renders the common-user navigation separately', async () => {
    const fixture: ComponentFixture<UserAccessPage> = TestBed.createComponent(UserAccessPage);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Seus investimentos');
    expect(fixture.nativeElement.textContent).not.toContain('Usuários');
    expect((await axe.run(fixture.nativeElement, { rules: { 'color-contrast': { enabled: false } } })).violations).toEqual([]);
  });
});
