package com.github.gino0631.icns;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * ICNS format builder.
 */
public interface IcnsBuilder extends Closeable {
    /**
     * Adds an icon from the specified input stream.
     * Equivalent to calling {@link #add(String, InputStream)} with {@code type.getOsType()} as the first argument.
     *
     * @param type  icon type
     * @param input input stream to read icon data from
     * @return this builder
     * @throws IOException if an I/O error occurs
     */
    IcnsBuilder add(IcnsType type, InputStream input) throws IOException;

    /**
     * Adds an icon from the specified input stream.
     * <p>
     * The input stream will not be closed afterwards.
     *
     * @param osType OSType identifier of the icon type
     * @param input  input stream to read icon data from
     * @return this builder
     * @throws IOException if an I/O error occurs
     */
    IcnsBuilder add(String osType, InputStream input) throws IOException;

    /**
     * Builds ICNS icon data.
     *
     * @return a representation of the built icon data
     */
    IcnsIcons build();

    /**
     * Gets an instance of {@code IcnsBuilder}.
     *
     * @return a new instance of the builder
     */
    static IcnsBuilder getInstance() {
        return new IcnsBuilderImpl();
    }
}
