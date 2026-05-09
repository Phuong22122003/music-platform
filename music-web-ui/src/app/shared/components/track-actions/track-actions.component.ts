import { Component, Input } from '@angular/core';
import { TrackAction } from '../../../core/models/track_action';

@Component({
  selector: 'app-track-actions',
  standalone: false,
  templateUrl: './track-actions.component.html',
  styleUrl: './track-actions.component.scss',
})
export class TrackActionsComponent {
  @Input() actions: TrackAction[] = [];
}
