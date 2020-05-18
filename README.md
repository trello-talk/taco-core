# Taco Core

Backend auth server for Taco rewrite

## Configuration

Create an `application.properties` file with the following properties:
```
spring.datasource.url=<DB_JDBC_URL>
spring.datasource.username=<DB_USERNAME>
spring.datasource.password=<DB_PASSWORD>

discord.client_id=<YOUR_CLIENT_ID>
discord.client_secret=<YOUR_CLIENT_SECRET>
discord.user_agent=Taco Core (https://github.com/trello-talk/taco-core, WIP)

trello.api_key=<YOUR_API_KEY>
trello.api_secret=<YOUR_API_SECRET>
trello.app_name=<YOUR_APP_NAME>

server.servlet.session.timeout=<TIMEOUT_IN_SECONDS>
spring.session.store-type=jdbc
spring.session.jdbc.initialize-schema: always

server.port=<YOUR_PORT>
server.hostname=<YOUR_HOSTNAME>
```
## Code style

- Run `./gradlew initCodeStyle` to pull IntelliJ IDEA project code style file from Gist.

Before commiting your code to repo remember to format your code according to established project guidelines:

  - [Reformat and rearrange code](https://www.jetbrains.com/help/idea/reformat-and-rearrange-code.html) with IDEA.
  - Run `./gradlew spotlessApply` to reformat code with Gradle.

It is also helpful to note that you before applying formatting with Spotless you can run `./gradlew spotlessJavaCheck` to check the proposed formatting before it is applied. Read more information about Spotless [here](https://github.com/diffplug/spotless/tree/master/plugin-gradle).

## Setup

- Run the application by using `./gradlew bootRun`.
- Build the JAR file by using `./gradlew build` and then run the JAR file, as follows:

	`java -jar build/libs/gs-rest-service-0.1.0.jar`

## Testing

- Visit http://localhost:8080/greeting
