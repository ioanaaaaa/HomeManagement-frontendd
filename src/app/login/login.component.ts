import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../services/auth-service/auth.service';
import {TokenStorageService} from '../services/token-storage/token-storage.service';

declare function require(path: string);

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  logo = require('src/assets/img/logo.png').default;

  form: any = {
    email: null,
    password: null
  };
  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';

  constructor(private authService: AuthService, private tokenStorage: TokenStorageService, private router: Router) { }

  ngOnInit() {
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
    }
  }

  onSubmit() {
    const { email, password } = this.form;

    console.log('testul vietii' + this.form.email + this.form.password);

    this.authService.login(email, password).subscribe(
      response => {

        if(response.status === 200){
          console.log("bag..." + response);
          this.tokenStorage.saveToken(response);
          //this.tokenStorage.saveUser(response);

          this.isLoginFailed = false;
          this.isLoggedIn = true;
          this.router.navigate(['/home']);
        }
      },
      err => {
        this.errorMessage = err.error.message;
        this.isLoginFailed = true;
      }
    );

    // this.authService.login(email, password)
    //   .pipe(first())
    //   .subscribe(
    //     data => {
    //       console.log('skrrr' + data);
    //       this.router.navigate(['/home']);
    //     },
    //     error => {
    //       this.errorMessage = error.error.message;
    //       this.isLoginFailed = true;
    //     });
  }

}
