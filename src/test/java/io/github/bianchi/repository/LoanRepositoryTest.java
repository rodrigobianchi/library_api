package io.github.bianchi.repository;

import io.github.bianchi.model.entity.Book;
import io.github.bianchi.model.entity.Loan;
import io.github.bianchi.model.repository.LoanRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    LoanRepository repository;

    @Test
    @DisplayName("Verify If Book Not Returned")
    public void existsByBookNotReturnedTest() {
        Book book = createNewBook();
        Book savedBook = entityManager.persist(book);

        Loan loan = Loan.builder().customer("Bianchi").book(savedBook).loanDate(LocalDate.now()).build();
        entityManager.persist(loan);

        boolean exists = repository.existsByBookNotReturned(loan.getBook());

        Assertions.assertThat(exists).isTrue();
    }


    private Book createNewBook() {
        return Book.builder().title("Livro 1").author("Bianchi").isbn("123").build();
    }
}
