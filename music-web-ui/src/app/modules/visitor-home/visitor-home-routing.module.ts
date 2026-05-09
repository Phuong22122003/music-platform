import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { VisitorHomeComponent } from './components/visitor-home/visitor-home.component';

const routes: Routes = [{ path: '', component: VisitorHomeComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class VisitorHomeRoutingModule {}
