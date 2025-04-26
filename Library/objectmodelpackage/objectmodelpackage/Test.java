
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Test {
    private static final int perDayFine = 10;
    private static ArrayList<Student> students;
    private static Library comsatsLibrary;
    private static ArrayList<Books> booksArrayList;
    private static boolean outOfStock = false;
    
    public static void main(String[] args) {
        // testIssueBook();
        testReturnBook();
    }

    // ✅ Library added -> ✅ Student added → ✅ Library card issued → ✅ Book added -> ✅ Book issued → ✅ Book returned
    public static void testReturnBook() {
        students = new ArrayList<>();
        comsatsLibrary = new Library("COMSATS");
        Books books = new Books();
        comsatsLibrary.addBookToLibrary(books);
        booksArrayList = comsatsLibrary.getBooksArrayList();
        Book book = new Book("The Great Gatsby", "F. Scott Fitzgerald");
        Student student = new Student("QWER", "TYU", "UG");

        addBookFunction(comsatsLibrary, book);
        addStudents(student);
        issueLibraryCard(comsatsLibrary, "TYU");
        issueBook(comsatsLibrary, "The Great Gatsby", "TYU");
        returnBook(comsatsLibrary, "TYU", "The Great Gatsby");
    }

    // ✅ Library added -> ✅ Student added → ✅ Library card issued → ✅ Book added -> ❌ Book issued → ❌ Book returned
    public static void testReturnBook_Fail_NotIssued() {
        students = new ArrayList<>();
        comsatsLibrary = new Library("COMSATS");
        Books books = new Books();
        books.setBooks(new Book().displayBooks());
        comsatsLibrary.addBookToLibrary(books);
        booksArrayList = comsatsLibrary.getBooksArrayList();
    
        Book book = new Book("The Great Gatsby", "F. Scott Fitzgerald");
        Student student = new Student("QWER", "TYU", "UG");
    
        //@ assume comsatsLibrary != null && book != null;
        addBookFunction(comsatsLibrary, book);
        //@ assert book.isPendingReturn() == false : "Book should not be available for borrowing";
        //@ assume checkBookAvailable(comsatsLibrary, book.getBookName()) != null;
        //@ assert checkBookAvailable(comsatsLibrary, book.getBookName()) == book : "Book is not found in library after addition";
        //@ assume comsatsLibrary.findBooksCollection(book.getBookName()) != null;
        //@ assert comsatsLibrary.findBooksCollection(book.getBookName()).getNumOfCopies() > 0 : "Book copies should increase after addition";
       
        //@ assume student != null;  
        addStudents(student);
        //@ assert students.contains(student) : "Student was not added successfully";
        
        //@ assume comsatsLibrary != null ;
        issueLibraryCard(comsatsLibrary, "TYU");
        //@ assume findStudent("TYU") != null;
        //@ assert findStudent("TYU").hasLibraryAccess("COMSATS") : "Library card was not issued";
        
        //@ assume checkBookAvailable(comsatsLibrary, "The Great Gatsby") != null && findStudent("TYU").hasLibraryAccess("COMSATS");
        issueBook(comsatsLibrary, "The Great Gatsby", "TYU");    
        //@ assert !findStudent("TYU").getIssuedBooks().contains(book) : "Book should not be issued";
    
        System.out.println("Attempting to return book that was never issued...");
        //@ assume !findStudent("TYU").getIssuedBooks().contains(book);
        returnBook(comsatsLibrary, "TYU", "The Great Gatsby"); // Should fail
        //@ assert book.isPendingReturn() == false : "Book return should fail since it was never issued";
    }

    // ✅ Library added -> ✅ Student added → ❌ Library card issued → ✅ Book added -> ❌ Book issued → ❌ Book returned
    /**
     * @requires comsatsLibrary != null && students != null && booksArrayList != null;
     * @ensures (
     *      \exists Student s; students.contains(s); s.getStudentId().equals("TYU") &&
     *      \exists Book b; booksArrayList.contains(b); b.getBookName().equals("The Great Gatsby") &&
     *      \forall Student s; students.contains(s); !s.hasLibraryAccess("COMSATS") && // No library card issued
     *      \forall Book b; booksArrayList.contains(b); b.isPendingReturn() // Book was never issued, return should fail
     * )
     * @modifies students, comsatsLibrary, booksArrayList
     * @pure
     */
    public static void testReturnBook_Fail_NoLibraryCard() {
        students = new ArrayList<>();
        comsatsLibrary = new Library("COMSATS");
        Books books = new Books();
        books.setBooks(new Book().displayBooks());
        comsatsLibrary.addBookToLibrary(books);
        booksArrayList = comsatsLibrary.getBooksArrayList();
    
        Book book = new Book("The Great Gatsby", "F. Scott Fitzgerald");
        Student student = new Student("QWER", "TYU", "UG");
    
        addBookFunction(comsatsLibrary, book);
        addStudents(student);
        
        System.out.println("Attempting to issue book without a library card...");
        issueBook(comsatsLibrary, "The Great Gatsby", "TYU"); // ❌ Should fail
    
        System.out.println("Attempting to return book...");
        returnBook(comsatsLibrary, "TYU", "The Great Gatsby"); // ❌ Should fail
    }

    // ✅ Library added -> ✅ Student added → ✅ Library card issued → ✅ Book added -> ✅ Book issued → ❌ Book returned

    /**
     * @requires comsatsLibrary != null && students != null && booksArrayList != null;
     * @ensures (
     *      \exists Student s; students.contains(s); s.getStudentId().equals("TYU") &&
     *      \exists Book b1; booksArrayList.contains(b1); b1.getBookName().equals("The Great Gatsby") &&
     *      \exists Book b2; booksArrayList.contains(b2); b2.getBookName().equals("1984") &&
     *      \forall Student s; students.contains(s); s.hasLibraryAccess("COMSATS") &&
     *      \forall Book b; booksArrayList.contains(b); b.isPendingReturn() // Wrong book returned, should fail
     * )
     * @modifies students, comsatsLibrary, booksArrayList
     * @pure
     */
    public static void testReturnBook_Fail_WrongBook() {
        students = new ArrayList<>();
        comsatsLibrary = new Library("COMSATS");
        Books books = new Books();
        books.setBooks(new Book().displayBooks());
        comsatsLibrary.addBookToLibrary(books);
        booksArrayList = comsatsLibrary.getBooksArrayList();
    
        Book book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald");
        Book book2 = new Book("1984", "George Orwell");
        Student student = new Student("QWER", "TYU", "UG");
    
        addBookFunction(comsatsLibrary, book1);
        addBookFunction(comsatsLibrary, book2);
        addStudents(student);
        issueLibraryCard(comsatsLibrary, "TYU");
    
        issueBook(comsatsLibrary, "1984", "TYU"); // ✅ "1984" is issued
    
        System.out.println("Attempting to return 'The Great Gatsby' (which was never issued)...");
        returnBook(comsatsLibrary, "TYU", "The Great Gatsby"); // ❌ Should fail
    }

    // ✅ Library added -> ❌ Student added → ✅ Library card issued → ✅ Book added -> ❌ Book issued → ❌ Book returned
    /**
     * @requires comsatsLibrary != null && students != null && booksArrayList != null;
     * @ensures (
     *      \forall Student s; students.contains(s); !s.getStudentId().equals("XYZ") && // Student does not exist
     *      \exists Book b; booksArrayList.contains(b); b.getBookName().equals("The Great Gatsby") &&
     *      \forall Book b; booksArrayList.contains(b); b.isPendingReturn() // Book was never issued, return should fail
     * )
     * @modifies students, comsatsLibrary, booksArrayList
     * @pure
     */
    public static void testReturnBook_Fail_NoStudent() {
        students = new ArrayList<>();
        comsatsLibrary = new Library("COMSATS");
        Books books = new Books();
        books.setBooks(new Book().displayBooks());
        comsatsLibrary.addBookToLibrary(books);
        booksArrayList = comsatsLibrary.getBooksArrayList();
    
        Book book = new Book("The Great Gatsby", "F. Scott Fitzgerald");
    
        addBookFunction(comsatsLibrary, book);
    
        System.out.println("Attempting to return book with a non-existent student...");
        returnBook(comsatsLibrary, "XYZ", "The Great Gatsby"); // ❌ Should fail
    }

    public static void addBookFunction(Library comsatsLibrary, Book book) {
        book.addBook();
        book.setPendingReturn(false);
        Book foundBook = checkBookAvailable(comsatsLibrary, book.getBookName());
        if (foundBook != null) {
            Books books = comsatsLibrary.findBooksCollection(book.getBookName());
            if (books != null) {
                Book newBook = new Book(foundBook);
                books.addBookToList(newBook);
                books.setNumOfCopies(books.getNumOfCopies() + 1);
            } else {
                System.out.println("Sorry but there's a bug");
            }
        } else {
            Books books = new Books();
            books.setNumOfCopies(1);
            books.addBookToList(book);
            comsatsLibrary.addBookToLibrary(books);
        }
    }

    private static void returnBook(Library library, String studentId, String bookName) {
        Student student = findStudent(studentId);
        if (student != null) {
            ArrayList<Book> bookList = student.getIssuedBooks();
            System.out.println("Welcome " + student.getStudentName());
            if (!bookList.isEmpty()) {
                for (Book book : bookList) {
                    calculateFine(student, book.getBorrowDate(), book.getReturnDate());
                }
                Optional<Book> bookToReturn = bookList.stream().filter(b -> b.getBookName().equalsIgnoreCase(bookName)).findFirst();
                if (bookToReturn.isPresent()) {
                    bookList.remove(bookToReturn.get());
                    bookToReturn.get().setPendingReturn(false);
                    System.out.println("Book returned successfully");
                } else {
                    System.out.println("Book not found");
                }
            } else {
                System.out.println("You haven't issued any books");
            }
        } else {
            System.out.println("Student not found");
        }
    }

    private static void issueBook(Library comsatsLibrary, String bookName, String studentId) {
        Book bookFound = checkBookAvailable(comsatsLibrary, bookName);
        if (bookFound == null) {
            System.out.println(outOfStock ? "Book out of stock" : "Book not available in the library");
            return;
        }

        System.out.println("Book Found in COMSATS library");
        Student student = findStudent(studentId);
        if (student != null) {
            if (student.hasLibraryAccess("COMSATS")) {
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
                    System.out.println("Book Issued");
                } else {
                    System.out.println("Max number of books issued");
                }
            } else {
                System.out.println("You do not have access to the library. Issue a Library card first.");
            }
        } else {
            System.out.println("Student not found");
        }
    }

    private static RulesResultSet maxBookStudentCanIssue(String bookLocation, String instituteName, String programEnrolledIn) {
        if (bookLocation.equals("COMSATS")) {
            return comsatsLibrary.comsatsRules(instituteName, programEnrolledIn);
        }
        return null;
    }

    private static Student findStudent(String id) {
        return students.stream().filter(student -> student.getStudentId().equals(id)).findFirst().orElse(null);
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
                System.out.println("numOfDays: "+numOfDays);
                int totalFine = student.getFine() + numOfDays*perDayFine;
                student.setFine(totalFine);
            }
        } catch (Exception e) {
            System.out.println("Error parsing dates"+e.getMessage()+"\n"+e);
        }
    }

    private static void issueLibraryCard(Library library, String studentId )
    {
        Student student = findStudent(studentId);
        if(student != null) {
            int status = student.issueCard(library.getInstituteName());
            switch (status){
                case 0:
                    System.out.println("Error issuing library card. Sorry for inconvenience. Try again later");
                    break;
                case 1:
                    System.out.println("Library Card Issued");
                    break;
                case 2:
                    System.out.println("Library card already issued earlier");
                    break;
            }
        }
        else
            System.out.println("Student not found");
    }

}
