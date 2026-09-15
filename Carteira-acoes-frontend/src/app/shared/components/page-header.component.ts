import { Component, input } from '@angular/core';

@Component({
  selector: 'app-page-header',
  template: `<div class="page-heading"><div><h1>{{ title() }}</h1>@if (description()) {<p>{{ description() }}</p>}</div><ng-content /></div>`,
})
export class PageHeaderComponent {
  readonly title = input.required<string>();
  readonly description = input('');
}
