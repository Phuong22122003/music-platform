import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Validators } from '@angular/forms';
import { TrackCreation } from '../../../../core/models/TrackCreation';
import {
  animate,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';
import { TagResponse } from '../../../../core/models/tag/tag_response.model';
import { GenreResponse } from '../../../../core/models/genre/genre_response.model';

@Component({
  selector: 'app-edit-track',
  standalone: false,
  templateUrl: './edit-track.component.html',
  styleUrl: './edit-track.component.scss',
  animations: [
    trigger('slideInOut', [
      state('void', style({ transform: 'translateX(100%)' })), // Trạng thái ban đầu khi component chưa xuất hiện
      state('in', style({ transform: 'translateX(0%)' })), // Trạng thái khi đã xuất hiện
      state('out', style({ transform: 'translateX(100%)' })), // Trạng thái khi biến mất
      transition('void => in', [animate('300ms ease-out')]), // Hiệu ứng vào
      transition('in => out', [animate('300ms ease-in')]), // Hiệu ứng ra
    ]),
  ],
})
export class EditTrackComponent {
  Validators = Validators;
  @Input('trackCreation') trackCreation!: TrackCreation;
  @Input('tagList') tagList: TagResponse[] = [];
  @Input('genreList') genreList: GenreResponse[] = [];
  @Input('disabled') disabled: boolean = false;
  @Output() close = new EventEmitter<void>();
  @Output() submit = new EventEmitter<TrackCreation>();
  panelState: 'in' | 'out' = 'in';
  result!: TrackCreation;
  formData: any;

  onSubmit(data?: any) {
    if (data) {
      this.formData = data;
    }
    if (!this.formData) return;

    const updatedTrack: TrackCreation = {
      ...this.trackCreation,
      title: this.formData.title,
      genreId: this.formData.genre || '',
      tagIds: this.formData.tags || [],
      description: this.formData.description,
    };
    this.submit.emit(updatedTrack);
  }

  onClose(event: Event) {
    event.stopPropagation();
    this.panelState = 'out';
    setTimeout(() => {
      this.close.emit();
    }, 300);
  }
  clickOutside(event: Event) {
    event.stopPropagation();
    this.close.emit();
  }
}
