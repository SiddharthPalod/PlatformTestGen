# Platform Test Generation 


## Codebase Deliverables
Manual test scripts for a Java platform application, integrated with symbolic execution via SPF.

---

## ⚙️ Prerequisites and Setup

1. **Clone the SPF Repository**  
    Clone the SPF repository from [SPF GitHub Repository](https://github.com/SymbolicPathFinder/jpf-symbc/tree/gradle-build).

2. **Install Java 8**  
    Download and install [Java 8](https://www.oracle.com/in/java/technologies/javase/javase8-archive-downloads.html).  
    Ensure that both `java -version` and `javac -version` point to Java 8.

3. **Download and Install Maven**  
    Follow the Maven installation instructions provided in the SPF GitHub repository linked in step 1.

4. **Add Testing Folders**  
    Copy the folders from the `Testing` directory of this repository into:  
    ```
    jpf-symbc/src/examples/
    ```

---

## 🚀 Running the Code

1. **Compile the Codebase**  
    Run the following command to compile and create the required classes:  
    ```bash
    gradle :jpf-symbc:buildJars
    ```

2. **Navigate into SPF/jpf-symbc**  
    ```bash
    cd SPF/jpf-symbc
    ```

3. **Run the Symbolic Execution**  
    Execute the test using the following command:  
    ```bash
    java -Xmx1024m -ea -jar ../jpf-core/build/RunJPF.jar ./src/examples/sidlibrary/objectmodelpackage/LibReturnBook.jpf
    ```

4. **Check Output**  
    The resultant symbolic execution output confirms that the manual test script is functioning correctly and generating symbolic paths as intended.

    Example Output:  
    ![Output Example](https://github.com/user-attachments/assets/347ccb33-196a-4e56-8430-052e2a24c0c9)

---

## 📝 Manual Test Script Details

The main manual test script is located at:  
```
Library/objectmodelpackage/LibReturnBook.java
```

### Key Features:
- Includes multiple assertions, branches, and symbolic inputs.
- Models real-world edge cases for a library book return system.
- Annotated and structured to maximize symbolic path exploration via SPF.
