package io.github.bianchi.service;

import io.github.bianchi.model.entity.Book;
import io.github.bianchi.exception.BusinessException;
import io.github.bianchi.model.repository.BookRepository;
import io.github.bianchi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
    public void shouldNotSaveBookWithDuplicatedIsbn() throws Exception {
        //cenary
        Book book = createNewBook();

        //simulate comportament of method
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //execution
        Throwable exception = Assertions.catchThrowable( () -> service.save(book));

        //verifications
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado");

        //Garante que o método save nunca foi acionado
        Mockito.verify(repository, Mockito.never()).save(book);
    }

    private Book createNewBook() {
        return Book.builder().title("Livro 1").author("Bianchi").isbn("123").build();
    }
}
