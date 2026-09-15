import { ComponentFixture, TestBed } from '@angular/core/testing';
import { describe, expect, it } from 'vitest';
import { CurrencyValueComponent } from './financial-display.component';

describe('CurrencyValueComponent', () => {
  it('formats USD with the corresponding locale and currency', () => {
    const fixture: ComponentFixture<CurrencyValueComponent> = TestBed.createComponent(CurrencyValueComponent);
    fixture.componentRef.setInput('value', 1234.5); fixture.componentRef.setInput('currency', 'USD'); fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('$1,234.50');
  });
});
