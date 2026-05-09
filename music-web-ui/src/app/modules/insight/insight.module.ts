import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { InsightRoutingModule } from './insight-routing.module';
import { InsightComponent } from './insight.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';

@NgModule({
  declarations: [InsightComponent],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    NgSelectModule,
    MatIconModule,
    NgxChartsModule,
    InsightRoutingModule,
  ],
})
export class InsightModule {}
