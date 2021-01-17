package io.github.bianchi.service;

import io.github.bianchi.api.dto.LoanFilterDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

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

    @Test
    @DisplayName("Get Loan By Id")
    public void getLoanById() {
        Long id = 1L;
        Loan mockLoan = createNewLoan();
        mockLoan.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(mockLoan));

        Optional<Loan> loan = service.getById(id);

        assertThat(loan.isPresent()).isTrue();
        assertThat(loan.get().getId()).isEqualTo(id);
        assertThat(loan.get().getCustomer()).isEqualTo(mockLoan.getCustomer());
        assertThat(loan.get().getBook()).isEqualTo(mockLoan.getBook());
        assertThat(loan.get().getLoanDate()).isEqualTo(mockLoan.getLoanDate());
    }

    @Test
    @DisplayName("Update Loan Success")
    public void updateLoanTest() {
        Loan updatingLoan = Loan.builder().id(1L).build();

        Loan mockUpdatedLoan = createNewLoan();
        mockUpdatedLoan.setId(1L);

        Mockito.when(repository.save(updatingLoan)).thenReturn(mockUpdatedLoan);

        Loan updatedLoan = service.update(updatingLoan);

        assertThat(updatedLoan.getId()).isEqualTo(mockUpdatedLoan.getId());
        assertThat(updatedLoan.getCustomer()).isEqualTo(mockUpdatedLoan.getCustomer());
        assertThat(updatedLoan.getBook()).isEqualTo(mockUpdatedLoan.getBook());
        assertThat(updatedLoan.getLoanDate()).isEqualTo(mockUpdatedLoan.getLoanDate());
    }

    @Test
    @DisplayName("Find loans")
    public void findLoanTest() {
        LoanFilterDTO filter = LoanFilterDTO.builder()
                .customer("Bianchi").isbn("123").build();

        Loan loan = createNewLoan();
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<Loan> page = new PageImpl<>(Arrays.asList(loan), pageRequest, 1);

        Mockito.when(repository.findByIsbnOrCustomer(Mockito.anyString(), Mockito.anyString(),
                Mockito.any(PageRequest.class))).thenReturn(page);

        Page<Loan> result = service.find(filter, pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(Arrays.asList(loan));
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    private Loan createNewLoan() {
        return Loan.builder().id(1L).customer("Bianchi").book(createNewBook()).loanDate(LocalDate.now()).build();
    }

    private Book createNewBook() {
        return Book.builder().id(1L).title("Livro 1").author("Gonzalez").isbn("123").build();
    }

}
