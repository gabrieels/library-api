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
        Book book = Book.builder().title("Aventuras").author("Fulano").isbn(isbn).build();
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
}
