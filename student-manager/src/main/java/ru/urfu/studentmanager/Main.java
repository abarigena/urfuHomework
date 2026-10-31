package ru.urfu.studentmanager;

import java.io.Console;
import java.nio.charset.Charset;
import java.util.Scanner;

/** Точка входа в приложение. */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in, consoleCharset())) {
            new ConsoleApp(scanner).run();
        }
    }

    /** Кодировка консоли, чтобы кириллица при вводе читалась верно и в Windows. */
    private static Charset consoleCharset() {
        Console console = System.console();
        return console != null ? console.charset() : Charset.defaultCharset();
    }
}
