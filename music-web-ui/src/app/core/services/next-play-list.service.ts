import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { Track } from '../models/track';
import { AudioPlayerService } from './audio-player.service';

@Injectable({
  providedIn: 'root',
})
export class NextPlayListService implements OnDestroy {
  addTrackSubject: BehaviorSubject<Track> = new BehaviorSubject<Track>({
    id: '0',
    name: 'Undefined',
    title: 'Undefined',
    description: 'Undefined',
    fileName: '/assets/audios/music.mp3',
    coverImagePath: '/assets/image.png',
    userId: 'Undefined',
    duration: 'Undefined',
    createdAt: 'Undefined',
    username: 'Undefined',
  });
  addTrackListSubject: BehaviorSubject<Track[]> = new BehaviorSubject<Track[]>(
    []
  );
  deleteTrackByIndex: Subject<number> = new Subject<number>();
  deleteAll: Subject<void> = new Subject<void>();
  playPauseTrackInNextPLayListSubject: Subject<{
    index: number;
    type: string;
  }> = new Subject<{
    index: number;
    type: string;
  }>();

  constructor(private audioPlayerService: AudioPlayerService) {}

  ngOnDestroy() {
    // Complete all subjects
    this.addTrackSubject.complete();
    this.addTrackListSubject.complete();
    this.deleteTrackByIndex.complete();
    this.deleteAll.complete();
    this.playPauseTrackInNextPLayListSubject.complete();
  }
}
