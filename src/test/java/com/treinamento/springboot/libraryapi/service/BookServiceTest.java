package com.treinamento.springboot.libraryapi.service;

import com.treinamento.springboot.libraryapi.api.model.entity.Book;
import com.treinamento.springboot.libraryapi.api.model.repository.BookRepository;
import com.treinamento.springboot.libraryapi.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService bookService;
    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp() {
        this.bookService = new BookServiceImpl(repository);
    }

    @Test
    public void deveriaSavarBook() {
        // cenario
        Book book = Book.builder().author("Fulano").title("As aventuras").isbn("12345").build();
        when(repository.save(book))
                .thenReturn(Book.builder()
                        .id(1l)
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

}
