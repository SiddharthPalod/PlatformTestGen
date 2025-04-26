# Platform Test generation 

## Pre-requistes and Setup
 1) clone SPF repository from https://github.com/SymbolicPathFinder/jpf-symbc/tree/gradle-build
 2) download Java-8 and set java and javac version to java 8: https://www.oracle.com/in/java/technologies/javase/javase8-archive-downloads.html
 3) Download maven and setup SPF according the the github link in (1)
 4) In jpf-symbc/src/examples/ directory add the folders present in Testing

## Running code
To compile and create classes
gradle :jpf-symbc:buildJars

Cd SPF/jpf-symbc

java -Xmx1024m -ea -jar ../jpf-core/build/RunJPF.jar ./src/examples/sidlibrary/objectmodelpackage/LibReturnBook.jpf

Resultant output indicates our manual script is working perfectly using SPF to generate symbolic output ![image](https://github.com/user-attachments/assets/347ccb33-196a-4e56-8430-052e2a24c0c9)

## Manual test script 
Library/objectmodelpackage/LibReturnBook.java

