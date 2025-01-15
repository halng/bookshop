/*
* *****************************************************************************************
* Copyright 2024 By ANYSHOP Project 
* Licensed under the Apache License, Version 2.0;
* *****************************************************************************************
*/

import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { config } from './app/app.config.server';

const bootstrap = () => bootstrapApplication(AppComponent, config);

export default bootstrap;
