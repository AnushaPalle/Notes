package com.pulseboard.core.adapter.in.web;

import com.pulseboard.core.application.port.in.DeadLetterUseCase;
import com.pulseboard.core.domain.DeadLetter;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class DeadLetterController {

    private final DeadLetterUseCase deadLetters;

    public DeadLetterController(DeadLetterUseCase deadLetters) {
        this.deadLetters = deadLetters;
    }

    @GetMapping("/dead-letters")
    public List<DeadLetter> list(@RequestParam(defaultValue = "50") int limit) {
        return deadLetters.list(limit);
    }
}
