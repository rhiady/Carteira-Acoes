import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import axe from 'axe-core';
import { describe, expect, it, vi } from 'vitest';
import { AuthService } from '../../core/services/auth.service';
import { AdminPage } from './admin.page';

describe('AdminPage', () => {
  it('is an administrative entry point and links to user management', () => {
    const auth = { currentUser: () => ({ login: 'admin@exemplo.com' }), logout: vi.fn() };
    TestBed.configureTestingModule({ providers: [provideRouter([]), { provide: AuthService, useValue: auth }] });
    const fixture: ComponentFixture<AdminPage> = TestBed.createComponent(AdminPage);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Administração');
    expect(fixture.nativeElement.textContent).toContain('Gestão');
    expect(fixture.nativeElement.querySelector('a[href="/usuarios"]')?.textContent).toContain('Usuários');
    expect(fixture.nativeElement.querySelector('table')).toBeNull();
  });

  it('has no detectable accessibility violations for the administrative entry', async () => {
    const auth = { currentUser: () => ({ login: 'admin@exemplo.com' }), logout: vi.fn() };
    TestBed.configureTestingModule({ providers: [provideRouter([]), { provide: AuthService, useValue: auth }] });
    const fixture: ComponentFixture<AdminPage> = TestBed.createComponent(AdminPage);
    fixture.detectChanges();

    expect((await axe.run(fixture.nativeElement, { rules: { 'color-contrast': { enabled: false } } })).violations).toEqual([]);
  });
});
