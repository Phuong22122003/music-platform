import { NgModule } from '@angular/core';
// import { Router } from '@angular/router';
import { RouterModule, Routes } from '@angular/router';
import { HomepageSignedOutComponent } from './components/homepage-signed-out/homepage-signed-out.component';
import { HomepageLogoutAndroidComponent } from './components/homepage-signed-out/homepage-logout-android/homepage-logout-android.component';
const routes: Routes = [
  { path: '', component: HomepageSignedOutComponent },
  { path: 'logout/android', component: HomepageLogoutAndroidComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class HomepageSignedOutRoutingModule {
}
