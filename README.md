# Spring Boot example application.
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

# Spring Security
Wo kommen die `@PreAuthorized` und `@RolesAllowed` Annotationen hin?
Service oder Controller Ebene?

# Zugriff auf das Java Keytoo
Im Verzeichnis `./src/test/resources/de/winkler/springboot/jwt` findet
sich eine KeyStore Datei `awtest.jks`. In einem Produktivsystem wäre dies
kein gangbarer Weg.

Mit dem folgenden Befehl wird eine KeyStore angelegt:
```
keytool -genkey -alias awtest -keyalg RSA -keystore awtest.jks -keysize 2048
```

Ein Zertifikat (Public-Key) kann exportiert werden:
```
keytool -export -keystore awtest.jks -alias awtest -file awtest.cer
```

Im Code findet sich ein Beispiel (Klasse `KeyStoreService`), wie man aus
der Applikation heraus direkt auf das KeyStore zugreift. 

# JUnit 5 Features
* `@DisplayName` Erlaubt einen aussagekräftigen Anzeigenamen.
* `@Tag` Damit können Tags/Labels vergeben werden. In den Tests
  habe ich Tags wie 'Repository', 'Service' und 'Controller'
  verwendet. Das ist redundant, da meine Test Klassen
  'UserControllerTest' oder 'UserRepositoryTest' heißen.
  Hier fehlt die fachliche Bedeutung. I.d.R. ein Hinweis,
  dass nicht viel 'Fachlichkeit' im Code zu finden ist.
  
# JPA mit @ManyToMany Beziehungen
Die Entitäten `UserEntity`, `RoleEntity` und `PrivilegeEntity`
sind per `@ManyToMany` Relation miteinander verbunden.
Einer Rolle sind verschiedene Privilegien zugeordnet.
Die Rollen werden wiederum Anwendern zugeordnet.

Es ist zu fragen, ob man diese Art der Klassenbeziehungen
in seiner Java Welt benötigt. Aus Performance Sicht ist diese
Struktur nicht ideal. Denkbar wäre ein DTO, welches
die Eigenschaften aus `UserEntity`, `RoleEntity` und
`PrivilegeEntity` vereint. 


# Constructor vs Field Injection
Hier habe ich mal für Constructor-Injection entschieden.
Erzeugt mehr Schreibaufwand. Dafür kann man die Felder
`final` deklarieren. Bin mir nicht sicher, ob das ein
Vorteil.

# TODO

* [x] Public/Private key for token creation/validation.
* [ ] Public/Private key reading from KeyStore or certificate file.
* [x] Spring Security einbauen (LoginService verwenden).
* [x] Spring Security Automatismus fuer die URL /logout.
* [ ] Beispiel für die Verwendung von @Authorized Annotation.
* [ ] Problem: Zugangsgeschuetzte Seite mit redirect auf '/login'.
* [ ] Rollen anlegen.
* [ ] BCrypt zum sicheren Ablegen von Passwörtern als Hash in der Datenbank verwendet wird.

Angular Demo Anwendung mit Login/Logout und automatischer Umleitung auf login.

# Links
* [Spring Boot Test Baeldung](https://www.baeldung.com/spring-boot-testing)