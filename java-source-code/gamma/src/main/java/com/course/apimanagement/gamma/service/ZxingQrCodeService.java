package com.course.apimanagement.gamma.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;

@Service
public class ZxingQrCodeService implements QrCodeService {

	@Override
	public byte[] getQrCode(String text, int size, ImageFormat imageFormat) throws IOException {
		try {
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			var bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size);

			var imageOutputStream = new ByteArrayOutputStream();
			var conf = new MatrixToImageConfig(-18283486, -1);
			ImageIO.write(MatrixToImageWriter.toBufferedImage(bitMatrix, conf), imageFormat.name(), imageOutputStream);

			return imageOutputStream.toByteArray();
		} catch (WriterException e) {
			throw new IOException(e.getMessage());
		}
	}

}
