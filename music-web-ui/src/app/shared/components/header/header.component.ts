import { Component, ElementRef, HostListener, OnInit } from '@angular/core';
import { AuthService } from '../../../core/services/auth-service';
import { ProfileService } from '../../../core/services/profile.service';
import { UserProfile } from '../../../core/models/user_profile';
import { environment } from '../../../../environments/environment';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import getImageUrl from '../../utils/get-avatar-url';
import { NotificationService } from '../../../core/services/notification.service';
import { UiNotificationService } from '../../../core/services/ui-notification.service';
@Component({
  selector: 'app-header',
  standalone: false,
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent implements OnInit {
  isLoggedIn!: boolean;
  userProfile!: UserProfile;
  avatarBaseUrl = environment.fileApi + '/images/avatars';
  intervalSubscription!: Subscription;
  avatarUrl = '';
  isAdmin = false;
  isOpenNotification = false;
  notificationCount = 0;
  constructor(
    private authService: AuthService,
    private profileService: ProfileService,
    private router: Router,
    private notificationService: NotificationService,
    private elementRef: ElementRef,
    private uiNotification: UiNotificationService
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin();
    this.intervalSubscription = this.authService.loggedIn$.subscribe(
      (isLoggedIn) => {
        this.isLoggedIn = isLoggedIn;
        if (this.isLoggedIn) {
          this.profileService
            .getProfileById(this.authService.getUserId() as string)
            .subscribe((apiResponse) => {
              this.userProfile = apiResponse.data;
              this.avatarUrl = getImageUrl(
                this.userProfile.avatar as string,
                this.avatarBaseUrl
              );
            });
        }
      }
    );
    this.notificationService.unreadCount$.subscribe((count) => {
      this.notificationCount = count;
    });
  }
  onSearch(query: any) {
    console.log(query);
    const trimmed = query.trim();
    if (trimmed) {
      this.router.navigate(['/search/tracks'], { queryParams: { q: trimmed } });
    }
  }
  onShowAuthModal() {
    this.authService.isOpenAuthModal.next(true);
  }
  onCloseModal() {
    this.authService.isOpenAuthModal.next(false);
  }
  onLogOut() {
    this.authService.logout();
    this.router.navigate(['home']);
  }
  toggleNotification() {
    this.isOpenNotification = !this.isOpenNotification;
  }

  @HostListener('document:click', ['$event'])
  clickOutside(event: Event) {
    // Check if click is outside notification area
    const notificationArea =
      this.elementRef.nativeElement.querySelector('.notification-area');
    const notificationIcon =
      this.elementRef.nativeElement.querySelector('.notification-icon');

    if (notificationArea && this.isOpenNotification) {
      const isClickInside =
        notificationArea.contains(event.target) ||
        notificationIcon.contains(event.target);

      if (!isClickInside) {
        this.isOpenNotification = false;
      }
    }
  }
  showComingSoon() {
    this.uiNotification.showComingSoon();
  }
}
