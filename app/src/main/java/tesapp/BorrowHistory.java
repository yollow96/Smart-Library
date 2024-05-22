package tesapp;

import java.time.LocalDate;

public class BorrowHistory {
    private String borrower;
    private String bookTitle;
    private LocalDate borrowDate;
    private LocalDate dueDate;

    public BorrowHistory(String borrower, String bookTitle, LocalDate borrowDate, LocalDate dueDate) {
        this.borrower = borrower;
        this.bookTitle = bookTitle;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    public String getBorrower() {
        return borrower;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
}