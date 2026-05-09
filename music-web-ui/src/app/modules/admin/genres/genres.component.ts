import { Component, ViewEncapsulation } from '@angular/core';
import { GenreService } from '../../../core/services/genre_service';
import { GenreResponse } from '../../../core/models/genre/genre_response.model';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-genres',
  standalone: false,
  templateUrl: './genres.component.html',
  styleUrl: './genres.component.scss',
  encapsulation: ViewEncapsulation.None,
})
export class GenresComponent {
  genres: GenreResponse[] = [];
  newGenre = '';

  constructor(private genreService: GenreService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.loadGenres();
  }

  loadGenres(): void {
    this.genreService
      .getAllGenres()
      .subscribe((res) => (this.genres = res.data));
  }

  addGenre(): void {
    if (this.newGenre.trim()) {
      this.genreService.createGenre(this.newGenre).subscribe(() => {
        this.newGenre = '';
        this.loadGenres();
      });
    }
  }

  deleteGenre(id: string): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Confirm Deletion',
        description: 'Are you sure you want to delete this genre?',
        confirmText: 'Delete',
        cancelText: 'Cancel',
      },
    });
    dialogRef.afterClosed().subscribe((res) => {
      if (res) {
        this.genreService.deleteGenre(id).subscribe((res) => {
          console.log(res);
          this.loadGenres();
        });
      }
    });
  }
}
