package tesapp;

import javafx.scene.control.ListCell;


/**
 * Kelas BookListCell adalah implementasi kustom untuk komponen ListView dalam JavaFX.
 * Kelas ini meng-extends kelas ListCell dan meng-override metode updateItem untuk menyesuaikan tampilan sel-sel dalam ListView.
 * Metode updateItem mengatur teks sel-sel agar menampilkan judul, penulis, dan kategori buku.
 */
public class BookListCell extends ListCell<Book> {
    @Override
    protected void updateItem(Book book, boolean empty) {
        super.updateItem(book, empty);
        if (empty || book == null) {
            setText(null);
        } else {
            setText(book.getTitle() + " - " + book.getAuthor() + " - " + book.getCategory());
        }
    }
}
