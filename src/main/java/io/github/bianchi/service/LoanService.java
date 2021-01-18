package io.github.bianchi.service;

import io.github.bianchi.api.dto.LoanFilterDTO;
import io.github.bianchi.api.resource.BookController;
import io.github.bianchi.model.entity.Book;
import io.github.bianchi.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LoanService {

    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO filter, Pageable pageable);

    Page<Loan> getLoansByBook(Book book, Pageable pageable);

    List<Loan> getAllLateLoans();
}
