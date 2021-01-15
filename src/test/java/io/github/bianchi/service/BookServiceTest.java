package io.github.bianchi.service;

import io.github.bianchi.exception.BusinessException;
import io.github.bianchi.model.entity.Book;
import io.github.bianchi.model.repository.BookRepository;
import io.github.bianchi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    private void setUp() {
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Create Book Success")
    public void saveBookTest() {
        //cenary
        Book book = createNewBook();

        //simulate comportament of method
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);

        Mockito.when(repository.save(book)).thenReturn(Book.builder()
                .id(1L)
                .title("Livro 1")
                .author("Bianchi")
                .isbn("123").build());

        //execution
        Book savedBook = service.save(book);

        //verifications
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Livro 1");
        assertThat(savedBook.getAuthor()).isEqualTo("Bianchi");
        assertThat(savedBook.getIsbn()).isEqualTo("123");
    }

    @Test
    @DisplayName("Should Not Save Book With Duplicated Isbn")
    public void shouldNotSaveBookWithDuplicatedIsbn() {
        //cenary
        Book book = createNewBook();

        //simulate comportament of method
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //execution
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        //verifications
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado");

        //Garante que o método save nunca foi acionado
        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Get Book By Id")
    public void getByIdTest() {
        Long id = 10L;
        Book mockBook = createNewBook();
        mockBook.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(mockBook));

        Optional<Book> book = service.getById(id);

        assertThat(book.isPresent()).isTrue();
        assertThat(book.get().getId()).isEqualTo(id);
        assertThat(book.get().getTitle()).isEqualTo(mockBook.getTitle());
        assertThat(book.get().getAuthor()).isEqualTo(mockBook.getAuthor());
        assertThat(book.get().getIsbn()).isEqualTo(mockBook.getIsbn());
    }

    @Test
    @DisplayName("Book Not Found By Id")
    public void bookNotFoundById() {
        Long id = 10L;

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Book> book = service.getById(id);

        assertThat(book.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Delete Book Success")
    public void deleteBookTest() {
        Book book = Book.builder().id(10L).build();

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(book));

        Mockito.verify(repository, Mockito.only()).delete(book);
    }

    @Test
    @DisplayName("Should Not Delete Book With Id Null")
    public void shouldNotDeleteBookWithIdNull() {
        Book book = Book.builder().build();

        Throwable exception = Assertions.catchThrowable(() -> service.delete(book));
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book id can be null");

        Mockito.verify(repository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Update Book Success")
    public void updateBookTest() {
        Book updatingBook = Book.builder().id(10L).build();

        Book mockUpdatedBook = createNewBook();
        mockUpdatedBook.setId(10L);

        Mockito.when(repository.save(updatingBook)).thenReturn(mockUpdatedBook);

        Book updatedBook = service.update(updatingBook);

        assertThat(updatedBook.getId()).isEqualTo(mockUpdatedBook.getId());
        assertThat(updatedBook.getTitle()).isEqualTo(mockUpdatedBook.getTitle());
        assertThat(updatedBook.getAuthor()).isEqualTo(mockUpdatedBook.getAuthor());
        assertThat(updatedBook.getIsbn()).isEqualTo(mockUpdatedBook.getIsbn());
    }

    @Test
    @DisplayName("Should Not Update Book With Id Null")
    public void shouldNotUpdateBookWithIdNull() {
        Book book = Book.builder().build();

        Throwable exception = Assertions.catchThrowable(() -> service.update(book));
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book id can be null");

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Find Books")
    public void findBookTest() {
        Book book = createNewBook();
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<Book> page = new PageImpl<>(Arrays.asList(book), pageRequest, 1);

        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Page<Book> result = service.find(book, pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(Arrays.asList(book));
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Get Book By Isbn")
    public void getBookByIsbnTest() {
        String isbn = "123";
        Book mockBook = createNewBook();

        Mockito.when(repository.findByIsbn(isbn)).thenReturn(Optional.of(mockBook));

        Optional<Book> book = service.getBookByIsbn(isbn);

        assertThat(book.isPresent()).isTrue();
        assertThat(book.get().getTitle()).isEqualTo(mockBook.getTitle());
        assertThat(book.get().getAuthor()).isEqualTo(mockBook.getAuthor());
        assertThat(book.get().getIsbn()).isEqualTo(mockBook.getIsbn());

        Mockito.verify(repository, Mockito.timeout(1)).findByIsbn(isbn);
    }

    private Book createNewBook() {
        return Book.builder().title("Livro 1").author("Bianchi").isbn("123").build();
    }

}
