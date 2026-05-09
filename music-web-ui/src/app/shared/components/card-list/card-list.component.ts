import { Component, ElementRef, ViewChild } from '@angular/core';

@Component({
  selector: 'app-card-list',
  standalone: false,
  templateUrl: './card-list.component.html',
  styleUrl: './card-list.component.scss'
})
export class CardListComponent {
  @ViewChild('cardList', { static: true }) cardList!: ElementRef;
  scollLeft(){
    this.cardList.nativeElement.scrollBy({ left: -200*5, behavior: "smooth" });
  }
  scollRight(){
    this.cardList.nativeElement.scrollBy({ left: 200*5, behavior: "smooth" });
  }
}
