# Required software
- MySql
- IntelliJ (or another Java IDE)

# How to run / configure
1. Open the project in IntelliJ and import all maven dependencies
2. Set the required db info in application.properties (in /src/main/resources; also set email api key if you want to enable email sending). Note: set username, password and database. Should you want to keep db name, make sure you create the db "easyTask".
3. Run the project (main in "RelovutApplication.java")

## Lombok notes
If the project doesn't compile, namely it complains about missing getters or setters, please follow the steps described here https://stackoverflow.com/questions/24006937/lombok-annotations-do-not-compile-under-intellij-idea
