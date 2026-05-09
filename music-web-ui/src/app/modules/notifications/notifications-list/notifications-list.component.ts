import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { NotificationService } from '../../../core/services/notification.service';
import { Notification } from '../../../core/models/notification.model';
import { AVATAR_BASE_URL } from '../../../shared/utils/url';
import { formatDistanceToNow } from 'date-fns';
import {
  getNotificationType,
  navigateToNotification,
} from '../../../shared/utils/helper';
import { FollowService } from '../../../core/services/follow.service';
import { AuthService } from '../../../core/services/auth-service';
import { Router } from '@angular/router';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-notifications-list',
  templateUrl: './notifications-list.component.html',
  styleUrls: ['./notifications-list.component.scss'],
  standalone: false,
})
export class NotificationsListComponent implements OnInit, OnDestroy {
  notifications: Notification[] = [];
  loading = false;
  avatarUrl = AVATAR_BASE_URL;
  currentFilter: 'all' | 'unread' = 'all';
  followingMap: { [key: string]: boolean } = {};
  private page = 0;
  private pageSize = 10;
  private allIds: string[] = [];
  hasMore = true;
  throttle = 300;
  scrollDistance = 1;
  scrollUpDistance = 2;
  private subscriptions: Subscription[] = [];
  navigateToNotification = (notification: Notification) => {
    return navigateToNotification(notification);
  };
  notificationType = (notification: Notification) => {
    return getNotificationType(notification);
  };

  @HostListener('window:scroll', ['$event'])
  onScroll(): void {
    if (this.loading || !this.hasMore) return;

    const windowHeight = window.innerHeight;
    const documentHeight = document.documentElement.scrollHeight;
    const scrollTop = window.scrollY || document.documentElement.scrollTop;

    if (windowHeight + scrollTop >= documentHeight - 100) {
      this.loadMoreNotifications();
    }
  }

  constructor(
    private notificationService: NotificationService,
    private followService: FollowService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    // Connect to WebSocket when component initializes
    this.notificationService.connect();
    this.loadData();

    // Subscribe to notifications updates
    this.subscriptions.push(
      this.notificationService.notifications$.subscribe((notifications) => {
        if (this.currentFilter === 'unread') {
          this.notifications = notifications.filter((n) => !n.read);
        } else {
          this.notifications = notifications;
        }

        // Check follow status for all notifications
        notifications.forEach((notification) => {
          if (notification.type === 'follow') {
            this.checkFollowStatus(notification.sender.userId);
          }
        });
      })
    );

    // Subscribe to real-time notifications
    this.subscriptions.push(
      this.notificationService.messages$.subscribe((notification) => {
        if (notification) {
          this.notificationService.addNotification(notification);
          if (notification.type === 'follow') {
            this.checkFollowStatus(notification.sender.userId);
          }
        }
      })
    );
  }

  ngOnDestroy() {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  getTimeAgo(date: string): string {
    return formatDistanceToNow(new Date(date), { addSuffix: true });
  }

  filterNotifications(filter: 'all' | 'unread') {
    this.currentFilter = filter;
    this.notificationService.notifications$.subscribe((allNotifications) => {
      if (filter === 'unread') {
        this.notifications = allNotifications.filter(
          (n: Notification) => !n.read
        );
      } else {
        this.notifications = allNotifications;
      }
    });
  }

  markAsRead(notification: Notification) {
    this.notificationService
      .markNotificationsAsRead([notification.id])
      .subscribe();
    this.router.navigate([this.navigateToNotification(notification)]);
  }

  markAllAsRead() {
    this.notifications.forEach((notification) => (notification.read = true));
    this.notificationService
      .markNotificationsAsRead(
        this.notifications.map((notification) => notification.id)
      )
      .subscribe();
  }

  checkFollowStatus(userId: string) {
    const loggedInUserId = this.authService.getUserId();
    if (loggedInUserId) {
      this.followService
        .isFollowing(loggedInUserId, userId)
        .subscribe((response) => {
          this.followingMap[userId] = response.data;
        });
    }
  }

  toggleFollow(event: Event, userId: string) {
    event.stopPropagation(); // Prevent notification from being marked as read

    if (this.followingMap[userId]) {
      this.followService.unfollowUser(userId).subscribe(() => {
        this.followingMap[userId] = false;
      });
    } else {
      this.followService.followUser({ followingId: userId }).subscribe(() => {
        this.followingMap[userId] = true;
      });
    }
  }

  onScrollDown() {
    if (!this.loading && this.hasMore) {
      this.loadMoreNotifications();
    }
  }

  loadNotifications(ids: string[]) {
    if (!ids.length) {
      this.hasMore = false;
      return;
    }

    this.loading = true;
    this.notificationService.getNotificationByIds(ids).subscribe({
      next: () => {
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.hasMore = false;
      },
    });
  }

  loadMoreNotifications() {
    const start = this.page * this.pageSize;
    const end = start + this.pageSize;
    const nextIds = this.allIds.slice(start, end);

    if (nextIds.length) {
      this.page++;
      this.loadNotifications(nextIds);
    } else {
      this.hasMore = false;
    }
  }

  loadData() {
    this.notificationService.getNotificationIds().subscribe({
      next: (response) => {
        console.log(response);
        this.allIds = response.data;
        this.loadMoreNotifications();
      },
      error: () => {
        this.loading = false;
        this.hasMore = false;
      },
    });
  }
}
