import { Component, Input } from "@angular/core";
import { MaterialModule } from "../../material/material.module";

@Component({
  selector: 'shared-button',
  templateUrl: './shared-button.html',
  standalone: true,
  imports: [MaterialModule]
})
export class SharedButtonComponent {
  @Input() disabled = false;
}
