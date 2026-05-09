import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TrackManagementComponent } from './track-management.component';

const routes: Routes = [{ path: '', component: TrackManagementComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TrackManagementRoutingModule {}
