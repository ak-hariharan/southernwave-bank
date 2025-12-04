import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MaterialModule } from './material/material.module';
import { SharedButtonComponent } from './ui/shared-button/shared-button';

@NgModule({
  imports: [
    CommonModule,
    MaterialModule,
    SharedButtonComponent
  ],
  exports: [
    MaterialModule,      
    SharedButtonComponent 
  ]
})
export class SharedModule {}
