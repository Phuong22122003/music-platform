import { environment } from '../../../environments/environment';

function getAvatarUrl(avatar: string, baseUrl: string | null): string {
  if (!avatar) return ''; // Không có avatar => trả chuỗi rỗng

  // Nếu avatar là một URL tuyệt đối (đã có http hoặc https), trả về luôn
  if (/^https?:\/\//.test(avatar)) {
    return avatar;
  }

  // Nếu có baseUrl, ghép baseUrl với avatar
  if (baseUrl) {
    return `${baseUrl}/${avatar}`;
  }

  // Mặc định ghép với đường dẫn API file
  return `${environment.fileApi}/images/avatars/${avatar}`;
}

export default getAvatarUrl;
