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

# REST und das Entity to Json Problem

Im ersten Entfwurf leite ich die Entities direkt aus der REST
Schnittstelle raus. Als Beispiel taugt die UserEntity ganz gut.
Diese habe ich später um Beziehungen zu Rollen und Privilegien
erweitert, die selbst Rückbeziehungen zur UserEntity unterhalten.
Bei der Serialisierung kommt es dann zu den typischen
Problemen, dass eine rekursive Struktur serialisert werden muss.
Zu dem Thema gibt es viele Meinungen. Z.B.
 * [Torben Jansen | Dont expose your entities](https://thoughts-on-java.org/dont-expose-entities-in-api/)
 * [Adam Bien](http://www.adam-bien.com/roller/abien/entry/creating_dtos_without_mapping_with)

Beide Meinungen sind richtig. Wie immer kommt es auf den
Anwendungsfall an. In simplen Fällen ist die Lösung von Adam Bien
völlig ausreichend. Werden die Dinge komplizierter, weil die
Serialisierung schwieriger wird, empfehlen sich spezielle DTO
Klassen, wie bei Torben Jansen beschrieben.

Ich bin eher ein Fan der strikten Trennung von JSON und Entity
Repräsentationen. Man kommt relativ schnell in die Verlegenheit
JSON Annotationen zu verwenden. Gemischt mit den JPA Annotationen
ergibt das in meinen Augen schiefes Bild im Code-Editor.

# JUnit 5 Features

* `@DisplayName` Erlaubt einen aussagekräftigen Anzeigenamen.
* `@Tag` Damit können Tags/Labels vergeben werden. In den Tests
  habe ich Tags wie 'Repository', 'Service' und 'Controller'
  verwendet. Das ist redundant, da meine Test Klassen
  'UserControllerTest' oder 'UserRepositoryTest' heißen.
  Hier fehlt die fachliche Bedeutung. I.d.R. ein Hinweis,
  dass nicht viel 'Fachlichkeit' im Code zu finden ist.
  

# TODO

* [x] Public/Private key for token creation/validation.
* [x] Spring Security einbauen (LoginService verwenden).
* [x] Spring Security Automatismus fuer die URL /logout.
* [ ] Problem: Zugangsgeschuetzte Seite mit redirect auf '/login'.
* [ ] Rollen anlegen.
* [ ] BCrypt zum sicheren Ablegen von Passwörtern als Hash in der Datenbank verwendet wird.

Angular Demo Anwendung mit Login/Logout und automatischer Umleitung auf login.

