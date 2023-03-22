## Disc Golf Tournaments Tracker

An app that pulls information about upcoming disc golf tournaments from
[Disc Golf Scene](https://www.discgolfscene.com/tournaments), and then displays the tournaments
that take place in [configured](src/main/resources/application.properties) favorite states and courses 
in a simple table format.

### Run locally
Prerequisites:
- Java 17
- Gradle 7.3
```
./gradlew build
./gradlew bootRun
``` 
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
