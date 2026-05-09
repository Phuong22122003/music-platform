import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { Artist } from '../../../core/models/artist';
import { UserProfile } from '../../../core/models/user_profile';
import { AVATAR_BASE_URL } from '../../utils/url';

@Component({
  selector: 'app-artist-card',
  standalone: false,
  templateUrl: './artist-card.component.html',
  styleUrl: './artist-card.component.scss',
})
export class ArtistCardComponent {
  @Input() artist!: UserProfile;
  avatarUrl = AVATAR_BASE_URL;
  constructor(private router: Router) {}
  goToArtist(event: any) {}

  playTrack(event: any) {}

  prevent(event: any) {}

  toggleLike(event: any) {}
}
