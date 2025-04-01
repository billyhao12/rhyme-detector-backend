# Rhyme Detector Backend

This is a Spring Boot application that provides endpoints a user can call to see how song lyrics can be parsed to highlight rhyming patterns. See https://github.com/billyhao12/rhyme-detector for the client-side Next.js app that calls this server.

## Technologies Used
* Spring Boot v3.3.4
* Gradle
* Java v23
* Deployed to Heroku at https://rhyme-detector-backend-4cea028cb9ab.herokuapp.com/
* Using [SpringDoc OpenAPI Starter WebMVC UI](https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-starter-webmvc-ui) to generate a Swagger doc
  - See Swagger doc at `/swagger-ui/index.html` endpoint

## Local Dev Instructions
To develop locally, install the IntelliJ IDE and run the [RhymeDetectorBackendApplication](src/main/java/com/example/rhymedetectorbackend/RhymeDetectorBackendApplication.java). The app will start up at http://localhost:8080/. Restart the app if you would like to see your changes applied.

## Questions
You can contact me with questions at [billyhao12@gmail.com](mailto:billyhao12@gmail.com).
