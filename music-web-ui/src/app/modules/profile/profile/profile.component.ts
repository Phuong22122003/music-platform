import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { ProfileService } from '../../../core/services/profile.service';
import { ActivatedRoute } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { UserProfile } from '../../../core/models/user_profile';

@Component({
  selector: 'app-profile',
  standalone: false,
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
})
export class ProfileComponent implements OnInit {
  isEditProfileOpen = false;
  userProfile!: UserProfile;

  constructor(
    private profileService: ProfileService,
    private route: ActivatedRoute,
    private toast: ToastrService
  ) {}

  openEditProfile() {
    this.isEditProfileOpen = true;
  }

  closeEditProfile() {
    this.isEditProfileOpen = false;
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const userId = params.get('userId');

      if (userId) {
        this.profileService.getProfileById(userId).subscribe({
          next: (res) => {
            this.userProfile = { ...res.data };
          },
          error: (err) => {
            this.toast.error(err.message, 'Error');
          },
        });
      }
    });
  }
  onUpdateProfile(newUserProfile: UserProfile) {
    this.userProfile = { ...newUserProfile };
  }
}
