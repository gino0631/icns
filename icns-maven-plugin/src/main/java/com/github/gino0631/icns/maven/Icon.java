package com.github.gino0631.icns.maven;

import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

public class Icon {
    /**
     * Icon file.
     */
    @Parameter(required = true)
    private File file;

    /**
     * OSType identifier of the icon type.
     */
    @Parameter
    private String osType;

    public Icon set(String file) {
        this.file = new File(file);

        return this;
    }

    public File getFile() {
        return file;
    }

    public String getOsType() {
        return osType;
    }
}
