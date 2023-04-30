package com.treinamento.springboot.libraryapi.service;

import com.treinamento.springboot.libraryapi.api.model.entity.Book;
import com.treinamento.springboot.libraryapi.api.model.repository.BookRepository;
import com.treinamento.springboot.libraryapi.exception.BusinessException;
import com.treinamento.springboot.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void deveriaObterBookPorId() {
        Long id = 1L;
        Book book = createValidBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = bookService.getById(id);

        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(book.getId());
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    void deveriaRetornarVazioAoObterLivroPorId() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());
        Optional<Book> book = bookService.getById(1L);
        assertThat(book.isPresent()).isFalse();
    }

    @Test
    void deveriaDeletarBook() {
        Book book = Book.builder().id(1L).build();

        assertDoesNotThrow(() -> bookService.delete(book));

        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }

    @Test
    void deveriaLancarMensagemErroAoDeletarBook() {
        Book book = null;

        assertThrows(IllegalArgumentException.class, () -> bookService.delete(book));

        Mockito.verify(repository, Mockito.never()).delete(book);
    }

    @Test
    void deveriaAtualizarBook() {
        Book book = Book.builder().id(1L).build();
        Book bookUpdated = createValidBook();
        book.setId(1L);

        Mockito.when(repository.save(book)).thenReturn(bookUpdated);

        Book bookEsperado = bookService.update(book);

        assertThat(bookUpdated.getId()).isEqualTo(bookEsperado.getId());
        assertThat(bookUpdated.getTitle()).isEqualTo(bookEsperado.getTitle());
        assertThat(bookUpdated.getAuthor()).isEqualTo(bookEsperado.getAuthor());
        assertThat(bookUpdated.getIsbn()).isEqualTo(bookEsperado.getIsbn());
    }

    @Test
    void deveriaLancarMensagemErroAoAtualizarBook() {
        Book book = null;

//        assertThrows(IllegalArgumentException.class, () -> bookService.update(book));

        Throwable throwable = Assertions.catchThrowable(() -> bookService.update(book));

        //verificacao
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book cant be null");

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    void deveriaBuscarBook() {
        Book book = createValidBook();
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(Arrays.asList(book), pageRequest, 1);

        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(page);

        Page<Book> resultado = bookService.find(book, pageRequest);

        assertThat(resultado.getTotalElements()).isEqualTo(page.getTotalElements());
        assertThat(resultado.getContent()).isEqualTo(page.getContent());
        assertThat(resultado.getPageable().getPageNumber()).isZero();
        assertThat(resultado.getPageable().getPageSize()).isEqualTo(10);
    }

    private static Book createValidBook() {
        return Book.builder().author("Fulano").title("As aventuras").isbn("12345").build();
    }

}
