package com.pulseboard.core.application.port.in;

import com.pulseboard.core.domain.DeadLetter;
import java.util.List;

public interface DeadLetterUseCase {
    List<DeadLetter> list(int limit);
}
