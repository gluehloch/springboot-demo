package de.winkler.springboot.logger;

import java.util.function.Supplier;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

public class ExceptionMessageFormatter {

    public static String format(String messagePattern, Object... args) {
        FormattingTuple ft = MessageFormatter.arrayFormat(messagePattern, args);
        return ft.getMessage();
    }

    public static String format(Supplier<String> supplier, Object... args) {
        return format(supplier.get(), args);
    }

}
