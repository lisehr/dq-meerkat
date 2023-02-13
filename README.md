# Dq-MeerKat-core
Core repository for dq-meerkat. Provides fundamental DQ logic. Name might be subject to change

## Contents
DQ-MeerKat (name might be subject to change) is a spring boot application that checks the data quality of data. It is a standalone application, that receives data using custom connectors.

The repository is structured as follows:
* Setup
  * How to set up a development environment
  * Description of necessary tools and versions
  * How to build and test the application
  * How to run the application
* ...
* How to contribute 
* License Declaration

## Setup

DQ-MeerKat is a java spring boot application and is built using maven. Ensure the following tools are installed:
* Java 17+
* Maven 3.8.1+

In order to build the application, run the following command in the root directory of the project:
```
mvn clean install
```
This will build the application and run all tests. The application can be run using the following command:
```
mvn spring-boot:run
```


## How to Contribute 

It is __highly__ recommended to use the feature branch workflow when contributing to this project. This means that all changes should be made in a feature branch, and then merged into the main branch using a pull request. GitHub provides major support in this way since you can use a defined issue to create a branch. The branch can later on be used to create a pull request. 

> Ensure to check the License section before contributing.

### Feature requests
Feature requests should be made using the issue tracker. As this project is purely research funded, it is not guaranteed that all feature requests will be implemented. However, if a feature request is deemed useful, it will be implemented.

### TL;DR
1. Create an issue
2. Create a branch from the issue
3. Make changes
4. Create a pull request
5. Assign it to a reviewer
6. Apply reviewer changes, if any
7. Profit

Depending on the people contributing, reviews might be omitted.

## License

This project is licensed under the GNU Lesser General Public License v3.0. See the [LICENSE.md](LICENSE.md) file for more information.

### Setting up your development environment for LGPLv3
Intellij provides support out of the box to automatically generate copyright headers. In LGPLv3 each source code file needs the following header:
```java
/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
```
To do so in Intellij

* Go To Settings 
* Editor 
* Copyright
* Copyright Profiles
* Add a new profile
* Name it LGPLv3
* Add the above header
* in Copyright make sure to use this profile as the project default

All files, old and new, should now have the correct header.