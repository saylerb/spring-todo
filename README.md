# spring-todo

A simple api for a todo application built with Spring Boot, adhering to the
spec described at [https://www.todobackend.com/](https://www.todobackend.com/).

## Installation

* Clone this repo
* Install Java (at least 8), docker
* Start the postgres container: `docker-compose up -d`
* Run the tests: `./gradlew`
* Boot the application: `./gradlew bootRun`

## Running the test harness 

* Clone down this repo and cd into it
* Run `./gradlew bootRun` to boot up the app
* Navigate to [this
  link](https://www.todobackend.com/specs/index.html?http://localhost:8080/todos)
to run tests.
