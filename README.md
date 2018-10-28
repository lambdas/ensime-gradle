# ensime-gradle
Gradle plugin for generating Ensime configuration

## Using

1. Clone plugin source and build it `./gradlew publishToMavenLocal`
2. Add to your build.gradle
```
apply plugin: "com.github.lambdas.ensimegradle"
...
buildscript {
  repositories {
    mavenLocal()
  }
  
  dependencies {
    classpath 'com.github.lambdas:ensime-gradle:0.0.1'
  }
}
```
3. `./gradlew ensime`

That's it.
