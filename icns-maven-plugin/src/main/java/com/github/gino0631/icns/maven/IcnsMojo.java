package com.github.gino0631.icns.maven;

import com.github.gino0631.icns.IcnsBuilder;
import com.github.gino0631.icns.IcnsIcons;
import com.github.gino0631.icns.IcnsType;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Mojo(name = "icns", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class IcnsMojo extends AbstractMojo {
    /**
     * Output base directory.
     */
    @Parameter(defaultValue = "${project.build.directory}")
    private File buildDirectory;

    /**
     * Output file.
     */
    @Parameter(required = true)
    private String outputFile;

    /**
     * Icon base directory.
     */
    @Parameter(defaultValue = "${basedir}/src/main/resources")
    private File resourceDirectory;

    /**
     * Icons.
     */
    @Parameter(required = true)
    private List<Icon> icons;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Objects.requireNonNull(outputFile);

        try {
            try (IcnsBuilder builder = IcnsBuilder.getInstance()) {
                for (Icon icon : icons) {
                    try (InputStream is = getInputStream(icon)) {
                        builder.add(getOsType(icon), is);
                    }
                }

                try (IcnsIcons icons = builder.build()) {
                    try (OutputStream os = getOutputStream()) {
                        icons.writeTo(os);
                    }
                }
            }

        } catch (IOException e) {
            throw new MojoExecutionException("Error building ICNS file", e);
        }
    }

    private String getOsType(Icon icon) throws MojoExecutionException {
        String osType = icon.getOsType();

        if (osType != null) {
            return osType;

        } else {
            String name = Paths.get(icon.getFile()).getFileName().toString();

            try {
                try (InputStream is = getInputStream(icon)) {
                    try (ImageInputStream iis = ImageIO.createImageInputStream(is)) {
                        Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);

                        if (imageReaders.hasNext()) {
                            ImageReader reader = imageReaders.next();

                            try {
                                String format = reader.getFormatName();
                                if (format.equals("png") || format.equals("jpeg")) {
                                    reader.setInput(iis, true, true);
                                    BufferedImage img = reader.read(0, reader.getDefaultReadParam());

                                    if (img.getWidth() == img.getHeight()) {
                                        switch (img.getWidth()) {
                                            case 16:
                                                return IcnsType.ICNS_16x16_JPEG_PNG_IMAGE.getOsType();

                                            case 32:
                                                return IcnsType.ICNS_32x32_JPEG_PNG_IMAGE.getOsType();

                                            case 64:
                                                return IcnsType.ICNS_64x64_JPEG_PNG_IMAGE.getOsType();

                                            case 128:
                                                return IcnsType.ICNS_128x128_JPEG_PNG_IMAGE.getOsType();

                                            case 256:
                                                return IcnsType.ICNS_256x256_JPEG_PNG_IMAGE.getOsType();

                                            case 512:
                                                return IcnsType.ICNS_512x512_JPEG_PNG_IMAGE.getOsType();

                                            case 1024:
                                                return IcnsType.ICNS_1024x1024_2X_JPEG_PNG_IMAGE.getOsType();
                                        }
                                    }

                                    throw new MojoExecutionException(MessageFormat.format("Size of {0} icon is not supported", name));
                                }

                            } finally {
                                reader.dispose();
                            }
                        }

                        throw new MojoExecutionException(MessageFormat.format("Unable to determine type of {0} icon", name));
                    }
                }

            } catch (IOException e) {
                throw new MojoExecutionException("Error determining icon type", e);
            }
        }
    }

    private InputStream getInputStream(Icon icon) throws IOException {
        Path path = Paths.get(icon.getFile());

        if (resourceDirectory != null) {
            path = resourceDirectory.toPath().resolve(path);
        }

        return Files.newInputStream(path);
    }

    private OutputStream getOutputStream() throws IOException {
        Path path = Paths.get(outputFile);

        if (path.getFileName().toString().indexOf('.') < 0) {
            path = Paths.get(path.toString() + ".icns");
        }

        if (buildDirectory != null) {
            path = buildDirectory.toPath().resolve(path);
        }

        Files.createDirectories(path.getParent());

        return Files.newOutputStream(path);
    }
}
