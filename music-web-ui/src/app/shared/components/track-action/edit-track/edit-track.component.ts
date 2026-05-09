import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ProfileService } from '../../../../core/services/profile.service';
import { TrackService } from '../../../../core/services/track.service';
import { Track } from '../../../../core/models/track';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-edit-track',
  standalone: false,
  templateUrl: './edit-track.component.html',
  styleUrl: './edit-track.component.scss',
})
export class EditTrackComponent {
  coverUpdate!: File;
  @Input() formFields: {
    name: string;
    label: string;
    type: string;
    columnSpan?: number;
    placeholder?: string;
    validators?: any[];
    options?: {
      imageOrientation?: any;
      widthImage?: number;
      heighImage?: number;
      defaultValue?: any;
      includeDefault?: boolean;
    };
    dataSelect?: any;
  }[] = [];
  @Input() track!: Track;
  @Output() updateInfo = new EventEmitter<Track>();
  @Output() close = new EventEmitter<void>();
  constructor(
    private trackService: TrackService,
    private toast: ToastrService
  ) {}
  onImageSelected(image: any) {
    this.coverUpdate = image;
  }
  submitUpdate(data: any) {
    this.trackService
      .updateTrack(this.track.id, data, this.coverUpdate)
      .subscribe((res) => {
        this.toast.success('Update track successfully');
        this.updateInfo.emit(res.data);
      });
  }
}
