package io.github.bianchi.service;

import io.github.bianchi.exception.BusinessException;
import io.github.bianchi.model.entity.Book;
import io.github.bianchi.model.entity.Loan;
import io.github.bianchi.model.repository.LoanRepository;
import io.github.bianchi.service.impl.LoanServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    LoanService service;

    @MockBean
    LoanRepository repository;

    @BeforeEach
    private void setUp() {
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Create Loan Success")
    public void saveLoanTest() {
        Loan loan = createNewLoan();

        Mockito.when(repository.existsByBookNotReturned(loan.getBook())).thenReturn(false);

        Mockito.when(repository.save(loan)).thenReturn(Loan.builder()
                .id(1L)
                .book(createNewBook())
                .customer("Bianchi")
                .loanDate(LocalDate.now()).build());

        Loan savedloan = service.save(loan);

        assertThat(savedloan.getId()).isNotNull();
        assertThat(savedloan.getBook()).isEqualTo(loan.getBook());
        assertThat(savedloan.getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(savedloan.getLoanDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Must Not Save Loan With Borrowed Book")
    public void mustNotSaveLoanWithBorrowedBook() {
        Loan loan = createNewLoan();

        Mockito.when(repository.existsByBookNotReturned(loan.getBook())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable(() -> service.save(loan));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Livro já emprestado");

        Mockito.verify(repository, Mockito.never()).save(loan);
    }

    private Loan createNewLoan() {
        return Loan.builder().id(1L).customer("Bianchi").book(createNewBook()).loanDate(LocalDate.now()).build();
    }

    private Book createNewBook() {
        return Book.builder().id(1L).title("Livro 1").author("Gonzalez").isbn("123").build();
    }

}
