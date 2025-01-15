/*
* *****************************************************************************************
* Copyright 2024 By ANYSHOP Project 
* Licensed under the Apache License, Version 2.0;
* *****************************************************************************************
*/
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomHeaderComponent } from './header.component';

describe('HeaderComponent', () => {
  let component: CustomHeaderComponent;
  let fixture: ComponentFixture<CustomHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomHeaderComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
