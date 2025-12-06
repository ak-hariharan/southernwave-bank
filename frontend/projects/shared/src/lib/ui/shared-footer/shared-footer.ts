import { Component, Input } from '@angular/core';

@Component({
  selector: 'shared-footer',
  imports: [],
  templateUrl: './shared-footer.html',
  styleUrl: './shared-footer.scss',
})
export class SharedFooter {
   currentYear = new Date().getFullYear();  
   @Input() translation: any;

}
