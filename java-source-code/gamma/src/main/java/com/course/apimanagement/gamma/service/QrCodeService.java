package com.course.apimanagement.gamma.service;

import java.io.IOException;

public interface QrCodeService {

    public enum ImageFormat {
        PNG, JPG, JPEG
    }

    byte[] getQrCode(String text, int size, ImageFormat imageFormat) throws IOException;

}
