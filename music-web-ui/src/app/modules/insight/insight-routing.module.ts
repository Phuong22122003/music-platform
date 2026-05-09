import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InsightComponent } from './insight.component';

const routes: Routes = [{ path: '', component: InsightComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class InsightRoutingModule {}
