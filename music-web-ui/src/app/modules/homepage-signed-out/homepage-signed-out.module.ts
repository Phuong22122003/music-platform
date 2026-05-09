import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HomepageSignedOutRoutingModule } from './homepage-signed-out-routing.module';
import { HomepageSignedOutComponent } from './components/homepage-signed-out/homepage-signed-out.component';
import { SharedModule } from '../../shared/shared.module';
import { HomepageLogoutAndroidComponent } from './components/homepage-signed-out/homepage-logout-android/homepage-logout-android.component';


@NgModule({
  declarations: [
     HomepageSignedOutComponent,
     HomepageLogoutAndroidComponent
  ],
  imports: [
    CommonModule,
    HomepageSignedOutRoutingModule,
    SharedModule,
  ]
})
export class HomepageSignedOutModule { }
