import { Component, OnInit } from '@angular/core';
import {AuthService} from '../services/auth-service/auth.service';
import {Router} from '@angular/router';
import {RegisterUserDto} from '../models/registerUserDto';

declare function require(path: string);

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  logo = require('src/assets/img/logo.png').default;
  form: any = {
    fullname: null,
    email: null,
    password: null
  };
  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) { }

  ngOnInit() {
  }

  onSubmit() {
    const { fullname, email, password } = this.form;

    this.authService.register(fullname, email, password).subscribe(
      data => {
        console.log(data);
        this.isSuccessful = true;
        this.isSignUpFailed = false;
        this.router.navigate(['/login']);
      },
      err => {
        this.errorMessage = err.error.message;
        this.isSignUpFailed = true;
      }
    );
  }

}
