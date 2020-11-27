# OSGi Package Tool

The OSGi Package Tool lets you check locally if your OSGi bundle project resolves against a container without the need to deploy the project in the OSGi environment.


## Prerequisites
* Java 11+

* Your project as valid OSGi bundle JAR

* Valid distro JAR of an OSGi container or access to an aQute Remote Agent deployed in an OSGi container


## Installation
Since the tool is a standalone application, no direct installation within the operating system is necessary.


### Build project
1. Clone the project locally \
`git clone https://github.com/das-Abroxas/osgi-package-tool`

2. Build the project \
`cd osgi-package-tool && mvn clean package`

3. Start the JAR which includes the dependencies \
`java -jar target/osgi-package-tool-<version>-jar-with-dependencies.jar`


### Download pre-built JAR
Download the pre-built JAR of the latest version which can be found in the root directory of the project or use one of the JARs provided from the releases.


### Optional steps

* Move the JAR into a directory of your choice
* Create a file with the name `osgi-package-tool` in a directory which is included in your *$PATH* variable with the following content:
  ```sh
  #!/usr/bin/bash
  java -jar /full/path/to/osgi-package-tool-<version>-jar-with-dependencies.jar
  ```
  
* Make the file executable: \
  `sudo chmod +x /path/to/bin/directory/osgi-package-tool`

* Now you can start the tool from everywhere like a native terminal command: \
  `$ osgi-package-tool`

## Configuration
The configuration can be set within the started application and stores the preferences for each operating system user individually.

The settings can be found in the open application in the menu under **_File_ > _Preferences_**.

* **Container Path:** \
Local path where the distro JAR is created and loaded from.

* **Container Host:** \
IP address or machine name of the server where the OSGi container is located.

* **Container Host Port:** \
Port that the aQute Remote Agent listens to.

* **Global Task Timeout:** \
Timeout in seconds for background tasks like the distro JAR creation.

* **Include Dynamic Imports:** \
Includes the packages from the Dynamic-ImportPackage Header into the project list.


## How to use

#### Load container distro JAR

If there is access to aQute Remote Agent, the distro JAR of the container can be created via the menu and loaded into the tool. In this case the appropriate access settings must first be set in the menu under File > Preferences. Afterwards the distro JAR of the OSGi container can be created and loaded directly into the tool by clicking Edit > Create & Load Distro.

Otherwise the distro JAR must be created on the server of the OSGi Container and then copied locally. The distro JAR can then be loaded by drag and drop on the left side of the tool.


#### Load project bundle JAR

The project must be available as JAR with the correct bundle headers (at least _Bundle-SymbolicName_) set in the MANIFEST.MF. This JAR can be loaded simply by drag and drop on the right side of the tool.


## License
The OSGi Package Tool is released under version 3.0 of the [GNU General Public License](https://www.gnu.org/licenses/gpl-3.0.de.html).
