## Disc Golf Tournaments Tracker

Web app that display tournaments, that take place in [configured](src/main/resources/application.properties) 
favorite states and courses, in a simple table format.

### Run locally
Prerequisites:
- Java 17
- Gradle 7.3
```
./gradlew build
./gradlew bootRun
``` 
App will be available on http://localhost:8080/.

### Run in Docker
Prerequisites:
- Docker
```
docker build -t tournaments-tracker .
docker run -p 8080:8080 tournaments-tracker
``` 
### Deploy to fly.io
The app is deployed to fly.io.
```
flyctl auth login
flyctl auth docker
flyctl deploy
``` 
