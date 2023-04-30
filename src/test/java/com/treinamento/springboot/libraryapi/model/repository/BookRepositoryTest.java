package com.treinamento.springboot.libraryapi.model.repository;

import com.treinamento.springboot.libraryapi.api.model.entity.Book;
import com.treinamento.springboot.libraryapi.api.model.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;
    @Autowired
    BookRepository bookRepository;

    @Test
    void deveriaRetornarVerdadeiroQuandoExistirIsbnInformado() {

        // cenario
        String isbn = "1234";
        Book book = creatNewBook(isbn);
        testEntityManager.persist(book);

        // execucao
        boolean exists = bookRepository.existsByIsbn(isbn);

        // verificacao
        assertThat(exists).isTrue();
    }

    @Test
    void deveriaRetornarFalsoQuandoNaoExistirIsbnInformado() {

        // cenario
        String isbn = "1234";

        // execucao
        boolean exists = bookRepository.existsByIsbn(isbn);

        // verificacao
        assertThat(exists).isFalse();
    }

    @Test
    void deveriaObterLivroPorId() {
        Book entity = creatNewBook("1234");
        testEntityManager.persist(entity);

        Optional<Book> foundBook = bookRepository.findById(entity.getId());

        assertThat(foundBook).isPresent();
    }

    @Test
    void deveriaSalvarBook() {
        Book entity = creatNewBook("123");

        Book bookSalvo = bookRepository.save(entity);

        assertThat(bookSalvo.getId()).isNotNull();
    }

    @Test
    void deveriaDeletarBook() {
        Book entity = creatNewBook("12345");
        testEntityManager.persist(entity);

        Book foundBook = testEntityManager.find(Book.class, entity.getId());

        bookRepository.delete(foundBook);

        Book bookDeleted = testEntityManager.find(Book.class, entity.getId());
        assertThat(bookDeleted).isNull();
    }

    private static Book creatNewBook(String isbn) {
        return Book.builder().title("Aventuras").author("Fulano").isbn(isbn).build();
    }
}
