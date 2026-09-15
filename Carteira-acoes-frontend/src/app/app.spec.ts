import { TestBed } from '@angular/core/testing';
import axe from 'axe-core';
import { provideRouter } from '@angular/router';
import { App } from './app';
import { routes } from './app.routes';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideRouter(routes)],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should render the primary navigation', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('nav[aria-label="Principal"]')?.textContent).toContain('Carteiras');
  });

  it('toggles the navigation from its keyboard-accessible menu button', async () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    await fixture.whenStable();
    const menuButton = fixture.nativeElement.querySelector('button[aria-controls="primary-navigation"]') as HTMLButtonElement;
    const expandedBefore = menuButton.getAttribute('aria-expanded');

    menuButton.click();
    fixture.detectChanges();

    expect(menuButton.getAttribute('aria-expanded')).not.toBe(expandedBefore);
  });

  it('has no automated accessibility violations in the application shell', async () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    await fixture.whenStable();

    const results = await axe.run(fixture.nativeElement as HTMLElement, {
      rules: { 'color-contrast': { enabled: false } },
    });

    expect(results.violations).toEqual([]);
  });
});
