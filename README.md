<img src="./etc/logo-bdsl-interpreter-dark.png" style="zoom:90%;" />

# BDSL Interpreter Framework

This project contains an interpreter for [BDSL](https://git-st.inf.tu-dresden.de/bigraphs/bigraph-dsl-ce) (a DSL for "computational bigraphs") for learning and experimentation when working with the bigraph theory of Robin Milner.
It provides an easier start into the theory by allowing the user to specify arbitrary bigraph expressions and bigraphical reactive systems by using a DSL called BDSL.
Moreover, the language includes some simple functions to print and export the defined bigraphs and also basic analysis techniques such as model checking.

The framework and grammar can be extended and facilitate "bigraphical language engineering."

|                              | Release | Development    |
|------------------------------|---------|----------------|
| **BDSL Interpreter**         | 2.0.1   | 3.0.0-SNAPSHOT |
| BDSL Core Elements (Grammar) | 2.0.1   | 2.0.1          |
| Bigraph Framework            | 2.0.1   | 2.0.1          |

### Structure

This project comprises 3 modules:
- `bdsl-interpreter-cli`: A command-line interface for the algebraic bigraph interpreter.
- `bdsl-interpreter-core`: the main functionality of the bigraph interpreter.
- `bdsl-execution-common`: an abstract BDSL statement execution framework to specify and manage BDSL program execution. It is used by the CLI.

## Getting Started

### Maven Configuration

**Dependencies**

```xml
<dependencies>
    <!-- Core: Low-level building blocks of the interpreter -->
    <dependency>
        <groupId>org.bigraphs.dsl.interpreter</groupId>
        <artifactId>bdsl-interpreter-core</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    <!-- CLI: the high-level building block to access command-line interface parser -->
    <dependency>
        <groupId>org.bigraphs.dsl.interpreter</groupId>
        <artifactId>bdsl-interpreter-cli</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    <!-- Exec: Advanced BDSL Statement Executions -->
    <dependency>
        <groupId>org.bigraphs.dsl.interpreter</groupId>
        <version>1.0.0-SNAPSHOT</version>
        <artifactId>bdsl-execution-common</artifactId>
    </dependency>
</dependencies>
```

**SNAPSHOT Releases**

For SNAPSHOT release configure the following repository:

```xml
<repositories>
    <repository>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
        <id>ossrh</id>
        <url>https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/</url>
    </repository>
</repositories>
```

### Usage Instructions

> Related Module: `bdsl-interpreter-cli`

```bash
java -jar bdsl.jar --main=program.bdsl <other-options...>
```

> Refer also to the BDSL user guide: https://nbn-resolving.org/urn:nbn:de:bsz:14-qucosa2-752170
> 
> It provides a more detailed introduction into BDSL and the command-line tool

## Development: Build Configuration

**Requirements:** 
- Java >= 17
- Maven >= 3.8.3

To build the project and all its individual components:

```shell
mvn clean install -DskipTests
```

To build the runnable BDSL CLI interpreter tool:
```bash
$ cd ./bdsl-interpreter-cli
$ mvn package -Pbuild-cli
```
After executing the command above, the command-line interpreter tool for BDSL is then available under `./bdsl-interpreter-cli/target/` from the root directory of this project.
See also the `README.md` of the respective submodules for more details on how to use the whole BDSL interpreter framework.

### Dependency Management
This project uses [Bigraph Framework](https://git-st.inf.tu-dresden.de/bigraphs/bigraph-framework) to perform all elementary underlying bigraph model operations.
The grammar, parser and other DSL-related things come from [BDSL Core Elements](https://git-st.inf.tu-dresden.de/bigraphs/bigraph-dsl-ce).

This BDSL interpreter project uses both dependencies to implement the fundamental interpretation logic and the command-line tool of the interpreter.


#### Using a local BDSL-Grammar Dependency

This approach is useful for development purposes, or in case one wants to use the latest version.
To use a local variant of the BDSL dependency (`org.bigraphs.dsl:bdsl-grammar:VERSION`), the Maven profile `useLocalLib` must be used:

```bash
$ mvn package -DskipTests -PuseLocalLib
```

This will install the dependency in the local Maven repository.
The `*.jar` dependency must be placed inside the folder `./etc/libs`.
The version can be specified in the project's root `pom.xml` via the property `bdsl.ce.version`.

### Deployment

To deploy the BDSL Interpreter Framework to the Central Repository:
```bash
mvn clean deploy -DskipTests -P release,ossrh
```

**Settings**

The Sonatype account details (username + password) for the deployment must be provided to the
Maven Sonatype Plugin as used in the project's `pom.xml` file.

The Maven GPG plugin is used to sign the components for the deployment.
It relies on the gpg command being installed:
```shell
sudo apt install gnupg2
```

and the GPG credentials being available e.g. from `settings.xml`.

More details can be found link:https://central.sonatype.org/publish/requirements/gpg/[here].

### Additional Notes

> **Note:** The current `*.jar` of the [Bigraph DSL](https://github.com/bigraph-toolkit-suite/bigraphs.bdsl-core-elements) project is also copied in the `./etc/lib/` folder for testing.
>
> In case you want to use the latest <i>BDSL Core Elements</i> `*.jar`, you have to manually install it. See [BDSL Core Elements](https://github.com/bigraph-toolkit-suite/bigraphs.bdsl-core-elements) for more details.
> 
> The Maven build script can automatically install it in the local Maven repository (usually located under `~/.m2/`).


[//]: # (> **Note 2:** The project uses [Lombok]&#40;https://projectlombok.org/&#41; in order to incorporate **extension methods** for Java.)

[//]: # (> The IDE is not able to properly resolve these extension methods and will show error messages that the used extension methods cannot be resolved. **However, the code will still compile.** )

[//]: # (> An update of the Lombok IntelliJ plugin is released soon supporting extension methods by Lombok in IntelliJ. )


## License

**Bigraph Interpreter** is Open Source software released under the Apache 2.0 license.

```text
   Copyright 2020-present Dominik Grzelak

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
