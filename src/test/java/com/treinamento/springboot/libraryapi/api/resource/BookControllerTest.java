package com.treinamento.springboot.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.treinamento.springboot.libraryapi.api.dto.BookDTO;
import com.treinamento.springboot.libraryapi.api.model.entity.Book;
import com.treinamento.springboot.libraryapi.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest // serve para testes unitarios
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc; // ira mocar as requisicoes da api
    private static String BOOK_API = "/api/books";
    @MockBean
    private BookService bookServiceMock;

    @Test
    public void deveriaCriarUmLivroComSucesso() throws Exception {

        // cenario
        BookDTO bookDTO = BookDTO.builder().author("Arthur").title("As aventuras").isbn("001").build();
        String json = new ObjectMapper().writeValueAsString(bookDTO);
        Book bookEsperado = Book.builder().id(10l).author("Arthur").title("As aventuras").isbn("001").build();

        // Dado
        given(bookServiceMock.save(Mockito.any(Book.class)))
                .willReturn(bookEsperado);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // Entao
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(10l))
                .andExpect(jsonPath("title").value(bookDTO.getTitle()))
                .andExpect(jsonPath("author").value(bookDTO.getAuthor()))
                .andExpect(jsonPath("isbn").value(bookDTO.getIsbn()));

    }

    @Test
    public void deveriaLancarErroDeValicao() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }

}
