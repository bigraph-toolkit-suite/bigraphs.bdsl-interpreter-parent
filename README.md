<img src="./etc/logo-bdsl-interpreter-dark.png" style="zoom:90%;" />

# BDSL Interpreter Framework

| Compatibility                    | Release |
| -------------------------------- | ------- |
| **BDSL Interpreter**             | 2.0.1   |
| \|- BDSL Core Elements (Grammar) | 2.0.1   |
| \|- Bigraph Framework            | 2.0.1   |

## Description

The BDSL Interpreter is a multi-module Maven project
designed to provide an extensible framework for interpreting BDSL scripts. 
Each module in the project focuses on a specific set of features,
enabling a modular and maintainable architecture for developing and executing BDSL-based applications.
Below is an overview of the project and its modules:

**Project Modules:**
- **bdsl-interpreter-parent:** Acts as the parent module for the entire project, managing shared configurations, dependencies, and build settings for all submodules.
- **bdsl-interpreter-core:** Contains the core features and foundational components of the BDSL interpreter. This module defines the architectural framework and fundamental mechanisms required for interpreting BDSL language constructs.
- **bdsl-interpreter-cli:** Implements a command-line interface for interacting with the BDSL interpreter. This module enables users to execute BDSL models and interact with the interpreter from the terminal.
- **bdsl-execution-common:** Provides the common execution environment and reusable strategies for the BDSL interpreter. This module encapsulates shared execution logic, environment management, and strategy definitions.

The framework and grammar can be extended and facilitate "bigraphical language engineering."

## Getting Started

### Maven Configuration

```xml
<dependencies>
    <!-- Core: Low-level building blocks of the interpreter -->
    <dependency>
        <groupId>org.bigraphs.dsl.interpreter</groupId>
        <artifactId>bdsl-interpreter-core</artifactId>
        <version>VERSION</version>
    </dependency>
    <!-- CLI: the high-level building block to access command-line interface parser -->
    <dependency>
        <groupId>org.bigraphs.dsl.interpreter</groupId>
        <artifactId>bdsl-interpreter-cli</artifactId>
        <version>VERSION</version>
    </dependency>
    <!-- Exec: Advanced BDSL Statement Executions -->
    <dependency>
        <groupId>org.bigraphs.dsl.interpreter</groupId>
        <version>VERSION</version>
        <artifactId>bdsl-execution-common</artifactId>
    </dependency>
</dependencies>
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
$ mvn clean install -DskipTests
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


## License

**Bigraph Interpreter** is Open Source software released under the Apache 2.0 license.

```text
Copyright 2020-present Bigraph Toolkit Developers

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
