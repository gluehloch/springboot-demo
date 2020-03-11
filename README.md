# Simple Spring Boot app to serve date and time
This is a simple Spring Boot application. Here you find the following examples:
 * RestController
 * Spring Security
 * Spring JPA support
 * [Spring-Boot-Devtools](https://docs.spring.io/spring-boot/docs/current/reference/html/using-spring-boot.html#using-boot-devtools). Automatic classpath reload on change.

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

# User anlegen / zeigen / aendern

User anlegen:
```
curl -X POST --header 'Content-Type: application/json' \
    -d '{ "nickname": "Frosch", "name": "Winkler", "firstname": "Andre", "password": "password" }' \
    'http://localhost:8080/user'
```

Alle User abfragen:
```
curl http://localhost:8080/user
```

# Einloggen / Ausloggen

Einloggen:
```
curl -X POST --header 'Content-Type: application/json' \
    'http://localhost:8080/login?nickname=Frosch&password=password'
```

# Spring
Das Projekt baut gegen Spring `2.2.4.RELEASE`

# Actuator Endpoints
Die Spring-Boot Anwendung runterfahren:
```
curl -X POST localhost:8080/actuator/shutdown
```


# TODO

* [x] Public/Private key for token creation/validation.
* [ ] Spring Security auf den LoginService aufbauen.
