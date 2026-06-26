package com.course.apimanagement.gamma.controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.course.apimanagement.gamma.service.QrCodeService;
import com.course.apimanagement.gamma.service.QrCodeService.ImageFormat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Barcode / QR Code Generator", description = "Generate QR code")
@RestController
public class QrCodeRestController {

	@Autowired
	private QrCodeService qrCodeGenerator;

	private static final Logger log = LoggerFactory.getLogger(QrCodeRestController.class);
	public static final int ALLOWABLE_SIZE_MIN = 16;
	public static final int ALLOWABLE_SIZE_MAX = 1024;

	@SuppressWarnings("rawtypes")
	@Operation(summary = "Generate QR Code with certain text, size, & image format (PNG / JPG). "
			+ "For field validations, see detailed description of each field.")
	@ApiResponses(value = { @ApiResponse(description = "QR Code generated as image", responseCode = "200"),
			@ApiResponse(description = "Something wrong on server", responseCode = "500"),
			@ApiResponse(description = "Bad query params", responseCode = "400") })
	@GetMapping(value = { "/v1/create-qr-code", "/v1/create-qr-code/" })
	public ResponseEntity generateQrCode(
			@Parameter(allowEmptyValue = false, description = "Text to be generated") @RequestParam(value = "data", required = true) String data,
			@Parameter(allowEmptyValue = true, description = "Size (width = height), range[" + ALLOWABLE_SIZE_MIN + ","
					+ ALLOWABLE_SIZE_MAX
					+ "]. Default value is 250") @RequestParam(value = "size", defaultValue = "250", required = false) int size,
			@Parameter(allowEmptyValue = true, description = "PNG (default), JPG, JPEG") @RequestParam(value = "format", defaultValue = "PNG", required = false) String imageType) {
		byte[] dataBytes = null;
		HttpHeaders headers = new HttpHeaders();
		ImageFormat imageFormat = null;

		if (size < ALLOWABLE_SIZE_MIN || size > ALLOWABLE_SIZE_MAX) {
			// bad requests
			headers.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
			return new ResponseEntity<String>("Invalid size : " + size, headers, HttpStatus.BAD_REQUEST);
		}

		if (StringUtils.isEmpty(imageType) || imageType.equalsIgnoreCase(ImageFormat.PNG.toString())) {
			imageFormat = ImageFormat.PNG;
		} else if (imageType.equalsIgnoreCase(ImageFormat.JPG.toString())
				|| imageType.equalsIgnoreCase(ImageFormat.JPEG.toString())) {
			imageFormat = ImageFormat.JPEG;
		} else {
			headers.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
			return new ResponseEntity<>(dataBytes, headers, HttpStatus.BAD_REQUEST);
		}

		try {
			dataBytes = qrCodeGenerator.getQrCode(data, size, imageFormat);

			headers.add(HttpHeaders.CONTENT_TYPE,
					(imageFormat.equals(ImageFormat.PNG) ? MediaType.IMAGE_PNG_VALUE : MediaType.IMAGE_JPEG_VALUE));
			headers.add(HttpHeaders.CACHE_CONTROL, "public,max-age=" + (60 * 60 * 24));

			return new ResponseEntity<>(dataBytes, headers, HttpStatus.OK);
		} catch (IOException e) {
			log.error(e.getMessage());
			headers.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
			return new ResponseEntity<String>(e.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
