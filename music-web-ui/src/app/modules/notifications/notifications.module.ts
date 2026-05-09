import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { NotificationsListComponent } from './notifications-list/notifications-list.component';
import { NotificationsRoutingModule } from './notifications-routing.module';

@NgModule({
  declarations: [NotificationsListComponent],
  imports: [CommonModule, NotificationsRoutingModule, InfiniteScrollModule],
})
export class NotificationsModule {}
