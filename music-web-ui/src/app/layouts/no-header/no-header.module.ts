import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NoHeaderComponent } from './no-header.component';
import { SharedModule } from '../../shared/shared.module';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [NoHeaderComponent],
  imports: [CommonModule, SharedModule, RouterModule],
  exports: [NoHeaderComponent],
})
export class NoHeaderModule {}
