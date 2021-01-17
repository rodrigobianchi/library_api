package io.github.bianchi.api.resource;

import io.github.bianchi.api.dto.BookDTO;
import io.github.bianchi.api.dto.LoanDTO;
import io.github.bianchi.api.dto.LoanFilterDTO;
import io.github.bianchi.api.dto.ReturnedLoanDTO;
import io.github.bianchi.model.entity.Book;
import io.github.bianchi.model.entity.Loan;
import io.github.bianchi.service.BookService;
import io.github.bianchi.service.LoanService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private BookService bookService;
    private LoanService service;
    private ModelMapper modelMapper;

    public LoanController(LoanService service, BookService bookService, ModelMapper modelMapper) {
        this.service = service;
        this.bookService = bookService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO dto) {
        Book book = bookService.getBookByIsbn(dto.getIsbn())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Isbn não encontrado"));
        Loan entity = Loan.builder().book(book).customer(dto.getCustomer()).loanDate(LocalDate.now()).build();

        entity = service.save(entity);
        return entity.getId();
    }

    @PatchMapping("{id}")
    public void returnedBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto) {
        Loan loan = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(dto.getReturned());

        service.update(loan);
    }

    @GetMapping
    public Page<LoanDTO> find(LoanFilterDTO filter, Pageable pageable) {
        Page<Loan> result = service.find(filter, pageable);

        List<LoanDTO> list = result.getContent().stream().map(entity -> {
                Book book = entity.getBook();
                BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
                LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
                loanDTO.setBook(bookDTO);
                return loanDTO;
        }).collect(Collectors.toList());

        return new PageImpl<LoanDTO>(list, pageable, result.getTotalElements());
    }
}
