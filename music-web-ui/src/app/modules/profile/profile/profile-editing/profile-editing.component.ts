import {
  animate,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Validators } from '@angular/forms';
import { UserProfile } from '../../../../core/models/user_profile';
import { FileService } from '../../../../core/services/file.service';
import { ProfileService } from '../../../../core/services/profile.service';
import { ToastrService } from 'ngx-toastr';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-profile-editing',
  standalone: false,
  templateUrl: './profile-editing.component.html',
  styleUrl: './profile-editing.component.scss',
  animations: [
    trigger('slideModal', [
      state('void', style({ transform: 'translateY(-30%)', opacity: 0 })),
      state('*', style({ transform: 'translateY(0)', opacity: 1 })),
      transition('void => *', [animate('300ms ease-out')]),
      transition('* => void', [animate('300ms ease-in')]),
    ]),
  ],
})
export class ProfileEditingComponent implements OnInit {
  Validators = Validators;
  @Input() userProfile!: UserProfile;
  @Input() show = false;
  @Output() close = new EventEmitter<void>();
  avatarSelected!: File | null;
  originalAvatar!: File | null;
  rendered = false;
  avatarApi!: string;
  coverApi!: string;
  firstTimeLoad = true;
  @Output('updateProfile') updateProfile: EventEmitter<UserProfile> =
    new EventEmitter();
  constructor(
    private fileService: FileService,
    private profileService: ProfileService,
    private toast: ToastrService
  ) {}

  ngOnInit(): void {
    this.avatarApi = this.fileService.avatarApi;
    this.coverApi = this.fileService.coverApi;

    // this.route.paramMap.subscribe((params) => {
    //   const userId = params.get('userId');
    //   if (userId) {
    //     this.profileService.getProfileById(userId).subscribe({
    //       next: (res) => {
    //         console.log(res);
    //         this.userProfile = res.data;
    //         if (this.userProfile?.avatar) {
    //           this.fileService
    //             .getAvatar(this.userProfile.avatar)
    //             .subscribe((file) => {
    //               console.log(file);
    //               this.avatarSelected = file;
    //               this.originalAvatar = file;
    //             });
    //         } else {
    //           this.avatarSelected = null;
    //           this.originalAvatar = null;
    //         }
    //       },
    //       error: (err) => {
    //         this.toast.error(err.message, 'Error');
    //       },
    //     });
    //   }
    // });
  }

  ngOnChanges() {
    if (this.show) {
      this.rendered = true;
    }
    if (
      this.userProfile?.avatar &&
      !this.userProfile?.avatar.startsWith('https://') &&
      this.firstTimeLoad
    ) {
      console.log(this.userProfile.avatar);
      this.fileService.getAvatar(this.userProfile.avatar).subscribe((file) => {
        this.avatarSelected = file;
        this.originalAvatar = file;
      });
      this.firstTimeLoad = false;
    }
  }

  onClose() {
    this.show = false;
  }

  onAnimationDone(event: any) {
    if (event.toState === 'void') {
      this.rendered = false;
      this.close.emit();
    }
  }
  onAvatarSelected(file: File) {
    this.avatarSelected = file;
  }
  onSubmit(res: any) {
    if (this.avatarSelected?.name != this.originalAvatar?.name) {
      this.profileService.updateProfile(res).subscribe({
        next: (res) => {
          this.toast.success('Update profile successfully!', 'Success');

          this.userProfile = res.data;
          const avatar = new FormData();
          avatar.append('avatar', this.avatarSelected as File);
          this.profileService.uploadAvatar(avatar).subscribe((res) => {
            this.userProfile.avatar = res.data.avatarName;
            this.updateProfile.emit(this.userProfile);
          });
        },
        error: (err) => {
          this.toast.error(err.message, 'Error');
        },
      });
    } else {
      this.profileService.updateProfile(res).subscribe({
        next: (res) => {
          this.toast.success('Update profile successfully!', 'Success');
          this.updateProfile.emit({ ...res.data });
          this.userProfile = res.data;
        },
        error: (err) => {
          this.toast.error(err.message, 'Error');
        },
      });
    }
    this.onClose();
  }
}
