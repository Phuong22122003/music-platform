import { Component, ElementRef, ViewChild } from '@angular/core';
import { Artist } from '../../../../core/models/artist';
import { createPlaceholders } from '../../../../shared/utils/helper';
import { RecommendedService } from '../../../../core/services/recommend_service';
import { UserProfile } from '../../../../core/models/user_profile';

@Component({
  selector: 'app-artists',
  standalone: false,
  templateUrl: './artists.component.html',
  styleUrl: './artists.component.scss',
})
export class ArtistsComponent {
  artists: UserProfile[] = [];
  @ViewChild('artistsCard', { static: true }) artistsCard!: ElementRef;
  constructor(private recommendedService: RecommendedService) {}
  ngOnInit(): void {
    this.recommendedService.getArtistYouShouldKnow().subscribe((res) => {
      this.artists = res.data;
    });
  }
  scollLeft() {
    this.artistsCard.nativeElement.scrollBy({
      left: -200 * 5,
      behavior: 'smooth',
    });
  }
  scollRight() {
    this.artistsCard.nativeElement.scrollBy({
      left: 200 * 5,
      behavior: 'smooth',
    });
  }
  mockData() {
    const mock_artists: Artist[] = [];
    for (let i = 0; i < 10; i++) {
      const track: Artist = {
        id: `${i}`,
        username: 'user' + i,
        profile_picture: '/assets/images/image.png',
      };
      mock_artists.push(track);
    }
    return mock_artists;
  }
  get placeholders(): any[] {
    return createPlaceholders(this.artists);
  }
}
