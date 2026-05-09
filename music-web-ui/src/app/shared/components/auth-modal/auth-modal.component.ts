import { Component, EventEmitter, Output } from '@angular/core';
import { AuthService } from '../../../core/services/auth-service';
// import {
//   trigger,
//   state,
//   style,
//   transition,
//   animate,
// } from '@angular/animations';
import { UserCreation } from '../../../core/models/UserCreation';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { environment } from '../../../../environments/environment';
enum Scene {
  EnterUserName,
  EnterPasswordNewAccount,
  EnterPasswordExistedAccount,
  EnterNameBirthdayGender,
  EnterEmailForForgotPassword,
  EnterOTPAndNewPassword,
}

@Component({
  selector: 'app-auth-modal',
  standalone: false,
  templateUrl: './auth-modal.component.html',
  styleUrl: './auth-modal.component.scss',
})
export class AuthModalComponent {
  isLoading = false;
  Scene = Scene;
  username: string = '';
  password: string = '';
  firstName: string = '';
  lastName: string = '';
  email: string = '';
  dob: string = '';
  gender: string = '';
  displayName: string = '';
  currentScene: Scene = Scene.EnterUserName;
  passwordMessage = '';
  usernameMessage = '';
  firstNameMessage = '';
  lastNameMessage = '';
  dobMessage = '';
  genderMessage = '';
  emailMessage = '';
  displayMessage = '';
  isOpen = true;
  isClosing = false;
  BaseUrl = environment.apiBaseUrl;
  otp: string = '';
  newPassword: string = '';
  confirmNewPassword: string = '';
  loadingOTP = false;
  @Output() modalClosed = new EventEmitter<void>();
  constructor(
    private authService: AuthService,
    private router: Router,
    private toast: ToastrService
  ) {}
  onCheckUsername() {
    this.isLoading = true;
    if (this.username.length < 3) {
      this.usernameMessage = 'User is at least 3 characters';
      return;
    }
    this.authService.checkUsernameExisted(this.username).subscribe({
      next: (res) => {
        this.isLoading = false;
        if (res.data['existed']) {
          this.currentScene = Scene.EnterPasswordExistedAccount;
        } else {
          this.currentScene = Scene.EnterPasswordNewAccount;
        }
      },
      error: (err) => {
        this.isLoading = false;
        console.log(err);
      },
    });
  }
  onBackToFirstScene() {
    this.currentScene = Scene.EnterUserName;
    this.passwordMessage = '';
    this.password = '';
  }
  onSignIn() {
    this.isLoading = true;
    if (this.password.length < 8) {
      this.passwordMessage = 'Enter a valid password. (at least 8 characters)';
      this.isLoading = false;
      return;
    }
    this.authService
      .login({ username: this.username, password: this.password })
      .subscribe({
        next: (res) => {
          this.isLoading = false;
          this.authService.saveToken(res.data['token']);
          this.router.navigate(['/discover']);
          this.onCloseModal();
        },
        error: (err) => {
          console.log(err.message);
          this.isLoading = false;
          this.passwordMessage = err.message;
        },
      });
  }
  onNextSceneEnterPasswordNewAccount() {
    if (this.password.length === 0) {
      this.passwordMessage = 'Enter a valid password.';
      this.isLoading = false;
      return;
    }
    if (this.password.length < 8) {
      this.passwordMessage = 'Password must be at least 8 characters';
      this.isLoading = false;
      return;
    }
    this.currentScene = Scene.EnterNameBirthdayGender;
  }

  onCloseModal() {
    this.isClosing = true; // Kích hoạt animation đóng
    setTimeout(() => {
      this.isOpen = false; // Ẩn modal sau animation
      this.isClosing = false; // Reset trạng thái
      this.modalClosed.emit(); // Gửi sự kiện sau khi modal đóng
    }, 300); // Delay = thời gian animation (300ms)
  }

  onSignUp() {
    let isValid = true;

    // Reset error messages
    this.genderMessage = '';
    this.dobMessage = '';
    this.firstNameMessage = '';
    this.lastNameMessage = '';
    this.emailMessage = '';

    // Validate gender
    if (!this.gender || this.gender.length === 0) {
      this.genderMessage = 'Please indicate your gender.';
      isValid = false;
    }

    // Validate date of birth
    if (!this.dob || this.dob.length === 0) {
      this.dobMessage = 'Enter your birthday';
      isValid = false;
    }

    // Validate first name
    if (!this.firstName || this.firstName.length === 0) {
      this.firstNameMessage = 'Enter your first name';
      isValid = false;
    }

    // Validate last name
    if (!this.lastName || this.lastName.length === 0) {
      this.lastNameMessage = 'Enter your last name';
      isValid = false;
    }

    // Validate email
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!this.email || this.email.length === 0) {
      this.emailMessage = 'Enter your email';
      isValid = false;
    } else if (!emailPattern.test(this.email)) {
      this.emailMessage = 'Enter a valid email address';
      isValid = false;
    }

    if (!this.displayName || this.displayName.length === 0) {
      this.displayMessage = 'Enter your display name';
      isValid = false;
    }

    if (!isValid) return;
    this.isLoading = true;
    let user: UserCreation = {
      username: this.username,
      password: this.password,
      firstName: this.firstName,
      lastName: this.lastName,
      dob: this.dob,
      gender: this.gender,
      roles: ['USER'],
      email: this.email,
      displayName: this.displayName,
    };

    this.authService.register(user).subscribe({
      next: (value) => {
        this.isLoading = false;
        this.toast.success('Register successfully', 'Success');
        this.router.navigate(['home']);
        this.onCloseModal();
      },
      error: (err) => {
        this.isLoading = false;
        console.log(err);
        if (err.message === 'Email đã tồn tại') {
          this.emailMessage = 'Email đã tồn tại';
        }
      },
    });
  }
  loginWithGoogle() {
    window.location.href = `http://localhost:8080/identity/oauth2/authorization/google`;
  }
  onClickForgotPassword() {
    this.currentScene = Scene.EnterEmailForForgotPassword;
  }
  onNextSceneEnterOTPAndNewPassword() {
    this.currentScene = Scene.EnterOTPAndNewPassword;
  }
  onClickSendOTP() {
    this.isLoading = true;
    this.authService.sendOTP(this.email).subscribe({
      next: (value) => {
        this.toast.success('OTP sent successfully', 'Success');
        this.currentScene = Scene.EnterOTPAndNewPassword;
        this.isLoading = false;
      },
      error: (err) => {
        this.toast.error(err.message, 'Error');
        this.isLoading = false;
      },
    });
  }
  onClickConfirmOTP() {
    if (this.otp.length === 0) {
      this.toast.error('Please enter your OTP', 'Error');
      return;
    }
    if (this.newPassword.length === 0) {
      this.toast.error('Please enter your new password', 'Error');
      return;
    }
    if (this.confirmNewPassword.length === 0) {
      this.toast.error('Please enter your confirm new password', 'Error');
      return;
    }
    if (this.newPassword !== this.confirmNewPassword) {
      this.toast.error(
        'New password and confirm new password do not match',
        'Error'
      );
      return;
    }
    this.isLoading = true;
    this.authService
      .confirmOTP(this.email, this.otp, this.newPassword)
      .subscribe({
        next: (value) => {
          this.toast.success('OTP confirmed successfully', 'Success');
          this.isLoading = false;
          this.onCloseModal();
        },
        error: (err) => {
          this.toast.error(err.message, 'Error');
          this.isLoading = false;
        },
      });
  }
}
