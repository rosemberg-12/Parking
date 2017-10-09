import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from "@angular/platform-browser";
import { DebugElement } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';


import { CarParkComponent } from './car-park.component';
import { VehicleParkComponent, CardVehicleComponent, CarPark, Vehicle } from '../components';

describe('CarParkComponent', () => {

  let component: CarParkComponent;
  let fixture: ComponentFixture<CarParkComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        CarParkComponent,
        VehicleParkComponent,
        CardVehicleComponent
      ],
      imports: [
        FormsModule,
        HttpClientTestingModule
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CarParkComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.get(HttpTestingController);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  fit('should mark the vehicles were parked', () => {
    fixture.detectChanges();

    const request = httpMock.expectOne('http://localhost:8090/api/vehicles/park');
    
    const car: CarPark = new CarPark();
    car.vehicle = new Vehicle();
    car.vehicle.plate = 'RTD45E';
    car.vehicle.type = 0;
    car.entryDate = '12-10-2017 08:00 A.M'
    car.slotNumber = 1;
    
    const motorcycle: CarPark = new CarPark();
    motorcycle.vehicle = new Vehicle();
    motorcycle.vehicle.plate = 'RTD45G';
    motorcycle.vehicle.cylinder = 100;
    motorcycle.vehicle.type = 1;
    motorcycle.entryDate = '12-10-2017 09:00 A.M'
    motorcycle.slotNumber = 1;

    request.flush([car]);

    fixture.detectChanges();

    const parkedCar = fixture.debugElement.query(By.css("#cars app-card-vehicle"));

    httpMock.verify();
  });

});