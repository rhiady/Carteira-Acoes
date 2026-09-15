import { ComponentFixture, TestBed } from '@angular/core/testing';
import axe from 'axe-core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { describe, expect, it, beforeEach } from 'vitest';
import { CorretoraService } from '../../core/services/corretora.service';
import { CorretoraDialogComponent } from './registration-dialogs.component';

describe('CorretoraDialogComponent', () => {
  let fixture: ComponentFixture<CorretoraDialogComponent>;
  beforeEach(async () => { await TestBed.configureTestingModule({ imports: [CorretoraDialogComponent, MatSnackBarModule], providers: [{ provide: MatDialogRef, useValue: { close() {} } }, { provide: CorretoraService, useValue: { create() {} } }] }).compileComponents(); fixture = TestBed.createComponent(CorretoraDialogComponent); fixture.detectChanges(); });
  it('has no Axe violations in the registration dialog', async () => { const result = await axe.run(fixture.nativeElement as HTMLElement, { rules: { 'color-contrast': { enabled: false } } }); expect(result.violations).toEqual([]); });
});
