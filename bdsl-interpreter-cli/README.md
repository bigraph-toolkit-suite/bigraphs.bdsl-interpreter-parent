<img src="../etc/logo-bdsl-cli-dark.png" style="zoom:90%;" />

# Command-line Interpreter Tool for BDSL

The BDSL interpreter tool can be used from the command-line by issuing the following command in the terminal:
```bash
$ java -jar bdsl-${version}.jar <options...>
```

However, an install script is included to conveniently launch the BDSL interpreter from the terminal by just issuing the following command:
```bash
bdsl <options...>
```
See [Launcher Script](#Launcher-Script) below.

## Build configuration

### Build as Dependency
In order to use the dependency of the "BDSL interpreter tool", it must be build first.
Therefore, use the following command inside a terminal:
```bash
$ mvn clean install -DskipTests
```

The tool (`*.jar`) and sources are then located under `./bdsl-interpreter-cli/target/` from the root directory of the parent project.
Additionally, the sources are installed in the local Maven repository, ready to be used within other Java projects.

### Build as a runnable Tool

To build the runnable BDSL CLI interpreter tool:
```bash
$ mvn package -Pbuild-cli
```

After executing the command above, the command-line interpreter tool for BDSL is then available under `./target/` from this module.

It has the name `bdsl-${version}.jar`.

## Launcher Script

Inside the `./etc/` from the root of the parent project, the install script `ìnstall.sh` is contained.
After this project has been successfully built, the script can be executed to install the BDSL interpreter on the system, so it can be run inside the terminal by just calling `bdsl <options...>`.

> *Note:* The install script has to run with `sudo`. The install script needs to be executed inside the `./etc/` directory.

To remove the files that the install script creates, delete the following files and folders:
- /usr/local/bin/bdsl
- /opt/bdsl/

## Import Instructions

For a Maven-based project, use this dependency inside the project's `pom.xml`:
```xml
<!-- ... -->
<dependencies>
    <dependency>
        <groupId>de.tudresden.inf.st.bigraphs.dsl.interpreter</groupId>
        <artifactId>bdsl-interpreter-cli</artifactId>
        <version>${version}</version>
        <!-- <classifier>sources</classifier> -->
    </dependency>
</dependencies>
<!-- ... -->
```

Replace `${version}` with the current version.

## Remarks

### Configuration

Configuring BDSL programs can be accomplished by setting various parameters in an externalized configuration file.

The following locations are scanned before, which is the default behavior as mentioned in
[https://docs.spring.io/spring-boot/docs/1.0.1.RELEASE/reference/html/boot-features-external-config.html](https://docs.spring.io/spring-boot/docs/1.0.1.RELEASE/reference/html/boot-features-external-config.html):
> A /config subdir of the current directory.
> The current directory
> A classpath /config package
> The classpath root
>
> The list is ordered by precedence (locations higher in the list override lower items).