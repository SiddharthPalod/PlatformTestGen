package sidlibrary.objectmodelpackage;

import java.util.ArrayList;

public class Library extends Institute {
    private ArrayList<Books> booksArrayList = new ArrayList<>();
    private int forUG, forPG, forPHD, maxDays;

    public Library(String instituteName) {
        super(instituteName);
        if (instituteName.equals("COMSATS")) {
            forUG = 5;
            forPG = 3;
            forPHD = 2;
            maxDays = 12;
        }
    }

    public ArrayList<Books> getBooksArrayList() {
        return booksArrayList;
    }

    public void addBookToLibrary(Books books) {
        booksArrayList.add(books);
    }

    public Books findBooksCollection(String findThisBook) {
        for (Books books : booksArrayList) {
            for (Book book : books.getBooks()) {
                if (book.getBookName().equals(findThisBook)) {
                    return books;
                }
            }
        }
        return null;
    }

    public RulesResultSet comsatsRules(String instituteName, String programEnrolledIn) {
        if (instituteName.equals("COMSATS")) {
            switch (programEnrolledIn) {
                case "UG": return new RulesResultSet(forUG, maxDays);
                case "PG": return new RulesResultSet(forPG, maxDays);
                case "PHD": return new RulesResultSet(forPHD, maxDays);
            }
        }
        return null;
    }
}
