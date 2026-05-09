import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from './auth-service';
import { map, Observable, take } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AdminAuthGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(): Observable<boolean> {
    return this.auth.loggedIn$.pipe(
      take(1),
      map((isLoggedIn) => {
        if (!isLoggedIn || !this.auth.isAdmin()) {
          this.router.navigate(['/home']);
          return false;
        }
        return true;
      })
    );
  }
}
