import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { throwError, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ErrorHandlerService {
  handleError(error: HttpErrorResponse): Observable<any> {
    console.error(`Error Status:`, error);

    if (error.error) {
      return throwError(() => error.error); // Throw full error body
    }

    return throwError(
      () => new Error('Something went wrong, please try again.')
    );
  }
}
