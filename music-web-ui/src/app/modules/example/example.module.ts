import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExampleListComponent } from './components/example-list/example-list.component';
import { ExampleRoutingModule } from './example-routing.module';
import { SharedModule } from '../../shared/shared.module';

@NgModule({
  declarations: [ExampleListComponent],
  imports: [CommonModule, ExampleRoutingModule, SharedModule],
})
export class ExampleModule {}
