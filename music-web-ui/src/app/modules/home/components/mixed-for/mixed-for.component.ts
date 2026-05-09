import { Component } from '@angular/core';
import { Track } from '../../../../core/models/track';
import { RecommendedService } from '../../../../core/services/recommend_service';
import { createPlaceholders } from '../../../../shared/utils/helper';

@Component({
  selector: 'app-mixed-for',
  standalone: false,
  templateUrl: './mixed-for.component.html',
  styleUrl: './mixed-for.component.scss',
})
export class MixedForComponent {
  tracks: Track[] = [];
  constructor(private recommendService: RecommendedService) {}
  ngOnInit(): void {
    this.recommendService.getMixedForUser().subscribe((res) => {
      this.tracks = res.data;
      console.log(this.tracks);
    });
  }

  get placeholders(): any[] {
    return createPlaceholders(this.tracks);
  }
}
