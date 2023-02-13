package com.treinamento.springboot.libraryapi.api.resource;

import com.treinamento.springboot.libraryapi.api.dto.BookDTO;
import com.treinamento.springboot.libraryapi.api.exception.ApiErros;
import com.treinamento.springboot.libraryapi.api.model.entity.Book;
import com.treinamento.springboot.libraryapi.service.BookService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BoockControler {

    private BookService bookService;
    private ModelMapper modelMapper;

    public BoockControler(BookService bookService, ModelMapper modelMapper) {
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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErros handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        return new ApiErros(bindingResult);
    }
}
