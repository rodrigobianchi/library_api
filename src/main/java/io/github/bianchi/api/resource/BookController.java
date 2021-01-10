package io.github.bianchi.api.resource;

import io.github.bianchi.api.dto.BookDTO;
import io.github.bianchi.api.exception.ApiErrors;
import io.github.bianchi.model.entity.Book;
import io.github.bianchi.exception.BusinessException;
import io.github.bianchi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService service;
    private ModelMapper modelMapper;

    public BookController(BookService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO dto) {
        /* Alternativa a utilização de model mapper
        Lembrando que model mapper só funcionará caso dto e entity tenham a mesma nomenclatura de atributos
        Book entity = Book.builder().title(dto.getTitle()).author(dto.getAuthor()).isbn(dto.getIsbn()).build();
        */

        Book entity = modelMapper.map(dto, Book.class);

        entity = service.save(entity);

        /*
        return BookDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .author(entity.getAuthor())
                .isbn(entity.getIsbn()).build();
         */

        return modelMapper.map(entity, BookDTO.class);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationExceptions(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();

        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleBusinessException(BusinessException exception) {
        return new ApiErrors(exception);
    }
}
