package com.treinamento.springboot.libraryapi.service.impl;

import com.treinamento.springboot.libraryapi.api.model.entity.Book;
import com.treinamento.springboot.libraryapi.api.model.repository.BookRepository;
import com.treinamento.springboot.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        return repository.save(book);
    }
}
