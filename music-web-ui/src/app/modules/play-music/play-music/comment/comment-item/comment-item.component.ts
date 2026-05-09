import { Component, Input, OnInit } from '@angular/core';
import { CommentResponse } from '../../../../../core/models/comment/comment-response.model';
import { UserProfile } from '../../../../../core/models/user_profile';
import { ProfileService } from '../../../../../core/services/profile.service';
import getAvatarUrl from '../../../../../shared/utils/get-avatar-url';
import { environment } from '../../../../../../environments/environment';
import { CommentService } from '../../../../../core/services/comment.service';
import { AuthService } from '../../../../../core/services/auth-service';
import { CommentRequest } from '../../../../../core/models/comment/comment-request.model';
import { CommentWithReply } from '../../../../../core/models/comment/comment-with-response';

@Component({
  selector: 'app-comment-item',
  standalone: false,
  templateUrl: './comment-item.component.html',
  styleUrl: './comment-item.component.scss',
})
export class CommentItemComponent implements OnInit {
  @Input() comment!: CommentResponse;
  @Input() trackId!: string;

  avatarBaseUrl: string = environment.fileApi + '/images/avatars';
  userProfile?: UserProfile;
  avatarUrl: string = '';

  likeCount = 0;
  loggedUserId = '';
  replyInputVisible = false;
  replyInput = '';
  currentUserAvatar = '';
  commentWithProfile!: CommentWithReply;
  repComment: CommentWithReply | null = null;

  constructor(
    private profileService: ProfileService,
    private commentService: CommentService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    console.log(this.userProfile);
    if (this.authService.getUserId()) {
      this.loggedUserId = this.authService.getUserId() as string;
      this.profileService.getProfileById(this.loggedUserId).subscribe((res) => {
        this.currentUserAvatar = getAvatarUrl(
          res.data.avatar as string,
          this.avatarBaseUrl
        );
      });
    }
    this.commentWithProfile = { ...this.comment } as CommentWithReply;
    this.enrichCommentWithProfile();
  }
  // private enrichCommentWithProfile(comment: CommentWithReply): void {
  //   this.profileService.getProfileById(comment.userId).subscribe((res) => {
  //     comment.userProfile = res.data;
  //   });
  //   if (comment.repliedUserId) {
  //     this.profileService
  //       .getProfileById(comment.repliedUserId)
  //       .subscribe((res) => {
  //         comment.replyUserProfile = res.data;
  //       });
  //   }
  //   if (comment.replies?.length) {
  // comment.replies = comment.replies.map((reply) => ({
  //   ...reply,
  // })) as CommentWithReply[];
  //     comment.replies.forEach((reply) => this.enrichCommentWithProfile(reply));
  //   } else {
  //     console.log(comment);
  //   }
  // }
  private enrichCommentWithProfile(): void {
    // Lấy profile người viết comment chính
    this.profileService
      .getProfileById(this.commentWithProfile.userId)
      .subscribe((res) => {
        this.commentWithProfile.userProfile = res.data;
      });

    if (!this.commentWithProfile.replies?.length) return;

    // Duyệt qua từng reply
    this.commentWithProfile.replies.forEach((reply) => {
      // Lấy profile người viết reply
      this.profileService.getProfileById(reply.userId).subscribe((res) => {
        reply.userProfile = res.data;
      });

      // Lấy profile của người được reply (repliedUserId)
      if (reply.repliedUserId) {
        this.profileService
          .getProfileById(reply.repliedUserId)
          .subscribe((res) => {
            reply.replyUserProfile = res.data;
          });
      }
    });

    // Sắp xếp reply theo thời gian giảm dần (mới ở dưới)
    this.commentWithProfile.replies.sort(
      (a, b) =>
        new Date(a.commentAt).getTime() - new Date(b.commentAt).getTime()
    );
  }

  toggleLike(comment: CommentWithReply): void {
    const action = comment.isLiked
      ? this.commentService.unlikeComment(comment.id, this.loggedUserId)
      : this.commentService.likeComment(comment.id, this.loggedUserId);

    action.subscribe({
      next: () => {
        comment.isLiked = !comment.isLiked;
        comment.likeCount += comment.isLiked ? 1 : -1;
        console.log(comment);
      },
      error: (err) => console.error('Toggle like failed:', err),
    });
  }

  submitReply(): void {
    const content = this.replyInput.trim();
    if (!content || !this.repComment) return;

    const request: CommentRequest = {
      content: content,
      likeCount: 0,
      trackId: this.trackId,
      userId: this.loggedUserId,
    };

    this.commentService.replyToComment(this.repComment.id, request).subscribe({
      next: (res) => {
        const newReply: CommentWithReply = {
          ...res.data,
          replies: [],
        };

        // Lấy profile người gửi (current user)
        this.profileService
          .getProfileById(this.loggedUserId)
          .subscribe((profileRes) => {
            newReply.userProfile = profileRes.data;

            // Gán repliedUserId = người được trả lời
            newReply.repliedUserId = this.repComment?.userId;

            // Lấy profile của người được reply (hiển thị tên họ sau @)
            if (this.repComment?.userId) {
              this.profileService
                .getProfileById(this.repComment.userId)
                .subscribe((repliedUserRes) => {
                  newReply.replyUserProfile = repliedUserRes.data;

                  // Thêm vào replies
                  if (!this.commentWithProfile.replies) {
                    this.commentWithProfile.replies = [];
                  }
                  this.commentWithProfile.replies.push(newReply);

                  // Reset
                  this.replyInput = '';
                  this.replyInputVisible = false;
                });
            }
          });
      },
      error: (err) => console.error('Reply failed', err),
    });
  }

  toggleReply(commentWithProfile: CommentWithReply) {
    if (this.replyInputVisible) {
      this.replyInputVisible = false;
      this.repComment = null;
    } else {
      this.repComment = commentWithProfile;

      this.replyInputVisible = true;
    }
  }

  getAvatar(avatar: string | null | undefined): string {
    return getAvatarUrl(avatar as string, this.avatarBaseUrl);
  }
}
