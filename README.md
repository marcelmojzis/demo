# Demo

## Project Structure

Demo has the following structure:

```
demo/
|-- api/
|   |-- src/main/scala/
|       |-- demo/api/
|       |-- ...
|-- project/
|   |-- build.properties
|   |-- plugins.sbt
|   |-- Versions.scala
|-- service/ 
|   |-- src/main/
|       |-- resources
|           |-- logback.xml
|       |-- scala/
|           |-- demo/service
|               |-- Main.scala
|               |-- ... 
|-- .scalafmt.conf 
|-- build.sbt
```

- `build.sbt`: Contains the build definition.
- `project/build.properties`: Specifies the SBT version.
- `project/plugins.sbt`: Defines SBT plugins to use in the project.
- `project/Version.scala`: Contains a list of versions of 3rd party dependencies.
- `api/`: Contains service endpoint definitions and payload models. This module is used by the `service` module.
- `service/`: Contains the backend sources.

## Setup Development Environment on Mac

### Install Homebrew

If you haven't already installed Homebrew, you can do so by running the following command in your terminal:

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

### Install Java

Scala runs on the Java Virtual Machine (JVM), so you'll need to have Java installed. You can install the latest version of OpenJDK using Homebrew:

```bash
brew install openjdk
```

After installation, you can check the Java version by running:

```bash
java -version
```

### Install Scala and SBT

SBT (Scala Build Tool) is the de facto build tool for Scala projects. You can install both Scala and SBT using Homebrew:

```bash
brew install scala sbt
```

After installation, you can check the Scala and SBT versions by running:

```bash
scala -version
sbt -version
```

## Run the Application for Development

1. **Navigate to the Project Directory**

   Open a terminal and navigate to the root directory of your SBT project:

   ```bash
   cd path/to/demo
   ```

2. **Start the backend**

   ```bash
   sbt service/run
   ```
   
   After starting the service, you can check if the backend is running:

   ```bash
   curl http://localhost:8080/api/motd
   ```
   
   This should return a random message of the day.
