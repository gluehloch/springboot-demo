package de.winkler.springboot.logger;

import org.junit.jupiter.api.Test;

import static de.winkler.springboot.logger.ExceptionMessageFormatter.format;
import static org.assertj.core.api.Assertions.assertThat;

public class MessageFormatterTest {

    @Test
    void messageFormatter() {
        assertThat(format("Das ist ein {}.", "Test")).isEqualTo("Das ist ein Test.");
        assertThat(format("{}, {}, {}", 1, 2, 3)).isEqualTo("1, 2, 3");
        assertThat(format(() -> "{}, {}, {}", 1, 2, 3)).isEqualTo("1, 2, 3");
    }

}
