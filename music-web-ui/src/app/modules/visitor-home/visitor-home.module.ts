import { NgModule, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { VisitorHomeRoutingModule } from './visitor-home-routing.module';
import { VisitorHomeComponent } from './components/visitor-home/visitor-home.component';
import { SharedModule } from '../../shared/shared.module';
import { AuthService } from '../../core/services/auth-service';
import { Router } from '@angular/router';

@NgModule({
  declarations: [VisitorHomeComponent],
  imports: [CommonModule, VisitorHomeRoutingModule, SharedModule],
})
export class VisitorHomeModule {}
