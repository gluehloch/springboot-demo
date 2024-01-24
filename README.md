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

# Open API
```
http://localhost:8080/v3/api-docs
```

# Spring
Das Projekt baut gegen Spring `3.1.4`

# Actuator Endpoints
Die Spring-Boot Anwendung runterfahren:
```
curl -X POST localhost:8080/actuator/shutdown
```

# REST und das Entity to Json Problem
Im ersten Entfwurf leite ich die Entities direkt aus der REST Schnittstelle raus. Als Beispiel taugt die UserEntity ganz gut. Diese habe ich später um Beziehungen zu Rollen und Privilegien erweitert, die selbst Rückbeziehungen zur UserEntity unterhalten. Bei der Serialisierung kommt es dann zu den typischen Problemen, dass eine rekursive Struktur serialisert werden muss.

Zu dem Thema gibt es zwei Meinungen. Z.B.
 * [Torben Jansen | Dont expose your entities](https://thoughts-on-java.org/dont-expose-entities-in-api/)
 * [Adam Bien](http://www.adam-bien.com/roller/abien/entry/creating_dtos_without_mapping_with)

Beide Begründungen sind einleuchtend. Wie immer kommt es auf den Anwendungsfall an. In simplen Fällen ist die Lösung von Adam Bien völlig ausreichend. Werden die Dinge komplizierter, weil die Serialisierung schwieriger wird, empfehlen sich spezielle DTO Klassen, wie bei Torben Jansen beschrieben.

Ich bin eher ein Fan der strikten Trennung von JSON und Entity Repräsentationen. Man kommt relativ schnell in die Verlegenheit JSON Annotationen zu verwenden. Gemischt mit den JPA Annotationen ergibt das ein schiefes Bild im Code-Editor in meinen Augen.

In dem Package `de.winkler.springboot.user` versuche ich den folgenden Ansatz:
* `UserController`: Alle Rückgabeparameter sind einfache Klassen mit setter und getter.
  Falls Relationen abgebildet werden müssen, dann maximal in einer 1:N Ausprägung.
  Das gleiche gilt für die Eingabeparameter. Die Klassen sind mit dem Postfix `Json` markiert.
  Der Controller ist für das Konvertieren von Entities zu Json zuständig.
* `UserService`: Wird vom `UserController` verwendet. Der `UserService` kennt keine `Json` Typen.
  Der `*Service` spricht nur mit Entities. Bzw. mit Typen der Art `Nickname`.
* `UserRepository`: Kommunikation mit der Datenbank.

TODO: Zur Zeit bin ich der Meinung, dass der Controller keine Konvertierung von Entity zu reinen
'JSON' Klassen vornehmen sollte. Falls DTO/JSON Klassen benötigt werden, sollten diese direkt
aus der Query heraus angelegt werden. Das reduziert für Hibernate die Komplexität der Query,
da keine zu cachenden Entities angelegt werden müssen. Ebenso entfällt für Hibernate die 'Lifecycle' Betrachtung.
Am Ende sind reine DTO-Queries günstiger als Entity-Queries. Insbesondere wenn das Ergebnis
nur für einen lesenden Zugriff benötigt wird.

# Business Code / Service-Klasse ohne Exceptions
Keine Exceptions. Keine NULL-Referenzen. Funktionaler Code. Kann der Code besser lesbar sein?
Im Package 'order' werde ich diese Idee umzusetzen.

# Spring Security
Wo kommen die `@PreAuthorized` und `@RolesAllowed` Annotationen hin? Service oder Controller Ebene?

## Authentication / Authorization
 * Authentication: Wer startet den Request?
 * Authorization: Welche Berechtigungen hat der Request? 

## ... TODO ...
`de.winkler.springboot.SecurityConfiguration` Konfiguriert Spring´s HttpSecurity.

# Zugriff auf das Java Keytool
Im Verzeichnis `./src/test/resources/de/winkler/springboot/jwt` findet sich eine KeyStore Datei `awtest.jks`. In einem produktiven System würde man die KeyStore Datei vielleicht nicht parallel zum Code in einem Repository halten.
 
Mit dem folgenden Befehl wird ein KeyStore angelegt:
```
keytool -genkey -alias awtest -keyalg RSA -keystore awtest.jks -keysize 2048
```

Ein Zertifikat (Public-Key) kann exportiert werden:
```
keytool -export -keystore awtest.jks -alias awtest -file awtest.cer
```

### Beispiele:
* Anzeigen aller Zertifikate/Schlüssel
  ```
  keytool -list -keystore <keystorefilename> -storepass <keystorepassword>
  keytool -list -keystore awtest.jks -storepass awtest666
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
  
# Restassured
[RestAssured](http://rest-assured.io/) für das Testen von REST
Services. Die Syntax kann ich mir nur schwer merken. Vielleicht
verwende ich das Tool zu wenig.

# JPA mit @ManyToMany Beziehungen
Die Entitäten `UserEntity`, `RoleEntity` und `PrivilegeEntity` sind per `@ManyToMany` Relation miteinander verbunden. Einer Rolle sind verschiedene Privilegien zugeordnet. Die Rollen werden wiederum Anwendern zugeordnet.

* Wie sind die N:N Beziehungen zu handhaben? Sollte es eine Master-Entity geben, die die Bearbeitung
  steuert?
* Es ist zu fragen, ob man diese Art der Klassenbeziehungen
  in seiner Java Welt benötigt. Aus Performance Sicht ist diese
  Struktur nicht ideal. Denkbar wäre ein DTO, welches
  die Eigenschaften aus `UserEntity`, `RoleEntity` und
  `PrivilegeEntity` vereint. 

# Constructor vs Field Injection
Hier habe ich mich für Constructor-Injection entschieden.
Erzeugt mehr Schreibaufwand. Dafür kann man die Felder
`final` deklarieren. Bin mir nicht sicher, ob das ein
Vorteil ist.

# OpenAPI, Springfox, Swagger.
Zu finden unter der [lokalen Adresse](http://localhost:8080/swagger-ui/index.html#/).

# Public Key Encryption

Ich generiere mir einen Public/Private Key. Den 'public key' publiziere ich auf einem
Server oder gebe diesen einer bestimmten Person. Bei dem Server melde ich mich mit dem
'public key' an. Die Person verschlüsselt eine Nachricht mit dem 'pubic key' und sendet
mir diese Nachricht. Ich kann mit dem 'private key' die Nachricht entschlüsseln und
lesen.

# Password speichern

* Speicherung im Klartext ist keine Lösung.
* Für ein verschlüsseltes Password gibt es keinen Rückweg. D.h. es kann nicht 
  wieder entschlüsselt werden, um es im Klartext zu lesen.
* Deswegen wöre ein String-Vergleich von Passwörtern keine gute Lösung.

# TODO

* [x] Public/Private key for token creation/validation.
* [ ] Public/Private key reading from KeyStore or certificate file.
* [x] Spring Security einbauen (LoginService verwenden).
* [x] Spring Security Automatismus für die URL /logout.
* [ ] Beispiel für die Verwendung von @Authorized Annotation.
* [ ] Problem: Zugangsgeschütze Seite mit redirect auf '/login'.
* [ ] Rollen anlegen.
* [ ] BCrypt zum sicheren Ablegen von Passwörtern als Hash in der Datenbank verwendet wird. In dem Zusammenhang: Was ist Vault?
* [ ] OpenAPI (Swagger)
* [ ] OpenID
* [ ] Beispiel: REST, JPA-Entity, DTO oder besser ohne? 
* [ ] Spring Data JPA: Pageable. Inklusive Weiterreichung an das Frontend.
* [ ] Angular Demo Anwendung mit Login/Logout und automatischer Umleitung auf login.

## Links
* [Spring Boot Test Baeldung](https://www.baeldung.com/spring-boot-testing)
* [Creating a Java KeyStore](https://www.thomasvitale.com/https-spring-boot-ssl-certificate/)

## Übersetzung

| Englisch    | Deutsch      |
|-------------|--------------|
| authority   | Befugnis / Berechtigung / Autorität |
| authentication | Authentifizierung |
| authorization | Autorisierung / Ermächtigung |
| credentials | Zugangsdaten |
| principal   | Hauptinhaber |

