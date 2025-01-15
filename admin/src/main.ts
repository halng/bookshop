/*
* *****************************************************************************************
* Copyright 2024 By ANYSHOP Project 
* Licensed under the Apache License, Version 2.0;
* *****************************************************************************************
*/

import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
// import 'localstorage-polyfill'

// global['localStorage'] = localStorage;
bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
