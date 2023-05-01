package com.treinamento.springboot.libraryapi.service;

import com.treinamento.springboot.libraryapi.api.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface BookService {
    Book save(Book book);

    Optional<Book> getById(Long id);

    void delete(Book book);

    Book update(Book book);

    Page<Book> find(Book filter, Pageable pageable);

    Optional<Book> getBookByIsbn(String isbn);
}
