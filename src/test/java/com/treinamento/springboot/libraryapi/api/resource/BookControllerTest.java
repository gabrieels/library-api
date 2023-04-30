package com.treinamento.springboot.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.treinamento.springboot.libraryapi.api.dto.BookDTO;
import com.treinamento.springboot.libraryapi.api.model.entity.Book;
import com.treinamento.springboot.libraryapi.exception.BusinessException;
import com.treinamento.springboot.libraryapi.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest // serve para testes unitarios
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc; // ira mocar as requisicoes da api
    private static final String BOOK_API = "/api/books";
    @MockBean
    private BookService bookServiceMock;

    @Test
    void deveriaCriarUmLivroComSucesso() throws Exception {

        // cenario
        BookDTO bookDTO = createNewBook();
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
    void deveriaLancarErroDeValicao() throws Exception {
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

    @Test
    void deveriaLancarErroAoTentarCadastrarUmLivroComSbnExistente() throws Exception {
        BookDTO bookDTO = createNewBook();
        String json = new ObjectMapper().writeValueAsString(bookDTO);

        given(bookServiceMock.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException("ISBN já cadastrado"));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("ISBN já cadastrado"));

    }

    @Test
    void deveriaObterInformacoesLivro() throws Exception {

        // cenario
        Long id = 1L;
        Book book = Book.builder()
                .id(id)
                .author(createNewBook().getAuthor())
                .title(createNewBook().getTitle())
                .isbn(createNewBook().getIsbn())
                .build();

        given(bookServiceMock.getById(id)).willReturn(of(book));

        // execucao
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        // verificacao
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));
    }

    @Test
    void deveriaLancarMensagemErroAoBuscarLivroInexistente() throws Exception {

        // cenario
        given(bookServiceMock.getById(Mockito.anyLong())).willReturn(Optional.empty());

        // execucao
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1L))
                .accept(MediaType.APPLICATION_JSON);

        // verificacao
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void deveriaRemoverLivro() throws Exception {

        // cenario
        given(bookServiceMock.getById(anyLong())).willReturn(of(Book.builder().id(1L).build()));

        // execucao
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1L));

        // verificacao
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isNoContent());
    }

    @Test
    void deveriaLancarMensagemErroAoRemoverLivro() throws Exception {

        // cenario
        given(bookServiceMock.getById(anyLong())).willReturn(empty());

        // execucao
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1L));

        // verificacao
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void deveriaAtualizarBook() throws Exception {

        // cenarario
        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createNewBook());

        Book bookUpdated = Book.builder().id(id).title("Star Wars").author("George Lucas").isbn("8528620301").build();
        given(bookServiceMock.getById(id))
                .willReturn(of(bookUpdated));

        Book bookEsperado = Book.builder().id(id).author("Arthur").title("As aventuras").isbn("8528620301").build();
        given(bookServiceMock.update(bookUpdated))
                .willReturn(bookEsperado);

        // execucao
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + id))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        // verificacao
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value("8528620301"));
    }

    @Test
    void deveriaLancarMensagemErroAoAtualizarBookInexistente() throws Exception {

        // cenarario
        String json = new ObjectMapper().writeValueAsString(createNewBook());
        BDDMockito.given(bookServiceMock.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        // execucao
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1L))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        // verificacao
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void deveriaBuscarBook() throws Exception {
        Long id = 1L;
        Book book = Book.builder()
                .id(id)
                .title(createNewBook().getTitle())
                .author(createNewBook().getAuthor())
                .isbn(createNewBook().getIsbn())
                .build();

        BDDMockito.given(bookServiceMock.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1));

        String queryString = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

    private static BookDTO createNewBook() {
        return BookDTO.builder()
                .author("Arthur")
                .title("As aventuras")
                .isbn("001")
                .build();
    }
}
