## Disc Golf Tournaments Tracker

Web app that displays upcoming dic golf tournaments that take place in [configured](src/main/resources/application.properties) states in a simple table format.

Available at [tournaments-tracker.fly.dev](https://tournaments-tracker.fly.dev/)

### Run locally
Prerequisites:
- Java 17
- Gradle 7.3
```
./gradlew build
./gradlew bootRun
``` 
App will be available at http://localhost:8080.

### Run in Docker
Prerequisites:
- Docker
```
docker build -t tournaments-tracker .
docker run -p 8080:8080 tournaments-tracker
``` 
### Deploy to fly.io
The app is hosted on fly.io.
```
flyctl auth login
flyctl auth docker
flyctl deploy
``` 
