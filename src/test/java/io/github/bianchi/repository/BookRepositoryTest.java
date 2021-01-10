package io.github.bianchi.repository;

import io.github.bianchi.model.entity.Book;
import io.github.bianchi.model.repository.BookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.*;

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

    private Book createNewBook() {
        return Book.builder().title("Livro 1").author("Bianchi").isbn("123").build();
    }

}
