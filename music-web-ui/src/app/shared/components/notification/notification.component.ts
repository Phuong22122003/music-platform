import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  OnDestroy,
} from '@angular/core';
import { NotificationService } from '../../../core/services/notification.service';
import { Notification } from '../../../core/models/notification.model';
import { AVATAR_BASE_URL } from '../../utils/url';
import { formatDistanceToNow } from 'date-fns';
import { Router } from '@angular/router';
import {
  getNotificationType,
  navigateToNotification,
} from '../../utils/helper';
import { from, Subscription } from 'rxjs';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.scss'],
  standalone: false,
})
export class NotificationComponent implements OnInit, OnDestroy {
  notifications: Notification[] = [];
  @Input() isOpen = false;
  @Output() notificationCount = new EventEmitter<number>();
  avatarUrl = AVATAR_BASE_URL;
  loading = false;
  private subscriptions: Subscription[] = [];

  notificationType = (notification: Notification) => {
    return getNotificationType(notification);
  };

  navigateToNotification = (notification: Notification) => {
    return navigateToNotification(notification);
  };

  constructor(
    private notificationService: NotificationService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadData();

    // Subscribe to notifications updates
    this.subscriptions.push(
      this.notificationService.notifications$.subscribe((notifications) => {
        this.notifications = notifications.slice(0, 5); // Only show latest 5 in dropdown
      })
    );

    // Subscribe to unread count
    this.subscriptions.push(
      this.notificationService.unreadCount$.subscribe((count) => {
        this.notificationCount.emit(count);
      })
    );

    // Subscribe to real-time notifications
    this.subscriptions.push(
      this.notificationService.messages$.subscribe((notification) => {
        if (notification) {
          this.notificationService.addNotification(notification);
        }
      })
    );
  }

  ngOnDestroy() {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  loadNotifications(ids: string[]) {
    this.loading = true;
    this.notificationService.getNotificationByIds(ids).subscribe({
      next: () => {
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  loadData() {
    this.notificationService.getNotificationIds().subscribe((response) => {
      this.loadNotifications(response.data.slice(0, 5));
    });
  }

  toggleNotifications() {
    this.isOpen = !this.isOpen;
    if (this.isOpen) {
      this.loadData();
    }
  }

  markAsRead(notification: Notification) {
    this.notificationService
      .markNotificationsAsRead([notification.id])
      .subscribe();
    this.router.navigate([this.navigateToNotification(notification)]);
  }

  markAllAsRead() {
    const unreadIds = this.notifications
      .filter((notification) => !notification.read)
      .map((notification) => notification.id);

    if (unreadIds.length > 0) {
      this.notificationService.markNotificationsAsRead(unreadIds).subscribe();
    }
  }

  getTimeAgo(date: string): string {
    return formatDistanceToNow(new Date(date), { addSuffix: true });
  }
}
