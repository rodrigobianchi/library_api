package io.github.bianchi.service.impl;

import io.github.bianchi.exception.BusinessException;
import io.github.bianchi.model.entity.Loan;
import io.github.bianchi.model.repository.LoanRepository;
import io.github.bianchi.service.LoanService;

public class LoanServiceImpl implements LoanService {

    LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        if(repository.existsByBookNotReturned(loan.getBook())) {
            throw new BusinessException("Livro já emprestado");
        }
        return repository.save(loan);
    }
}
