## Disc Golf Scene Scraper

A web scraper that pulls information about upcoming disc golf tournaments from
[Disc Golf Scene](https://www.discgolfscene.com/tournaments). The app provides a 
simple table to track the tournaments at [configured](src/main/resources/application.properties) 
favorite locations and courses.

### Prerequisites

- Java 17
- Gradle 7.3

### Run locally
```
./gradlew build
./gradlew bootRun
``` 
### Run in Docker
```
docker build -t my-app .
docker run -p 8080:8080 my-app
``` 
### Deploy to fly.io
The app is deployed to fly.io.
```
flyctl auth login
flyctl auth docker
flyctl deploy
``` 
