/*
* *****************************************************************************************
* Copyright 2024 By ANYSHOP Project 
* Licensed under the Apache License, Version 2.0;
* *****************************************************************************************
*/

import { Component } from '@angular/core';
import { FooterComponent } from '@coreui/angular';

@Component({
    selector: 'app-footer',
    templateUrl: './footer.component.html',
    styleUrls: ['./footer.component.scss'],
    standalone: true,
})
export class CustomFooterComponent extends FooterComponent {
  constructor() {
    super();
  }
}
