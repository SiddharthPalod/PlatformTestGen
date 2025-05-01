
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Test2 {
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
        String libraryName = "COMSATS";
        comsatsLibrary = new Library(libraryName);
        Books books = new Books();
        books.setBooks(new Book().displayBooks());
        comsatsLibrary.addBookToLibrary(books);
        booksArrayList = comsatsLibrary.getBooksArrayList();
        String bookName = "The Great Gatsby";
        String bookAuthor = "F. Scott Fitzgerald";
        Book book = new Book(bookName, bookAuthor);
        //@ assume comsatsLibrary != null && book != null;
        addBookFunction(comsatsLibrary, book);
        //@ assert book.isPendingReturn() == false : "Book should not be available for borrowing";        
        //@ assume checkBookAvailable(comsatsLibrary, book.getBookName()) != null;
        //@ assert checkBookAvailable(comsatsLibrary, book.getBookName()) == book : "Book is not found in library after addition";
        //@ assume comsatsLibrary.findBooksCollection(book.getBookName()) != null;
        //@ assert comsatsLibrary.findBooksCollection(book.getBookName()).getNumOfCopies() > 0 : "Book copies should increase after addition";
        
        students = new ArrayList<>();
        String studentName = "QWER";
        String studentId = "TYU";
        String studentProgram = "UG";
        Student student = new Student(studentName, studentId, studentProgram);
        //@ assume student != null;
        addStudents(student);
        //@ assert students.contains(student) : "Student was not added successfully";
        
        String libraryName2 = "COMSATS";
        Library comsatsLibrary2 = new Library(libraryName2);
        comsatsLibrary2 = comsatsLibrary; //L instead of referential and in assert checking this we will create a clone to do so TestAPI
        // books , studends
        String studentId2 = "TYU";
        //@ assume findStudent(studentId2) != null;
        issueLibraryCard(comsatsLibrary2, studentId2);
        //@ assert findStudent(studentId2).hasLibraryAccess(libraryName2) : "Library card was not issued";
        comsatsLibrary = comsatsLibrary2;
        
        String libraryName3 = "COMSATS";
        Library comsatsLibrary3 = new Library(libraryName3);
        comsatsLibrary3 = comsatsLibrary;
        String bookName2 = "The Great Gatsby";
        String studentId3 = "TYU";
        //@ assume checkBookAvailable(comsatsLibrary3, bookName2) != null && findStudent(studentId3).hasLibraryAccess(libraryName3);
        comsatsLibrary = comsatsLibrary3;
        issueBook(comsatsLibrary3, bookName2, studentId3);    
        //@ assert findStudent(studentId3).getIssuedBooks().contains(book) && book.isPendingReturn() : "Book was not issued to the student or is not marked as pending return";

        String libraryName4 = "COMSATS";
        Library comsatsLibrary4 = new Library(libraryName4);
        comsatsLibrary4 = comsatsLibrary;
        String bookName3 = "The Great Gatsby";
        String studentId4 = "TYU";
        //@ assume checkBookAvailable(comsatsLibrary3, bookName2) != null && findStudent(studentId4).getIssuedBooks().contains(book);
        returnBook(comsatsLibrary4, studentId4, bookName3);
        comsatsLibrary = comsatsLibrary4;
       //@ assert !book.isPendingReturn() && !findStudent(studentId).getIssuedBooks().contains(book) : "Book was not returned successfully or still marked as issued";
    }

   /**
     * @requires comsatsLibrary != null && book != null;
     * @ensures (
     *      \exists Books books; comsatsLibrary.getBooksArrayList().contains(books);
     *      books.getBooks().contains(book) &&
     *      book.isPendingReturn() == false // Book is available for borrowing
     * )
     * @modifies comsatsLibrary
     */
    public static void addBookFunction(Library comsatsLibrary, Book book) {
        //@ assume book != null;
        book.addBook();
        
        book.setPendingReturn(false);
        //@ assert book.isPendingReturn() == false : "Book should be available for borrowing";

        
        //@ assume checkBookAvailable(comsatsLibrary, book.getBookName()) != null;
        Book foundBook = checkBookAvailable(comsatsLibrary, book.getBookName());
        //@ assert foundBook == null || foundBook.getBookName().equals(book.getBookName()) : "Book availability check failed";
        
        if (foundBook != null) {
            //@ assume comsatsLibrary.findBooksCollection(book.getBookName()) != null;
            Books books = comsatsLibrary.findBooksCollection(book.getBookName());
            if (books != null) {
                Book newBook = new Book(foundBook);
                books.addBookToList(newBook);
                books.setNumOfCopies(books.getNumOfCopies() + 1);
                //@ assert books.getNumOfCopies() > 0 : "Book copies should increase after addition";
            } else {
                System.out.println("Sorry but there's a bug");
            }
        } else {
            Books books = new Books();
            books.setNumOfCopies(1);
            books.addBookToList(book);
            comsatsLibrary.addBookToLibrary(books);
            //@ assert books.getNumOfCopies() == 1 : "New book should have exactly one copy";
        }
    }

        /**
     * @requires library != null && studentId != null && bookName != null;
     * @ensures (
     *      \exists Student s; students.contains(s) ==> s.getStudentId().equals(studentId) &&
     *      \exists Book b; s.getIssuedBooks().contains(b) ==> b.getBookName().equals(bookName) ==> !b.isPendingReturn()
     * )
     * @modifies students, library
     */
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


    /**
     * @requires comsatsLibrary != null && bookName != null && studentId != null;
     * @ensures (
     *      \exists Student s; students.contains(s) ==> s.getStudentId().equals(studentId) && s.hasLibraryAccess("COMSATS") &&
     *      \exists Book b; comsatsLibrary.getBooksArrayList().contains(b) ==> b.getBookName().equals(bookName) ==> b.isPendingReturn()
     * )
     * @modifies students, comsatsLibrary
     */

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

    /**
     * @requires bookLocation != null && instituteName != null && programEnrolledIn != null;
     * @ensures (
     *      (bookLocation.equals("COMSATS")) ==> (
     *          \result == comsatsLibrary.comsatsRules(instituteName, programEnrolledIn)
     *      )
     * )
     * @pure
     */
    
    private static RulesResultSet maxBookStudentCanIssue(String bookLocation, String instituteName, String programEnrolledIn) {
        if (bookLocation.equals("COMSATS")) {
            return comsatsLibrary.comsatsRules(instituteName, programEnrolledIn);
        }
        return null;
    }

     /**
     * @requires id != null;
     * @ensures (
     *      \result == (\exists Student s; students.contains(s); s.getStudentId().equals(id))
     * )
     * @pure
     */
    private static Student findStudent(String id) {
        return students.stream().filter(student -> student.getStudentId().equals(id)).findFirst().orElse(null);
    }

    /**
     * @requires library != null && getBookName != null;
     * @ensures (
     *      (\exists Book b; booksArrayList.contains(b); b.getBookName().equalsIgnoreCase(getBookName) && !b.isPendingReturn())
     *          ==> \result == b
     * )
     * @modifies outOfStock
     */
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

     /**
     * @requires student != null;
     * @ensures students.contains(student);
     * @modifies students
     */
    private static void addStudents(Student student) {
        student.addStudent();
        students.add(student);
    }

    /**
     * @requires student != null && borrowDate != null && returnDate != null;
     * @ensures (
     *      (ChronoUnit.DAYS.between(LocalDate.parse("26/06/2021", DateTimeFormatter.ofPattern("dd/MM/yyyy")), LocalDate.parse(returnDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))) < 0)
     *          ==> (student.getFine() > \old(student.getFine()))
     * )
     * @modifies student
     */
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

        /**
     * @requires library != null && studentId != null;
     * @ensures (
     *      \exists Student s; students.contains(s) ==> s.getStudentId().equals(studentId) ==> (
     *          \result == 0 || \result == 1 || \result == 2
     *      )
     * )
     * @modifies students
     */
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
