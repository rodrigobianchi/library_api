package io.github.bianchi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bianchi.api.dto.LoanDTO;
import io.github.bianchi.exception.BusinessException;
import io.github.bianchi.model.entity.Book;
import io.github.bianchi.model.entity.Loan;
import io.github.bianchi.service.BookService;
import io.github.bianchi.service.LoanService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService bookService;

    @MockBean
    LoanService service;

    @Test
    @DisplayName("Create Loan Success")
    public void createLoanTest() throws Exception {
        LoanDTO dto = createNewLoanDTO();
        Loan entity = createNewLoan();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(createNewBook()));

        BDDMockito.given(service.save(Mockito.any(Loan.class))).willReturn(entity);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("Throws Exception When Try Create Loan With Invalid Isbn")
    public void createLoanWithInvalidIsbn() throws Exception {
        LoanDTO dto = createNewLoanDTO();
        String json = new ObjectMapper().writeValueAsString(dto);
        String mensagemErro = "Isbn não encontrado";

        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(mensagemErro));
    }

    @Test
    @DisplayName("Throws Exception When Try Create Loan With Book Loaned")
    public void createLoanWithBookLoaned() throws Exception {
        LoanDTO dto = createNewLoanDTO();
        String json = new ObjectMapper().writeValueAsString(dto);
        String mensagemErro = "Book already loaned";
        Loan entity = createNewLoan();

        BDDMockito.given(bookService.getBookByIsbn("123")).willThrow(new BusinessException("Book already loaned"));

        BDDMockito.given(service.save(Mockito.any(Loan.class))).willReturn(entity);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(mensagemErro));
    }

    private Loan createNewLoan() {
        return Loan.builder().id(1L).customer("Bianchi").book(createNewBook()).loanDate(LocalDate.now()).build();
    }

    private LoanDTO createNewLoanDTO() {
        return LoanDTO.builder().isbn("123").customer("Bianchi").build();
    }

    private Book createNewBook() {
        return Book.builder().id(10L).title("Livro 1").author("Gonzalez").isbn("123").build();
    }
}
