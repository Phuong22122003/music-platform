export interface UserCreation {
  username: string;
  password: string;
  firstName: string;
  lastName: string;
  dob: string; // Có thể dùng kiểu `Date` nếu cần
  gender: string; // '0' có thể là Nam, '1' có thể là Nữ
  roles: string[]; // Danh sách quyền (ví dụ: ['ADMIN'])
  email: string;
  displayName: string;
}
