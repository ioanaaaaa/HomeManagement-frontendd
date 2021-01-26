import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {JwtHelperService} from '@auth0/angular-jwt';
import {TokenStorageService} from '../token-storage/token-storage.service';

const AUTH_API = environment.apiBase + '/users/';
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient, public jwtHelper: JwtHelperService, private tokenStorageService: TokenStorageService) { }

  login(email: string, password: string): Observable<any> {
    return this.http.post<any>(AUTH_API + 'login', {email, password}, httpOptions);
  }

  register(fullname: string, email: string, password: string): Observable<any> {
    return this.http.post<any>(AUTH_API + 'register', {fullname, email, password}, httpOptions);
  }


  public isAuthenticated(): boolean {
    const token = this.tokenStorageService.getToken();
    console.log(token);
    // verifica daca tokenul e expirat si returneaza adevarat sau fals, imi trebuie pentru auth-guard
    return !this.jwtHelper.isTokenExpired(token);
  }
}


