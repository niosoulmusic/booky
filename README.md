# Spring boot bookmark demo app

## Task Description
This simple demo app has been written to demonstrate use of spring 
for restful Hateoas applications. Althought the data model is pretty simple 
i will add more features in the next version of it to demonstrate microservices
metodologies and patterns.

The project is based on a small bookmarks web service with both rest apis & ui components
which uses the following technologies:

* Test Driven development
* Java 1.8
* Distributed Sessions (Redis)
* Security,csrf, bcrypt encrypted passwords 
* Flyway for db versioning
* Docker compose
* Spring data jpa & jdbc 
* Spring MVC with Spring Boot
* Basic custom metrics with actuator
* Basic AOP
* Database H2 (In-Memory)
* Maven

Users available : 

user / password

admin / password

URLS:

http://localhost:8080 -> web interface

http://localhost:8081 -> redis commander 

http://localhost:8888/internal -> actuator endpoing

http://localhost:8888/internal/mappings -> mappings 