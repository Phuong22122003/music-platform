import { Component, Input, OnInit } from '@angular/core';
import { TrackService } from '../../../../core/services/track.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-track-desc',
  standalone: false,
  templateUrl: './track-desc.component.html',
  styleUrl: './track-desc.component.scss',
})
export class TrackDescComponent implements OnInit {
  @Input('desc') desc: string | undefined = `
  Âm nhạc là một phần không thể thiếu trong cuộc sống con người, không chỉ giúp giải trí mà còn truyền tải cảm xúc, câu chuyện, và cả những triết lý sâu xa. Từ thuở sơ khai, con người đã biết dùng tiếng trống, tiếng sáo để giao tiếp và thể hiện nội tâm. Theo thời gian, âm nhạc phát triển vượt bậc, trở thành một ngành nghệ thuật đa dạng với hàng trăm thể loại khác nhau, từ cổ điển, jazz, rock cho đến nhạc điện tử hiện đại. Mỗi giai điệu, mỗi ca từ đều có thể mang lại cảm xúc khác nhau – niềm vui, nỗi buồn, hoài niệm hay khát vọng.

  Trong thời đại số, âm nhạc còn trở nên dễ tiếp cận hơn bao giờ hết. Chỉ cần một chiếc điện thoại thông minh và kết nối mạng, bạn có thể nghe bất cứ bản nhạc nào trên toàn thế giới, bất kể thời gian hay không gian. Các nền tảng như Spotify, YouTube, Apple Music không chỉ thay đổi cách nghe nhạc mà còn ảnh hưởng đến cách nghệ sĩ phát hành và quảng bá sản phẩm của họ.

  Tuy nhiên, chính sự tiện lợi đó cũng mang đến thách thức. Người nghe có xu hướng tiêu thụ nhạc nhanh hơn, dễ "nghe lướt", làm giảm đi sự trân trọng dành cho nghệ thuật và thông điệp bên trong từng bài hát. Vì vậy, đôi khi, hãy dành thời gian để thực sự lắng nghe – không chỉ bằng tai, mà còn bằng tâm hồn. Để âm nhạc không chỉ là âm thanh, mà còn là ký ức, là cảm xúc, là một phần của chính bạn.
`;
  isExpanded: boolean = false;
  lineClamp: number = 3;

  constructor(
    private trackService: TrackService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {}
  toggle() {
    this.isExpanded = !this.isExpanded;
  }
  get paragraphClass() {
    if (this.isExpanded) {
      return 'line-clamp-none';
    }
    return `line-clamp-${this.lineClamp}`;
  }
}
