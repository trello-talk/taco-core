# Taco Core

Backend auth server for Taco rewrite

## Configuration

Copy the `.application.properties` file to `application.properties`

## Code style

- Run `./gradlew initCodeStyle` to pull IntelliJ IDEA project code style file from Gist.

Before commiting your code to repo remember to format your code according to established project guidelines:

  - [Reformat and rearrange code](https://www.jetbrains.com/help/idea/reformat-and-rearrange-code.html) with IDEA.
  - Run `./gradlew spotlessApply` to reformat code with Gradle.

It is also helpful to note that you before applying formatting with Spotless you can run `./gradlew spotlessJavaCheck` to check the proposed formatting before it is applied. Read more information about Spotless [here](https://github.com/diffplug/spotless/tree/master/plugin-gradle).

## Setup

- Run the application by using `./gradlew bootRun`.
- Build the JAR file by using `./gradlew buildJar`.

## Testing

- Visit http://localhost:8080/
