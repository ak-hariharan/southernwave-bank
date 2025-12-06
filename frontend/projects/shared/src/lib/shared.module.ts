import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MaterialModule } from './material/material.module';
import { SharedButtonComponent } from './ui/shared-button/shared-button';
import { SharedFooter } from './ui/shared-footer/shared-footer';

@NgModule({
  imports: [
    CommonModule,
    MaterialModule,
    SharedButtonComponent,
    SharedFooter
  ],
  exports: [
    MaterialModule,      
    SharedButtonComponent,
    SharedFooter
  ]
})
export class SharedModule {}
