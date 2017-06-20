package com.github.gino0631.icns;

import com.github.gino0631.common.io.InputStreamSupplier;
import com.github.gino0631.common.io.IoStreams;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

final class IcnsIconsImpl implements IcnsIcons, IcnsParser {
    private static final int MAGIC = toInt("icns");
    private static final int HEADER_SIZE = 8;

    private final List<Entry> entries;
    private final Closeable closeable;

    static class EntryImpl implements Entry {
        private final String osType;
        private final IcnsType type;
        private final int size;
        private final InputStreamSupplier streamSupplier;
        private final long offs;

        EntryImpl(String osType, IcnsType type, int size, InputStreamSupplier streamSupplier, long offs) {
            this.osType = osType;
            this.type = type;
            this.size = size;
            this.streamSupplier = streamSupplier;
            this.offs = offs;
        }

        @Override
        public String getOsType() {
            return osType;
        }

        @Override
        public IcnsType getType() {
            return type;
        }

        @Override
        public int getSize() {
            return size;
        }

        @Override
        public InputStream newInputStream() throws IOException {
            InputStream is = streamSupplier.newInputStream();
            if (offs > 0) {
                if (IoStreams.skip(is, offs) != offs) {
                    throw new IOException(MessageFormat.format("Stream should contain at least {0} bytes, but it does not", offs + size));
                }
            }

            return IoStreams.limit(is, size);
        }
    }

    IcnsIconsImpl(List<Entry> entries, Closeable closeable) {
        this.entries = Collections.unmodifiableList(entries);
        this.closeable = closeable;
    }

    @Override
    public List<Entry> getEntries() {
        return entries;
    }

    @Override
    public void writeTo(OutputStream output) throws IOException {
        int fileSize = HEADER_SIZE;

        for (Entry e : entries) {
            fileSize += (HEADER_SIZE + e.getSize());
        }

        // Header
        DataOutputStream dos = new DataOutputStream(output);
        dos.writeInt(MAGIC);
        dos.writeInt(fileSize);

        // TOC
        dos.writeInt(toInt(TOC));
        dos.writeInt(HEADER_SIZE + (entries.size() * HEADER_SIZE));
        for (Entry e : entries) {
            dos.writeInt(toInt(e.getOsType()));
            dos.writeInt(HEADER_SIZE + e.getSize());
        }

        // Data
        for (Entry e : entries) {
            dos.writeInt(toInt(e.getOsType()));
            dos.writeInt(HEADER_SIZE + e.getSize());
            IoStreams.copy(e.newInputStream(), dos);
        }

        dos.flush();
    }

    @Override
    public void close() throws IOException {
        if (closeable != null) {
            closeable.close();
        }
    }

    static IcnsIcons load(Path file) throws IOException {
        return load(InputStreamSupplier.of(file));
    }

    static IcnsIcons load(InputStreamSupplier streamSupplier) throws IOException {
        List<Entry> entries = new ArrayList<>();

        try (InputStream is = streamSupplier.newInputStream()) {
            parse(is, new Listener() {
                byte[] buf = new byte[4];
                long offs = HEADER_SIZE;

                @Override
                public boolean onIcon(String osType, IcnsType type, int size, InputStream input) throws IOException {
                    if (osType.equals(TOC)) {
                        if ((offs != HEADER_SIZE) || !entries.isEmpty()) {
                            throw new IllegalStateException("TOC is supposed to be the first entry");
                        }

                        offs += HEADER_SIZE;
                        offs += size;

                        AtomicLong inputCounter = new AtomicLong();
                        DataInputStream dis = new DataInputStream(IoStreams.count(input, inputCounter::addAndGet));

                        while (inputCounter.get() < size) {
                            dis.readFully(buf);
                            int len = dis.readInt();

                            entries.add(new EntryImpl(toStr(buf), IcnsType.of(toInt(buf)), len - HEADER_SIZE, streamSupplier, offs + HEADER_SIZE));
                            offs += len;
                        }

                        return false;

                    } else {
                        offs += HEADER_SIZE;
                        entries.add(new EntryImpl(osType, type, size, streamSupplier, offs));
                        offs += size;

                        return true;
                    }
                }
            });
        }

        return new IcnsIconsImpl(entries, null);
    }

    static void parse(InputStream input, Listener handler) throws IOException {
        AtomicLong inputCounter = new AtomicLong();
        DataInputStream dis = new DataInputStream(IoStreams.count(input, inputCounter::addAndGet));

        if (dis.readInt() != MAGIC) {
            throw new IOException("Not an ICNS stream");
        }

        final int fileSize = dis.readInt();
        byte[] buf = new byte[4];

        while (inputCounter.get() < fileSize) {
            dis.readFully(buf);
            IcnsType icnsType = IcnsType.of(toInt(buf));

            int iconSize = dis.readInt() - HEADER_SIZE;
            if (iconSize < 0) {
                throw new IOException(MessageFormat.format("Illegal icon size ({0})", iconSize));
            }
            InputStream iconStream = IoStreams.closeProtect(IoStreams.limit(dis, iconSize));

            if (!handler.onIcon(toStr(buf), icnsType, iconSize, iconStream)) {
                break;
            }

            IoStreams.skip(iconStream, iconSize);
        }
    }

    static int toInt(String typeStr) {
        return toInt(typeStr.getBytes(StandardCharsets.US_ASCII));
    }

    private static int toInt(byte[] typeBytes) {
        if (typeBytes.length != 4) {
            throw new IllegalArgumentException("OSType must consist of exactly four characters");
        }

        return (typeBytes[0] << 24) | (typeBytes[1] << 16) | (typeBytes[2] << 8) | typeBytes[3];
    }

    private static String toStr(byte[] typeBytes) {
        return new String(typeBytes, StandardCharsets.US_ASCII);
    }
}
