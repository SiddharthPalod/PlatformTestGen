package sidlibrary.objectmodelpackage;

import java.util.ArrayList;

public class Student extends Institute {

    private String studentName;
    private String studentId;
    private String programEnrolledIn;
    private int fine;
    private ArrayList<Book> issuedBooks;
    private boolean cmsatsLibraryCard;

    public Student() {}

    public Student(String studentName, String studentId, String programEnrolledIn) {
        super("COMSATS");
        this.studentName = studentName;
        this.studentId = studentId;
        this.programEnrolledIn = programEnrolledIn;
        issuedBooks = new ArrayList<>();
    }

    public Student(String instituteName, String studentName, String studentId, String programEnrolledIn) {
        super("COMSATS");
        this.studentName = studentName;
        this.studentId = studentId;
        this.programEnrolledIn = programEnrolledIn;
        this.cmsatsLibraryCard = false;
        issuedBooks = new ArrayList<>();
    }

    public int issueCard(String instituteName) {
        if (instituteName.equals("COMSATS")) {
            if (cmsatsLibraryCard) {
                return 2;
            }
            cmsatsLibraryCard = true;
            return 1;
        }
        return 0;
    }

    public boolean hasLibraryAccess(String instituteName) {
        return "COMSATS".equals(instituteName) && cmsatsLibraryCard;
    }

    public void addBookToIssueList(Book book) {
        this.issuedBooks.add(book);
    }

    public String getStudentName() { return studentName; }

    public String getStudentId() { return studentId; }

    public String getProgramEnrolledIn() { return programEnrolledIn; }

    public ArrayList<Book> getIssuedBooks() { return issuedBooks; }

    public int getFine() { return fine; }

    public void setFine(int fine) { this.fine = fine; }

    // File handling removed for SPF compatibility
    public void addStudent() {
        // Simulated logic for adding student (no actual file I/O)
        System.out.println("Student added: " + studentName + ", " + studentId);
    }

    // Return a mock list of students for symbolic testing
    public ArrayList<Student> displayStudents() {
        ArrayList<Student> students = new ArrayList<>();
        students.add(new Student("Student1", "S001", "UG"));
        students.add(new Student("Student2", "S002", "PG"));
        return students;
    }
}
