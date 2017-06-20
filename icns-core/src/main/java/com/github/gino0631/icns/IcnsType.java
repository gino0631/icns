package com.github.gino0631.icns;

/**
 * ICNS types.
 * <p>
 * The code is based on {@code org.apache.tika.parser.image.ICNSType}.
 */
public enum IcnsType {
    ICNS_32x32_1BIT_IMAGE("ICON", 32, 32, 1, false, false),
    ICNS_16x12_1BIT_IMAGE_AND_MASK("icm#", 16, 12, 1, true, false),
    ICNS_16x12_4BIT_IMAGE("icm4", 16, 12, 4, false, false),
    ICNS_16x12_8BIT_IMAGE("icm8", 16, 12, 8, false, false),

    ICNS_16x16_8BIT_MASK("s8mk", 16, 16, 8, true, false),
    ICNS_16x16_1BIT_IMAGE_AND_MASK("ics#", 16, 16, 1, true, false),
    ICNS_16x16_4BIT_IMAGE("ics4", 16, 16, 4, false, false),
    ICNS_16x16_8BIT_IMAGE("ics8", 16, 16, 8, false, false),
    ICNS_16x16_24BIT_IMAGE("is32", 16, 16, 24, false, false),

    ICNS_32x32_8BIT_MASK("l8mk", 32, 32, 8, true, false),
    ICNS_32x32_1BIT_IMAGE_AND_MASK("ICN#", 32, 32, 1, true, false),
    ICNS_32x32_4BIT_IMAGE("icl4", 32, 32, 4, false, false),
    ICNS_32x32_8BIT_IMAGE("icl8", 32, 32, 8, false, false),
    ICNS_32x32_24BIT_IMAGE("il32", 32, 32, 24, false, false),

    ICNS_48x48_8BIT_MASK("h8mk", 48, 48, 8, true, false),
    ICNS_48x48_1BIT_IMAGE_AND_MASK("ich#", 48, 48, 1, true, false),
    ICNS_48x48_4BIT_IMAGE("ich4", 48, 48, 4, false, false),
    ICNS_48x48_8BIT_IMAGE("ich8", 48, 48, 8, false, false),
    ICNS_48x48_24BIT_IMAGE("ih32", 48, 48, 24, false, false),
    ICNS_128x128_8BIT_MASK("t8mk", 128, 128, 8, true, false),
    ICNS_128x128_24BIT_IMAGE("it32", 128, 128, 24, false, false),

    ICNS_16x16_JPEG_PNG_IMAGE("icp4", 16, 16, 0, false, false),
    ICNS_32x32_JPEG_PNG_IMAGE("icp5", 32, 32, 0, false, false),
    ICNS_64x64_JPEG_PNG_IMAGE("icp6", 64, 64, 0, false, false),
    ICNS_128x128_JPEG_PNG_IMAGE("ic07", 128, 128, 0, false, false),
    ICNS_256x256_JPEG_PNG_IMAGE("ic08", 256, 256, 0, false, false),
    ICNS_512x512_JPEG_PNG_IMAGE("ic09", 512, 512, 0, false, false),
    ICNS_1024x1024_2X_JPEG_PNG_IMAGE("ic10", 1024, 1024, 0, false, true),
    ICNS_16x16_2X_JPEG_PNG_IMAGE("ic11", 32, 32, 0, false, true),
    ICNS_32x32_2X_JPEG_PNG_IMAGE("ic12", 64, 64, 0, false, true),
    ICNS_128x128_2X_JPEG_PNG_IMAGE("ic13", 256, 256, 0, false, true),
    ICNS_256x256_2X_JPEG_PNG_IMAGE("ic14", 512, 512, 0, false, true);

    private final String osType;
    private final int type;
    private final int width;
    private final int height;
    private final int bitsPerPixel;
    private final boolean hasMask;
    private final boolean hasRetinaDisplay;

    IcnsType(String osType, int width, int height, int bitsPerPixel, boolean hasMask, boolean hasRetinaDisplay) {
        this.osType = osType;
        this.type = IcnsIconsImpl.toInt(osType);
        this.width = width;
        this.height = height;
        this.bitsPerPixel = bitsPerPixel;
        this.hasMask = hasMask;
        this.hasRetinaDisplay = hasRetinaDisplay;
    }

    public String getOsType() {
        return osType;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getBitsPerPixel() {
        return bitsPerPixel;
    }

    public boolean hasMask() {
        return hasMask;
    }

    public boolean hasRetinaDisplay() {
        return hasRetinaDisplay;
    }

    public static IcnsType of(String type) {
        return of(IcnsIconsImpl.toInt(type));
    }

    public static IcnsType of(int type) {
        for (IcnsType i : IcnsType.values()) {
            if (i.type == type) {
                return i;
            }
        }

        return null;
    }
}
