import { Component, OnInit } from '@angular/core';
import {TokenStorageService} from '../services/token-storage/token-storage.service';
import {Router} from '@angular/router';

declare function require(path: string);

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  logo = require('src/assets/img/logo2.png').default;
  isLoggedIn = false;
  username: string;

  constructor(private tokenStorageService: TokenStorageService, private router: Router) { }

  ngOnInit() {
    this.isLoggedIn = !!this.tokenStorageService.getToken();

    if (this.isLoggedIn) {
      const user = this.tokenStorageService.getUser();
      this.username = user.username;
    }
  }

  logout() {
    this.tokenStorageService.signOut();
    this.router.navigateByUrl('/login');
  }
}
