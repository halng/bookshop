/*
* *****************************************************************************************
* Copyright 2024 By ANYSHOP Project 
* Licensed under the Apache License, Version 2.0;
* *****************************************************************************************
*/
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomFooterComponent } from './footer.component';

describe('FooterComponent', () => {
  let component: CustomFooterComponent;
  let fixture: ComponentFixture<CustomFooterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomFooterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomFooterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
