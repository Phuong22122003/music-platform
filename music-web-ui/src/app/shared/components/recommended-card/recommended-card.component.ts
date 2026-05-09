import { Component, Input } from '@angular/core';
import { Track } from '../../../core/models/track';
import { Router } from '@angular/router';
import { Observable, of } from 'rxjs';

@Component({
  selector: 'app-recommended-card',
  standalone: false,
  templateUrl: './recommended-card.component.html',
  styleUrl: './recommended-card.component.scss'
})
export class RecommendedCardComponent {
  @Input({required:true}) track!:Track;
  isShow = false;
  @Input() isLiked = false;
  @Input() isFollowed =false;
  constructor(private router: Router){}
  goToTrack(){
    // this.router.navigate(['track',this.track.id]);
    alert('go to track')
  }

  playTrack(){
    alert('play')
  }

  prevent(){

  }

  toggleLike():Observable<any>{
    return of(true);
  }
  toggleFollow():Observable<any>{
    return of(true);
  }

}
