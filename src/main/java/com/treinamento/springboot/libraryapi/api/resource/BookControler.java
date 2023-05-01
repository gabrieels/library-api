package com.treinamento.springboot.libraryapi.api.resource;

import com.treinamento.springboot.libraryapi.api.dto.BookDTO;
import com.treinamento.springboot.libraryapi.api.exception.ApiErros;
import com.treinamento.springboot.libraryapi.api.model.entity.Book;
import com.treinamento.springboot.libraryapi.exception.BusinessException;
import com.treinamento.springboot.libraryapi.service.BookService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookControler {

    private BookService bookService;
    private ModelMapper modelMapper;

    public BookControler(BookService bookService, ModelMapper modelMapper) {
        this.bookService = bookService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO criar(@Valid @RequestBody BookDTO bookDTO) {
        Book entity = modelMapper.map(bookDTO, Book.class);
        entity = bookService.save(entity);
        return modelMapper.map(entity, BookDTO.class);
    }

    @GetMapping("{id}")
    public BookDTO obterBook(@PathVariable Long id) {
        return bookService
                .getById(id)
                .map(book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerBook(@PathVariable Long id) {
        Book book = bookService
                .getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        bookService.delete(book);
    }

    @PutMapping("{id}")
    public BookDTO update(@PathVariable Long id, BookDTO bookDTO) {
        return bookService.getById(id).map(entity -> {
                entity.setAuthor(bookDTO.getAuthor());
                entity.setTitle(bookDTO.getTitle());
                entity = bookService.update(entity);
                return modelMapper.map(entity, BookDTO.class);
            })
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @GetMapping
    public Page<BookDTO> find(BookDTO bookDTO, Pageable pageable) {
        Book filter = modelMapper.map(bookDTO, Book.class);
        Page<Book> resultado = bookService.find(filter, pageable);
        List<BookDTO> list = resultado.getContent().stream()
                .map(entity -> modelMapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<>(list, pageable, resultado.getTotalElements());
    }
}
