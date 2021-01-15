package io.github.bianchi.repository;

import io.github.bianchi.model.entity.Book;
import io.github.bianchi.model.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Return True When Isbn Exists")
    public void returnTrueWhenIsbnExists() {
        String isbn = "123";
        Book book = createNewBook();
        entityManager.persist(book);

        boolean exists = repository.existsByIsbn(isbn);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Return True When Isbn Doesnt Exists")
    public void returnTrueWhenIsbnDoesntExists() {
        String isbn = "456";
        Book book = createNewBook();
        entityManager.persist(book);

        boolean exists = repository.existsByIsbn(isbn);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Find Book By ID")
    public void findByIdTest() {
        Book newBook = createNewBook();
        entityManager.persist(newBook);

        Optional<Book> book = repository.findById(newBook.getId());

        assertThat(book.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Save Book Success")
    public void saveBookTest() {
        Book book = createNewBook();

        Book savedBook = repository.save(book);

        assertThat(savedBook.getId()).isNotNull();
    }

    @Test
    @DisplayName("Delete Book Success")
    public void deleteBookTest() {
        Book newBook = createNewBook();
        entityManager.persist(newBook);

        Book book = entityManager.find(Book.class, newBook.getId());

        repository.delete(book);

        Book deletedBook = entityManager.find(Book.class, newBook.getId());

        assertThat(deletedBook).isNull();
    }

    private Book createNewBook() {
        return Book.builder().title("Livro 1").author("Bianchi").isbn("123").build();
    }

}
