import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  QueryList,
  Renderer2,
  SimpleChanges,
  ViewChildren,
} from '@angular/core';
import { NextPlayListService } from '../../../../core/services/next-play-list.service';

import { Track } from '../../../../core/models/track';
import {
  animate,
  query,
  stagger,
  style,
  transition,
  trigger,
} from '@angular/animations';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
@Component({
  selector: 'app-next-up',
  standalone: false,
  templateUrl: './next-up.component.html',
  styleUrl: './next-up.component.scss',
  animations: [
    trigger('listAnimation', [
      // Áp dụng cho phần tử bị xóa
      transition(':leave', [
        animate(
          '500ms ease-out',
          style({ transform: 'translateX(100%)', opacity: 0 })
        ),
      ]),

      // Áp dụng cho phần tử mới xuất hiện
      transition(':enter', [
        style({ opacity: 0, transform: 'translateX(100%)' }), // Bắt đầu từ vị trí dưới
        animate(
          '500ms ease-in',
          style({ opacity: 1, transform: 'translateX(0)' })
        ),
      ]),
    ]),
  ],
})
export class NextUpComponent implements OnInit, OnDestroy, OnChanges {
  @ViewChildren('appNextUp', { read: ElementRef })
  appNextUp!: QueryList<ElementRef>;
  @Input('isOpenNextUpList') isOpenNextUpList: boolean = false;
  @Input('tracks') tracks: Track[] = [];
  @Input('playingTrackIndex') playingTrackIndex: number = 0;
  @Input('isFirstTime') isFirstTime: boolean = true;
  @Output() closeEvent = new EventEmitter<void>();
  @Output() dropDragEvent = new EventEmitter<{
    tracks: Track[];
    currentIndex: number;
  }>();
  deletedIndex: number = -1;
  onClose() {
    this.closeEvent.emit();
  }

  constructor(
    private nextPlayListService: NextPlayListService,
    private renderer: Renderer2
  ) {}
  ngOnInit(): void {}
  ngOnChanges(changes: SimpleChanges): void {
    console.log(changes);
    if (changes['tracks']) {
      this.tracks = changes['tracks'].currentValue;
    }
  }
  ngOnDestroy(): void {}
  trackById(index: number, track: any): number {
    return track.id; // Ensure 'id' is unique
  }
  onClear() {
    let isAnimate: boolean = false;
    if (this.playingTrackIndex != 0) {
      isAnimate = true;
    }
    this.nextPlayListService.deleteAll.next();
    this.playingTrackIndex = 0;
    setTimeout(() => {
      this.deletedIndex = isAnimate ? 0 : -1;
    }, 500);
  }
  onDeleteByTrackIndex(trackIndex: number) {
    if (this.playingTrackIndex > trackIndex) this.playingTrackIndex--;
    this.nextPlayListService.deleteTrackByIndex.next(trackIndex);
    setTimeout(() => {
      this.deletedIndex = trackIndex;
    }, 500);
  }
  onDrop(event: CdkDragDrop<any[]>) {
    this.playingTrackIndex = event.currentIndex;
    moveItemInArray(this.tracks, event.previousIndex, event.currentIndex);
    this.dropDragEvent.emit({
      tracks: this.tracks,
      currentIndex: this.playingTrackIndex,
    });
  }
}
