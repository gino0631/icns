package com.github.gino0631.icns.maven;

import org.apache.maven.plugins.annotations.Parameter;

public class Icon {
    /**
     * Icon file.
     */
    @Parameter(required = true)
    private String file;

    /**
     * OSType identifier of the icon type.
     */
    @Parameter
    private String osType;

    public Icon set(String file) {
        this.file = file;

        return this;
    }

    public String getFile() {
        return file;
    }

    public String getOsType() {
        return osType;
    }
}
