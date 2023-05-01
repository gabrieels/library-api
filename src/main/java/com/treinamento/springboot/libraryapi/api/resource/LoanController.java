package com.treinamento.springboot.libraryapi.api.resource;

import com.treinamento.springboot.libraryapi.api.dto.LoanDTO;
import com.treinamento.springboot.libraryapi.api.model.entity.Book;
import com.treinamento.springboot.libraryapi.api.model.entity.Loan;
import com.treinamento.springboot.libraryapi.service.BookService;
import com.treinamento.springboot.libraryapi.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;

    public LoanController(LoanService loanService, BookService bookService) {
        this.loanService = loanService;
        this.bookService = bookService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO loanDTO) {
        Book book = bookService
                .getBookByIsbn(loanDTO.getIsbn())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isnb"));

        Loan entity = Loan.builder()
                .book(book)
                .customer(loanDTO.getCustomer())
                .loanDate(LocalDate.now()).build();

        entity = loanService.save(entity);
        return entity.getId();
    }
}
