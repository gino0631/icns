package com.github.gino0631.icns;

import com.github.gino0631.common.io.InputStreamSupplier;
import com.github.gino0631.common.io.Writable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

/**
 * A representation of ICNS icon data.
 */
public interface IcnsIcons extends Writable, Closeable {
    /**
     * ICNS icon entry.
     */
    interface Entry extends InputStreamSupplier {
        /**
         * Gets OSType identifier of the icon type.
         *
         * @return a string corresponding to a four-byte type identifier
         */
        String getOsType();

        /**
         * Gets type of the icon.
         *
         * @return type of the icon, or {@code null} if it could not be determined (e.g because the entry is a special entry, not an icon)
         */
        IcnsType getType();

        /**
         * Gets size of the icon.
         *
         * @return size of icon data in bytes
         */
        int getSize();

        /**
         * Gets input stream of the entry.
         *
         * @return input stream
         * @throws IOException if an I/O error occurs
         */
        InputStream newInputStream() throws IOException;
    }

    /**
     * Gets a list of icon entries.
     *
     * @return unmodifiable list of entries
     */
    List<Entry> getEntries();

    /**
     * Writes the icon data to the specified output stream.
     * <p>
     * Care must be taken not to write to the same file the data was loaded from.
     *
     * @param output output stream to write to
     * @throws IOException if an I/O error occurs
     */
    void writeTo(OutputStream output) throws IOException;

    /**
     * Loads icon data from a file.
     *
     * @param file file to read from
     * @return a representation of ICNS icon data
     * @throws IOException if an I/O error occurs
     */
    static IcnsIcons load(Path file) throws IOException {
        return IcnsIconsImpl.load(file);
    }

    /**
     * Loads icon data from a supplier of input streams.
     *
     * @param streamSupplier supplier of streams to read from
     * @return a representation of ICNS icon data
     * @throws IOException if an I/O error occurs
     */
    static IcnsIcons load(InputStreamSupplier streamSupplier) throws IOException {
        return IcnsIconsImpl.load(streamSupplier);
    }
}
