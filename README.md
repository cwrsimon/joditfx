# summernotefx
SummernoteFX is a simple wrapper that allows using the summernote HTML 
editor from summernote.org as a JavaFX component.

# Getting started

1. Clone the project 

2. Build the library and install it to your local Maven repository ($HOME/.m2):
```
./mvnw clean install
```

3. Run the demo app from the commmand-line:
```
./mvnw package exec:java -Dexec.mainClass="de.wesim.summernotefx.DemoApp"
```

4. Or, run the demo app from inside your IDE:
Locate *src/test/java/de/wesim/summernotefx/* and run *RunDemoAppInIDE.java*.

5. Integrate in your favourite Javafx project with this XML snippet:
```
<dependency>
	<groupId>de.wesim</groupId>
	<artifactId>summernotefx</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```
