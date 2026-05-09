import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { CommentService } from '../../../../core/services/comment.service';
import { AuthService } from '../../../../core/services/auth-service';
import { CommentRequest } from '../../../../core/models/comment/comment-request.model';
import { ProfileService } from '../../../../core/services/profile.service';
import { UserProfile } from '../../../../core/models/user_profile';
import getAvatarUrl from '../../../../shared/utils/get-avatar-url';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-send-comment',
  standalone: false,
  templateUrl: './send-comment.component.html',
  styleUrl: './send-comment.component.scss',
})
export class SendCommentComponent implements OnInit {
  @Input() trackId!: string;
  profile!: UserProfile;
  @Output() commentAdded = new EventEmitter<void>();
  loggedUserId: string | null = '';
  commentText: string = '';
  avatarUrl = '';

  constructor(
    private commentService: CommentService,
    private authService: AuthService,
    private profileService: ProfileService,
    private toast: ToastrService
  ) {}
  ngOnInit(): void {
    this.loggedUserId = this.authService.getUserId();
    if (!this.loggedUserId) return;
    this.profileService.getProfileById(this.loggedUserId).subscribe({
      next: (res) => {
        this.profile = res.data;
        this.avatarUrl = getAvatarUrl(this.profile.avatar as string, null);
      },
      error: (err) => this.toast.error(err.message),
    });
  }
  sendComment(): void {
    const content = this.commentText.trim();
    if (!content || !this.trackId || !this.loggedUserId) return;

    const commentReq: CommentRequest = {
      content,
      trackId: this.trackId,
      userId: this.loggedUserId,
      likeCount: 0,
    };
    this.commentService.addComment(commentReq).subscribe({
      next: () => {
        this.commentText = '';
        this.commentService.emitCommentAdded();
      },
      error: (err) => this.toast.error(err.message),
    });
  }
}
