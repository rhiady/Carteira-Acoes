import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { provideRouter } from '@angular/router';
import axe from 'axe-core';
import { of, throwError } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { UsuarioService } from '../../core/services/usuario.service';
import { UsuariosPage } from './usuarios.page';

const user = { id: 4, nome: 'Ana Silva', email: 'ana@exemplo.com', createdAt: '2026-11-21T12:00:00Z', updatedAt: '2026-11-21T12:00:00Z' };
const page = { content: [user], totalElements: 1, totalPages: 1, size: 10, number: 0, first: true, last: true, numberOfElements: 1, empty: false };

describe('UsuariosPage', () => {
  const service = { list: vi.fn(() => of(page)) };

  beforeEach(() => {
    service.list.mockReset(); service.list.mockReturnValue(of(page));
    TestBed.configureTestingModule({ providers: [provideRouter([]), { provide: UsuarioService, useValue: service }, { provide: MatDialog, useValue: { open: vi.fn() } }] });
  });

  it('loads the protected paginated user listing', () => {
    const fixture: ComponentFixture<UsuariosPage> = TestBed.createComponent(UsuariosPage); fixture.detectChanges();
    expect(service.list).toHaveBeenCalledWith({ page: 0, size: 10, sort: 'nome,asc' });
    expect(fixture.nativeElement.textContent).toContain('Ana Silva');
  });

  it('shows an error and retries the published listing', () => {
    service.list.mockReturnValueOnce(throwError(() => ({ message: 'Acesso negado.' }))).mockReturnValueOnce(of(page));
    const fixture: ComponentFixture<UsuariosPage> = TestBed.createComponent(UsuariosPage); fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Acesso negado.');
    (fixture.nativeElement.querySelector('.error-state button') as HTMLButtonElement).click(); fixture.detectChanges();
    expect(service.list).toHaveBeenCalledTimes(2);
    expect(fixture.nativeElement.textContent).toContain('Ana Silva');
  });

  it('shows the empty state when the published listing has no users', () => {
    service.list.mockReturnValueOnce(of({ ...page, content: [], totalElements: 0, numberOfElements: 0, empty: true }));
    const fixture: ComponentFixture<UsuariosPage> = TestBed.createComponent(UsuariosPage); fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Cadastre um usuário para criar uma carteira.');
  });

  it('has no detectable accessibility violations for the user listing', async () => {
    const fixture: ComponentFixture<UsuariosPage> = TestBed.createComponent(UsuariosPage); fixture.detectChanges();
    expect((await axe.run(fixture.nativeElement, { rules: { 'color-contrast': { enabled: false } } })).violations).toEqual([]);
  });
});
