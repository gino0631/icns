package com.github.gino0631.icns;

import com.github.gino0631.common.io.IoFiles;
import com.github.gino0631.common.io.IoStreams;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

final class IcnsBuilderImpl implements IcnsBuilder {
    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final List<IcnsIcons.Entry> entries;
    private final Path icnsFile;
    private final SeekableByteChannel outputChannel;
    private long pos;

    IcnsBuilderImpl() {
        try {
            entries = new ArrayList<>();
            icnsFile = IoFiles.createTempFile("icns-");
            outputChannel = Files.newByteChannel(icnsFile, StandardOpenOption.WRITE);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public IcnsBuilder add(IcnsType type, InputStream input) throws IOException {
        return add(type.getOsType(), input);
    }

    @Override
    public synchronized IcnsBuilder add(String osType, InputStream input) throws IOException {
        checkNotClosed();
        Objects.requireNonNull(osType);
        Objects.requireNonNull(input);

        outputChannel.position(pos);
        final long start = pos;

        long size = IoStreams.copy(input, Channels.newOutputStream(outputChannel));
        entries.add(new IcnsIconsImpl.EntryImpl(osType, IcnsType.of(osType), (int) size,
                () -> Channels.newInputStream(Files.newByteChannel(icnsFile, StandardOpenOption.READ).position(start)), 0));
        pos += size;

        return this;
    }

    @Override
    public synchronized IcnsIcons build() {
        checkNotClosed();

        IcnsIcons icnsIcons = null;

        try {
            outputChannel.close();

            icnsIcons = new IcnsIconsImpl(entries, this::cleanup);

            return icnsIcons;

        } catch (IOException e) {
            throw new RuntimeException(e);

        } finally {
            if (icnsIcons == null) {
                // Something went wrong - clean up now
                cleanup();
            }
        }
    }

    @Override
    public synchronized void close() throws IOException {
        if (outputChannel.isOpen()) {
            outputChannel.close();

            cleanup();
        }
    }

    private void checkNotClosed() {
        if (!outputChannel.isOpen()) {
            throw new IllegalStateException("The builder is closed");
        }
    }

    private void cleanup() {
        deleteTempFile(icnsFile);
    }

    private static void deleteTempFile(Path file) {
        IoFiles.delete(file, e -> logger.log(Level.WARNING, MessageFormat.format("Error deleting {0}", file), e));
    }
}
