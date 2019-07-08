# spring-todo

A simple api for a todo application built with Spring Boot, adhering to the
spec described at [https://www.todobackend.com/](https://www.todobackend.com/).

## Installation

* Clone this repo
* Install Java (at least 8), docker
* Start the postgres container: `docker-compose up -d`
* Run the tests: `./gradlew`
* Boot the application: `./gradlew -Dspring.profiles.active=dev`

## Running the test harness 

* Clone down this repo and cd into it
* Run `./gradlew bootRun -Dspring.profiles.active=dev` to boot up the app
* Navigate to [this
  link](https://www.todobackend.com/specs/index.html?http://localhost:8080/todos)
to run tests.

## Publishing the postgres docker image to dockerhub

Publishing the postgres image so that CI can use it:

* cd into `./postgres`
* Run `docker build -t saylerb/spring-todo:0.0.1 .`
* Run `docker login`
* Run `docker push saylerb/spring-todo:0.0.1`
