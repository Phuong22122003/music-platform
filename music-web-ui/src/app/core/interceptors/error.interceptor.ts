import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';
import { ApiResponse } from '../models/api_response';
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(private toastr: ToastrService) {}
  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        // Handle different error types here
        console.log(error);
        let errorMessage = 'Xãy ra lỗi';

        if (error.error instanceof ErrorEvent) {
          // Client-side error
          console.error(`Client Error: ${error.error.message}`);
        } else {
          // Server-side error
          console.error(`Server Error: ${error.status} - ${error.message}`);
          console.error(error);
        }
        if (error.status == 500) {
          errorMessage = 'Hệ thống xãy ra lỗi!';
        } else {
          const res: ApiResponse<void> = error.error;
          console.error(error.error);
          if (res.code != null && res.code >= 101 && res.code <= 150) {
            switch (res.code) {
              // Nhóm 1: General/Auth/Token
              case 101:
                errorMessage = 'Khóa API không hợp lệ';
                break;
              case 102:
                errorMessage = 'Hệ thống hiện đang xảy ra lỗi!';
                break;
              case 103:
                errorMessage = 'Chưa đăng nhập';
                break;
              case 104:
                errorMessage = 'Không có quyền';
                break;
              case 105:
                errorMessage = 'Token không hợp lệ';
                break;
              case 106:
                errorMessage = 'Thiếu user ID';
                break;
              case 107:
                errorMessage = 'Không tìm thấy hồ sơ';
                break;

              // Nhóm 2: User/Account/Email/Password
              case 108:
                errorMessage = 'Người dùng đã tồn tại';
                break;
              case 109:
                errorMessage = 'Người dùng không tồn tại';
                break;
              case 110:
                errorMessage = 'Tên người dùng quá ngắn';
                break;
              case 111:
                errorMessage = 'Thiếu tên hiển thị';
                break;
              case 112:
                errorMessage = 'Email đã tồn tại';
                break;
              case 113:
                errorMessage = 'Thiếu email';
                break;
              case 114:
                errorMessage = 'Email không hợp lệ';
                break;
              case 115:
                errorMessage = 'Mật khẩu sai';
                break;
              case 116:
                errorMessage = 'Mật khẩu quá ngắn';
                break;
              case 117:
                errorMessage = 'Thiếu mật khẩu cũ';
                break;
              case 118:
                errorMessage = 'Thiếu mật khẩu mới';
                break;
              case 119:
                errorMessage = 'Thiếu xác nhận mật khẩu mới';
                break;
              case 120:
                errorMessage = 'Mật khẩu mới không khớp';
                break;
              case 121:
                errorMessage = 'Ngày sinh không hợp lệ';
                break;

              // Nhóm 3: Music (Track/Playlist/Album)
              case 123:
                errorMessage = 'Không tìm thấy track';
                break;
              case 124:
                errorMessage = 'Không tìm thấy playlist';
                break;
              case 125:
                errorMessage = 'Không tìm thấy album';
                break;
              case 126:
                errorMessage = 'Thiếu tiêu đề album';
                break;
              case 127:
                errorMessage = 'Thiếu link album';
                break;
              case 128:
                errorMessage = 'Thiếu genre ID của album';
                break;
              case 129:
                errorMessage = 'Thiếu loại album';
                break;
              case 130:
                errorMessage = 'Thiếu quyền riêng tư album';
                break;
              case 131:
                errorMessage = 'Giá trị quyền riêng tư sai';
                break;
              case 132:
                errorMessage = 'Thiếu nghệ sĩ chính';
                break;
              case 133:
                errorMessage = 'Album đã được thích';
                break;
              case 134:
                errorMessage = 'Album chưa được thích';
                break;
              case 135:
                errorMessage = 'Link album đã tồn tại';
                break;
              case 136:
                errorMessage = 'Đã thích';
                break;
              case 137:
                errorMessage = 'Đã bỏ thích';
                break;

              // Nhóm 4: File (Avatar, Cover, Track)
              case 140:
                errorMessage = 'Không tìm thấy file track';
                break;
              case 141:
                errorMessage = 'Không tìm thấy ảnh đại diện';
                break;
              case 142:
                errorMessage = 'Không tìm thấy ảnh bìa';
                break;

              // Nhóm 5: Comment
              case 143:
                errorMessage = 'Không tìm thấy bình luận';
                break;

              // Nhóm 6: Follow
              case 144:
                errorMessage = 'Đã theo dõi rồi';
                break;
              case 145:
                errorMessage = 'Chưa theo dõi';
                break;

              // Nhóm 7: Tag / Genre
              case 146:
                errorMessage = 'Không tìm thấy tag';
                break;
              case 147:
                errorMessage = 'Tag đã tồn tại';
                break;
              case 148:
                errorMessage = 'Không tìm thấy thể loại';
                break;
              case 149:
                errorMessage = 'Thể loại đã tồn tại';
                break;

              default:
                errorMessage = 'Lỗi không xác định';
                break;
            }
          }
        }
        console.log(errorMessage);
        // Re-throw the error for further handling if needed
        return throwError(() => new Error(errorMessage));
      })
    );
  }
}
