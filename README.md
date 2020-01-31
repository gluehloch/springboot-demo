# Simple Spring Boot app to serve date and time
This is a simple Spring Boot application.

# Install
```Bash
git clone git@github.com:gluehloch/springboot-demo.git springboot-demo.git
cd springboot-demo.git
mvn clean package spring-boot:run
```

# Test
Unter der Adresse `http://localhost:8080/demo/ping` gibt der
Server das aktuelle Datum und Uhrzeit aus. Mit den Command-Line
Werkzeugen httpie oder curl bekommt man eine Ausgabe auf der Kommandozeile:
```
http http://localhost:8080/demo/ping
curl http://localhost:8080/demo/ping
``` 

# Spring
Das Projekt baut gegen Spring `2.2.4.RELEASE`
