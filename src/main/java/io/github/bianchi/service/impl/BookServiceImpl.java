package io.github.bianchi.service.impl;

import io.github.bianchi.model.entity.Book;
import io.github.bianchi.exception.BusinessException;
import io.github.bianchi.model.repository.BookRepository;
import io.github.bianchi.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("Isbn já cadastrado");
        }
        return repository.save(book);
    }
}
