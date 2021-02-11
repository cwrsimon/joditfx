# joditfx


![Screenshot](https://raw.github.com/cwrsimon/joditfx/master/src/main/screenshots/screenshot1.png)

# Getting started

0. Make sure you have a working JDK 11 environment on your machine.
1. Clone the project.
2. Build the library and install it to your local Maven repository ($HOME/.m2):
```
./mvnw clean install
```
3. Run the demo app from the commmand-line:
```
./mvnw exec:java -Dexec.mainClass="de.wesim.joditfx.DemoApp"
```
4. Or, run the demo app from inside your IDE:
Locate *src/test/java/de/wesim/joditfx/* and run *RunDemoAppInIDE.java*.
5. Integrate *joditfx* in your own Javafx project with this XML snippet:
```
<repositories>
        <repository>
            <id>mvn-repo</id>
            <url>https://github.com/cwrsimon/artifacts/raw/master</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
</repositories>
<dependency>
	<groupId>de.wesim</groupId>
	<artifactId>joditfx</artifactId>
	<version>0.2.1</version>
</dependency>
```
# Planned features
- deployment to Maven Central Repository

