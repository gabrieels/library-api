package com.treinamento.springboot.libraryapi.service;

import com.treinamento.springboot.libraryapi.api.model.entity.Loan;
import org.springframework.stereotype.Service;

@Service
public interface LoanService {
    Loan save(Loan loan);
}
