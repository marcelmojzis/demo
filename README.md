# Demo

## Project Structure

Demo has the following structure:

```
demo/
|-- project/
|   |-- build.properties
|   |-- plugins.sbt
|-- src/main/scala/
|   |-- demo/
|       |-- HelloWorld.scala
|-- .scalafmt.conf
|-- build.sbt
```

- `build.sbt`: Contains the build definition.
- `project/build.properties`: Specifies the SBT version.
- `project/plugins.sbt`: Defines SBT plugins to use in the project.
- `src/main/scala/`: Contains Scala source files.

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

2. **Start SBT**

   Simply type `sbt` and hit enter:

   ```bash
   sbt
   ```

   This will start the SBT interactive shell.

3. **Run HelloWorld**

   Inside the SBT shell, type `run` and hit enter.

   ```
   run
   ```
