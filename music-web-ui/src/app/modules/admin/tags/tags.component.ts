import { Component } from '@angular/core';
import { TagResponse } from '../../../core/models/tag/tag_response.model';
import { TagService } from '../../../core/services/tag_service';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-tags',
  standalone: false,
  templateUrl: './tags.component.html',
  styleUrl: './tags.component.scss',
})
export class TagsComponent {
  tags: TagResponse[] = [];
  newTag: string = '';

  constructor(private tagService: TagService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.loadTags();
  }

  loadTags(): void {
    this.tagService.getAllTags().subscribe((res) => {
      this.tags = res.data;
    });
  }

  addTag(): void {
    if (this.newTag.trim()) {
      this.tagService.createTag(this.newTag).subscribe((res) => {
        this.tags.push(res.data);
        this.newTag = ''; // Reset input field
      });
    }
  }

  deleteTag(tagId: string): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Confirm Deletion',
        description: 'Are you sure you want to delete this tag?',
        confirmText: 'Delete',
        cancelText: 'Cancel',
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.tagService.deleteTag(tagId).subscribe(() => {
          this.tags = this.tags.filter((tag) => tag.id !== tagId);
        });
      }
    });
  }
}
