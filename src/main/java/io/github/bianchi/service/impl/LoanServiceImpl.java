package io.github.bianchi.service.impl;

import io.github.bianchi.api.dto.LoanFilterDTO;
import io.github.bianchi.exception.BusinessException;
import io.github.bianchi.model.entity.Loan;
import io.github.bianchi.model.repository.LoanRepository;
import io.github.bianchi.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class LoanServiceImpl implements LoanService {

    LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        if (repository.existsByBookNotReturned(loan.getBook())) {
            throw new BusinessException("Livro já emprestado");
        }
        return repository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return repository.save(loan);
    }

    @Override
    public Page<Loan> find(LoanFilterDTO filter, Pageable pageable) {
        return repository.findByIsbnOrCustomer(filter.getIsbn(), filter.getCustomer(), pageable);
    }
}
