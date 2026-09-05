package com.parkyc.jypword.wordbook.service;


import com.parkyc.jypword.wordbook.repository.WordBookJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WordBookService {

    private final WordBookJpaRepository wordBookRepository;
}
