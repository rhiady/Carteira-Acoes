import { ComponentFixture, TestBed } from '@angular/core/testing';
import axe from 'axe-core';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ChartPanelComponent } from './chart-panel.component';

describe('ChartPanelComponent', () => {
  let fixture: ComponentFixture<ChartPanelComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [ChartPanelComponent] });
    fixture = TestBed.createComponent(ChartPanelComponent);
    fixture.componentRef.setInput('title', 'Evolução da cotação');
    fixture.componentRef.setInput('chart', { type: 'line' });
    fixture.componentRef.setInput('series', []);
  });

  it('renders a chart-specific loading state instead of obsolete chart data', () => {
    fixture.componentRef.setInput('loading', true);
    fixture.componentRef.setInput('hasData', true);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('[role="status"]')?.textContent).toContain('Carregando gráfico');
    expect(fixture.nativeElement.querySelector('apx-chart')).toBeNull();
  });

  it('renders an empty state when no authoritative points are available', () => {
    fixture.componentRef.setInput('hasData', false);
    fixture.componentRef.setInput('emptyMessage', 'Não há histórico.');
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.chart-message')?.textContent).toContain('Não há histórico.');
    expect(fixture.nativeElement.querySelector('apx-chart')).toBeNull();
  });

  it('offers an explicit retry action for retry-safe chart failures', () => {
    const retry = vi.fn();
    fixture.componentInstance.retry.subscribe(retry);
    fixture.componentRef.setInput('error', 'Falha ao carregar o histórico.');
    fixture.componentRef.setInput('retryable', true);
    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('button') as HTMLButtonElement;
    expect(fixture.nativeElement.querySelector('[role="alert"]')?.textContent).toContain('Falha ao carregar');
    button.click();
    expect(retry).toHaveBeenCalledOnce();
  });

  it('renders a visible textual alternative for chart data', () => {
    fixture.componentRef.setInput('summary', ['PETR4: R$ 100,00']);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.chart-text-summary')?.textContent).toContain('PETR4: R$ 100,00');
  });

  it('mounts the ApexCharts host when authoritative data is available', () => {
    fixture.componentRef.setInput('hasData', true);
    fixture.componentRef.setInput('series', [{ name: 'Cotação', data: [{ x: 1, y: 10 }] }]);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.chart-canvas apx-chart')).not.toBeNull();
    expect(fixture.nativeElement.querySelector('.chart-skeleton')).toBeNull();
  });

  it('has an accessible chart region and textual summary without Axe violations', async () => {
    fixture.componentRef.setInput('summary', ['PETR4: + R$ 100,00', 'AAPL: - US$ 20.00']);
    fixture.detectChanges();

    const results = await axe.run(fixture.nativeElement as HTMLElement);

    expect(fixture.nativeElement.querySelector('section[aria-label="Evolução da cotação"]')).not.toBeNull();
    expect(fixture.nativeElement.querySelector('.chart-text-summary')?.textContent).toContain('- US$');
    expect(results.violations).toEqual([]);
  });

  it('keeps semantic financial colors at WCAG AA contrast against chart surfaces', () => {
    expect(contrastRatio('#167347', '#ffffff')).toBeGreaterThanOrEqual(4.5);
    expect(contrastRatio('#b42318', '#ffffff')).toBeGreaterThanOrEqual(4.5);
    expect(contrastRatio('#5b6871', '#ffffff')).toBeGreaterThanOrEqual(4.5);
  });
});

const contrastRatio = (foreground: string, background: string) => {
  const luminance = (color: string) => {
    const values = color.slice(1).match(/.{2}/g)?.map((part) => Number.parseInt(part, 16) / 255) ?? [];
    const [red, green, blue] = values.map((value) => value <= .04045 ? value / 12.92 : ((value + .055) / 1.055) ** 2.4);
    return .2126 * red + .7152 * green + .0722 * blue;
  };
  const [first, second] = [luminance(foreground), luminance(background)].sort((left, right) => right - left);
  return (first + .05) / (second + .05);
};
