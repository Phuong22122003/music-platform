import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { map } from 'rxjs/operators';
@Injectable({
  providedIn: 'root',
})
export class FileService {
  public avatarApi = `${environment.apiBaseUrl}/file-service/images/avatars`;
  public coverApi = `${environment.apiBaseUrl}/file-service/images/covers`;

  constructor(private http: HttpClient) {}

  // Lấy ảnh avatar
  getAvatar(filename: string) {
    return this.http
      .get(`${this.avatarApi}/${filename}`, { responseType: 'blob' })
      .pipe(map((blob) => new File([blob], filename, { type: blob.type })));
  }

  getCoverImage(filename: string) {
    return this.http
      .get(`${this.coverApi}/${filename}`, { responseType: 'blob' })
      .pipe(map((blob) => new File([blob], filename, { type: blob.type })));
  }
}
