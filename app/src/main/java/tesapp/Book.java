package tesapp;

import java.time.LocalDate;

public class Book {
    private String title;
    private String author;
    private String category;
    private boolean borrowed;
    private LocalDate borrowDate;
    private String borrower;

    public Book(String title, String author, String category) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.borrowed = false;
        this.borrowDate = null;
        this.borrower = null;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getBorrower() {
        return borrower;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    @Override
    public String toString() {
        return title + " - " + author + "-" + "(" + category + ")";
    }
}
