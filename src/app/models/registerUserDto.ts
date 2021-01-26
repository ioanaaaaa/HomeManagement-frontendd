export class RegisterUserDto{
  fullname: string;
  email: string;
  password: string;


  constructor(fullname: string, email: string, password: string) {
    this.fullname = fullname;
    this.email = email;
    this.password = password;
  }
}
