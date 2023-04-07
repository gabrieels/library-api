package com.treinamento.springboot.libraryapi.service;

import com.treinamento.springboot.libraryapi.api.model.entity.Book;
import com.treinamento.springboot.libraryapi.api.model.repository.BookRepository;
import com.treinamento.springboot.libraryapi.exception.BusinessException;
import com.treinamento.springboot.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class BookServiceTest {

    BookService bookService;
    @MockBean
    BookRepository repository;

    @BeforeEach
    void setUp() {
        this.bookService = new BookServiceImpl(repository);
    }

    @Test
    void deveriaSavarBook() {
        // cenario
        Book book = createValidBook();
        when(repository.save(book))
                .thenReturn(Book.builder()
                        .id(1L)
                        .title("As aventuras")
                        .author("Fulano")
                        .isbn("12345")
                        .build()
                );

        // execucao
        Book bookEsperado = bookService.save(book);

        // verificacao
        assertThat(bookEsperado.getId()).isNotNull();
        assertThat(bookEsperado.getAuthor()).isEqualTo("Fulano");
        assertThat(bookEsperado.getTitle()).isEqualTo("As aventuras");
        assertThat(bookEsperado.getIsbn()).isEqualTo("12345");
    }

    @Test
    void deveriaLancarErroIsbCadastrado() {
        // cenario
        Book book = createValidBook();
        when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        // execucao
        Throwable throwable = Assertions.catchThrowable(() -> bookService.save(book));

        //verificacao
        assertThat(throwable)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado");

        verify(repository, Mockito.never()).save(book);
    }

    private static Book createValidBook() {
        return Book.builder().author("Fulano").title("As aventuras").isbn("12345").build();
    }

}
