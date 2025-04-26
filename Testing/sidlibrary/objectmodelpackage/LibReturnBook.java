// SPDX-License-Identifier: MIT
package sidlibrary.objectmodelpackage;

import gov.nasa.jpf.symbc.Debug;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class LibReturnBook {
    private static final int perDayFine = 10;
    private static ArrayList<Student> students;
    private static Library comsatsLibrary;
    private static ArrayList<Books> booksArrayList;
    private static boolean outOfStock = false;

    public static void main(String[] args) {
        testReturnBook();
    }
    // ✅ Library added -> ✅ Student added → ✅ Library card issued → ✅ Book added -> ✅ Book issued → ✅ Book returned
    public static void testReturnBook() {
        // Symbolic library name and initialization
        String libraryName1 = Debug.makeSymbolicString("libraryName1");
        comsatsLibrary = new Library(libraryName1);
    
        // Add books to the library
        Books books = new Books();
        books.setBooks(new Book().displayBooks());
        comsatsLibrary.addBookToLibrary(books);
        booksArrayList = comsatsLibrary.getBooksArrayList();
    
        // Symbolic book
        String bookName1 = Debug.makeSymbolicString("bookName1");
        String bookAuthor1 = Debug.makeSymbolicString("bookAuthor1");
        Book book = new Book(bookName1, bookAuthor1);
        addBookFunction(comsatsLibrary, book);
    
        students = new ArrayList<>();
    
        // Student 1 (base student)
        String studentName1 = Debug.makeSymbolicString("studentName1");
        String studentId1 = Debug.makeSymbolicString("studentId1");
        String studentProgram1 = Debug.makeSymbolicString("studentProgram1");
        Student student1 = new Student(studentName1, studentId1, studentProgram1);
        addStudents(student1);
    
        // --- Student 2 ---
        String studentId2 = Debug.makeSymbolicString("studentId2");
        Student student2 = new Student("Student2", studentId2, "CSE");
        addStudents(student2);
        issueLibraryCard(new Library(libraryName1), studentId2);
    
        // --- Student 3 ---
        String bookName2 = Debug.makeSymbolicString("bookName2");
        String studentId3 = Debug.makeSymbolicString("studentId3");
        Student student3 = new Student("Student3", studentId3, "CSE");
        addStudents(student3);
        issueLibraryCard(new Library(libraryName1), studentId3);
    
        // Add the book to library3 too
        Book book2 = new Book(bookName2, "Author2");
        addBookFunction(comsatsLibrary, book2); // reuse comsatsLibrary for consistency
    
        issueBook(comsatsLibrary, bookName2, studentId3);
    
        // --- Student 4 ---
        String studentId4 = Debug.makeSymbolicString("studentId4");
        Student student4 = new Student("Student4", studentId4, "CSE");
        addStudents(student4);
        issueLibraryCard(new Library(libraryName1), studentId4);
    
        // Student 4 manually issued a book
        Book borrowedBook = new Book(bookName2, "AuthorX");
        borrowedBook.setBorrowDate("01/06/2021");
        borrowedBook.setReturnDate("15/06/2021");
        borrowedBook.setPendingReturn(true);
        borrowedBook.setBorrowerId(studentId4);
        student4.addBookToIssueList(borrowedBook);
        addBookFunction(comsatsLibrary, borrowedBook); // make sure library has this book too
    
        returnBook(comsatsLibrary, studentId4, bookName2);
    }

    // ✅ Library added -> ✅ Student added → ✅ Library card issued → ✅ Book added -> ❌ Book issued → ❌ Book returned
    public static void testReturnBook_Fail_NotIssued() {
        String libraryName = Debug.makeSymbolicString("libraryName");
        comsatsLibrary = new Library(libraryName);
    
        Books books = new Books();
        books.setBooks(new Book().displayBooks());
        comsatsLibrary.addBookToLibrary(books);
        booksArrayList = comsatsLibrary.getBooksArrayList();
    
        // Add a book symbolically
        String bookName = Debug.makeSymbolicString("bookName");
        String bookAuthor = Debug.makeSymbolicString("bookAuthor");
        Book book = new Book(bookName, bookAuthor);
        addBookFunction(comsatsLibrary, book);
    
        students = new ArrayList<>();
    
        // Create student symbolically
        String studentName = Debug.makeSymbolicString("studentName");
        String studentId = Debug.makeSymbolicString("studentId");
        String studentProgram = Debug.makeSymbolicString("studentProgram");
        Student student = new Student(studentName, studentId, studentProgram);
        addStudents(student);
        issueLibraryCard(new Library(libraryName), studentId);
    
        // Return without issuing
        returnBook(comsatsLibrary, studentId, bookName); // Should fail or no-op
    }

    // ✅ Library added -> ✅ Student added → ❌ Library card issued → ✅ Book added -> ❌ Book issued → ❌ Book returned
    public static void testReturnBook_Fail_NoLibraryCard() {
        String libraryName = Debug.makeSymbolicString("libraryName");
        comsatsLibrary = new Library(libraryName);
    
        Books books = new Books();
        books.setBooks(new Book().displayBooks());
        comsatsLibrary.addBookToLibrary(books);
        booksArrayList = comsatsLibrary.getBooksArrayList();
    
        String bookName = Debug.makeSymbolicString("bookName");
        String bookAuthor = Debug.makeSymbolicString("bookAuthor");
        Book book = new Book(bookName, bookAuthor);
        addBookFunction(comsatsLibrary, book);
    
        students = new ArrayList<>();
    
        String studentName = Debug.makeSymbolicString("studentName");
        String studentId = Debug.makeSymbolicString("studentId");
        String studentProgram = Debug.makeSymbolicString("studentProgram");
        Student student = new Student(studentName, studentId, studentProgram);
        addStudents(student);
    
        // No library card issued
        returnBook(comsatsLibrary, studentId, bookName); // Should fail due to missing access
    }
    
    // ✅ Library added -> ✅ Student added → ✅ Library card issued → ✅ Book added -> ✅ Book issued → ❌ Book returned
    public static void testReturnBook_Fail_WrongBook() {
        String libraryName = Debug.makeSymbolicString("libraryName");
        comsatsLibrary = new Library(libraryName);
    
        Books books = new Books();
        books.setBooks(new Book().displayBooks());
        comsatsLibrary.addBookToLibrary(books);
        booksArrayList = comsatsLibrary.getBooksArrayList();
    
        // Book 1 and Book 2 (symbolic)
        String issuedBookName = Debug.makeSymbolicString("issuedBookName");
        String wrongBookName = Debug.makeSymbolicString("wrongBookName");
    
        Book book1 = new Book(issuedBookName, "Author1");
        Book book2 = new Book(wrongBookName, "Author2");
    
        addBookFunction(comsatsLibrary, book1);
        addBookFunction(comsatsLibrary, book2);
    
        students = new ArrayList<>();
    
        String studentId = Debug.makeSymbolicString("studentId");
        Student student = new Student("StudentX", studentId, "CSE");
        addStudents(student);
        issueLibraryCard(new Library(libraryName), studentId);
    
        // Manually issue book1 to student
        book1.setBorrowDate("01/06/2021");
        book1.setReturnDate("15/06/2021");
        book1.setPendingReturn(true);
        book1.setBorrowerId(studentId);
        student.addBookToIssueList(book1);
    
        // Try returning book2 (not issued)
        returnBook(comsatsLibrary, studentId, wrongBookName);
    }

    // ✅ Library added -> ❌ Student added → ✅ Library card issued → ✅ Book added -> ❌ Book issued → ❌ Book returned
    public static void testReturnBook_Fail_NoStudent() {
        String libraryName = Debug.makeSymbolicString("libraryName");
        comsatsLibrary = new Library(libraryName);
    
        Books books = new Books();
        books.setBooks(new Book().displayBooks());
        comsatsLibrary.addBookToLibrary(books);
        booksArrayList = comsatsLibrary.getBooksArrayList();
    
        String bookName = Debug.makeSymbolicString("bookName");
        Book book = new Book(bookName, "AuthorX");
        addBookFunction(comsatsLibrary, book);
    
        students = new ArrayList<>(); // Empty student list
    
        String unknownStudentId = Debug.makeSymbolicString("unknownStudentId");
        returnBook(comsatsLibrary, unknownStudentId, bookName); // Should fail: student not found
    }
    

    public static void addBookFunction(Library lib, Book book) {
        //@ assume book != null;
        book.addBook();
        book.setPendingReturn(false);

        //@ assert !book.isPendingReturn();

        Book foundBook = checkBookAvailable(lib, book.getBookName());
        //@ assert foundBook == null || foundBook.getBookName().equals(book.getBookName());

        if (foundBook != null) {
            //@ assume lib.findBooksCollection(book.getBookName()) != null;
            Books books = lib.findBooksCollection(book.getBookName());
            if (books != null) {
                Book newBook = new Book(foundBook);
                books.addBookToList(newBook);
                books.setNumOfCopies(books.getNumOfCopies() + 1);
                //@ assert books.getNumOfCopies() > 0;
            }
        } else {
            Books books = new Books();
            books.setNumOfCopies(1);
            books.addBookToList(book);
            lib.addBookToLibrary(books);
            //@ assert books.getNumOfCopies() == 1;
        }
    }

    private static void returnBook(Library library, String studentId, String bookName) {
        Student student = findStudent(studentId);
        if (student != null) {
            ArrayList<Book> bookList = student.getIssuedBooks();
            if (!bookList.isEmpty()) {
                for (Book book : bookList) {
                    calculateFine(student, book.getBorrowDate(), book.getReturnDate());
                }
                for (int i = 0; i < bookList.size(); i++) {
                    Book book = bookList.get(i);
                    if (book.getBookName().equalsIgnoreCase(bookName)) {
                        bookList.remove(i);
                        book.setPendingReturn(false);
                        break;
                    }
                }
            }
        }
    }

    private static void issueBook(Library lib, String bookName, String studentId) {
        Book bookFound = checkBookAvailable(lib, bookName);
        if (bookFound == null) return;

        Student student = findStudent(studentId);
        if (student != null && student.hasLibraryAccess("COMSATS")) {
            RulesResultSet resultSet = maxBookStudentCanIssue("COMSATS", student.getInstituteName(), student.getProgramEnrolledIn());
            if (resultSet != null && student.getIssuedBooks().size() < resultSet.getNumOfBooks()) {
                bookFound.setBorrowerId(student.getStudentId());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Calendar c = Calendar.getInstance();
                bookFound.setBorrowDate(sdf.format(c.getTime()));
                c.add(Calendar.DATE, resultSet.getNumOfDays());
                bookFound.setReturnDate(sdf.format(c.getTime()));
                bookFound.setPendingReturn(true);
                student.addBookToIssueList(bookFound);
            }
        }
    }

    private static RulesResultSet maxBookStudentCanIssue(String bookLocation, String instituteName, String programEnrolledIn) {
        if (bookLocation.equals("COMSATS")) {
            return comsatsLibrary.comsatsRules(instituteName, programEnrolledIn);
        }
        return null;
    }

    public static Student findStudent(String studentId) {
        for (Student student : students) {
            if (student.getStudentId().equals(studentId)) {
                return student;
            }
        }
        return null; // Return null if no student is found
    }

    private static Book checkBookAvailable(Library library, String getBookName) {
        outOfStock = false;
        for (Books books : booksArrayList) {
            for (Book book : books.getBooks()) {
                if (book.getBookName().equalsIgnoreCase(getBookName) && !book.isPendingReturn()) {
                    return book;
                }
            }
        }
        outOfStock = true;
        return null;
    }

    private static void addStudents(Student student) {
        student.addStudent();
        students.add(student);
    }

    private static void calculateFine(Student student, String borrowDate, String returnDate) {
        String todaysDate = "26/06/2021";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate borDate = LocalDate.parse(returnDate, formatter);
            LocalDate retDate = LocalDate.parse(todaysDate, formatter);
            long days = ChronoUnit.DAYS.between(retDate, borDate);

            if (days < 0) {
                int numOfDays = (int) Math.abs(days);
                int totalFine = student.getFine() + numOfDays * perDayFine;
                student.setFine(totalFine);
            }
        } catch (Exception e) {
            System.out.println("Date parsing failed: " + e.getMessage());
        }
    }

    private static void issueLibraryCard(Library library, String studentId) {
        Student student = findStudent(studentId);
        if (student != null) {
            student.issueCard(library.getInstituteName());
        }
    }
}
