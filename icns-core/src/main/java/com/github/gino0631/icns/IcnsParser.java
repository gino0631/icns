package com.github.gino0631.icns;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ICNS format parser.
 */
public interface IcnsParser {
    String TOC = "TOC ";

    @FunctionalInterface
    interface Listener {
        /**
         * Listener method called when an icon is found.
         * <p>
         * It is not required (though not forbidden) to close the provided input stream.
         *
         * @param osType OSType identifier of the icon type
         * @param type   type of the icon
         * @param size   size of the icon
         * @param input  input stream to read icon data from
         * @return {@code true} to continue parsing, {@code false} to stop
         * @throws IOException if an I/O error occurs
         */
        boolean onIcon(String osType, IcnsType type, int size, InputStream input) throws IOException;
    }

    /**
     * Parses the provided ICNS file.
     *
     * @param file     ICNS file
     * @param listener event listener
     * @throws IOException if an I/O error occurs
     */
    static void parse(Path file, Listener listener) throws IOException {
        try (InputStream is = Files.newInputStream(file)) {
            parse(is, listener);
        }
    }

    /**
     * Parses the provided ICNS stream.
     * <p>
     * The stream will not be closed afterwards.
     *
     * @param input    ICNS input stream
     * @param listener event listener
     * @throws IOException if an I/O error occurs
     */
    static void parse(InputStream input, Listener listener) throws IOException {
        IcnsIconsImpl.parse(input, listener);
    }
}
