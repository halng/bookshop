/*
* *****************************************************************************************
* Copyright 2024 By ANYSHOP Project 
* Licensed under the Apache License, Version 2.0;
* *****************************************************************************************
*/

import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'create',
    loadComponent: () =>
      import('./create/create.component').then((m) => m.CreateComponent),
  },
  {
    path: "view",
    loadComponent: () =>
      import("./view/view.component").then((m) => m.ViewComponent),
  }
];
