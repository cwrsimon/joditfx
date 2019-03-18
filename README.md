# summernotefx
SummernoteFX is a simple wrapper that allows using the summernote HTML 
editor from [summernote.org](https://www.summernote.org) as a JavaFX component. It is implemented in Java 11 and uses OpenJFX 11.0.2.

![Screenshot](https://raw.github.com/cwrsimon/summernotefx/master/src/main/screenshots/screenshot1.png)

# Getting started

0. Make sure you have a working JDK 11 environment on your machine.
1. Clone the project.
2. Build the library and install it to your local Maven repository ($HOME/.m2):
```
./mvnw clean install
```
3. Run the demo app from the commmand-line:
```
./mvnw exec:java -Dexec.mainClass="de.wesim.summernotefx.DemoApp"
```
4. Or, run the demo app from inside your IDE:
Locate *src/test/java/de/wesim/summernotefx/* and run *RunDemoAppInIDE.java*.
5. Integrate *summernotefx* in your own Javafx project with this XML snippet:
```
<dependency>
	<groupId>de.wesim</groupId>
	<artifactId>summernotefx</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```
# Planned features
- deployment to Maven Central Repository
- more configuration options that cover more of the many options offered by *summernote*
- support for inserting images (probably comes with OpenJFX 12)

