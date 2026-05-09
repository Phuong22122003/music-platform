import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';

@Component({
  selector: 'app-upload',
  standalone: false,
  templateUrl: './upload.component.html',
  styleUrl: './upload.component.scss',
})
export class UploadComponent implements OnInit {
  @ViewChild('inputFiles', { static: false })
  inputFiles!: ElementRef<HTMLInputElement>;
  files: File[] = [];
  isUploadTrack = false;
  isUploadAlbum = false;

  ngOnInit(): void {}
  openFilePicker() {
    this.inputFiles.nativeElement.click();
  }
  onFileSelected(event: Event) {
    const inputTarget = event.target as HTMLInputElement;
    if (inputTarget && inputTarget.files && inputTarget.files.length > 0) {
      this.files = Array.from(inputTarget.files);
      if (this.files.length > 1) this.isUploadAlbum = true;
      else this.isUploadTrack = true;
    }
  }
}
