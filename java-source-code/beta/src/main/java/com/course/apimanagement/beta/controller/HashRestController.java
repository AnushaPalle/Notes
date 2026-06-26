package com.course.apimanagement.beta.controller;

import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Beta Hash", description = "Beta API for hashing")
public class HashRestController {

	@PostMapping(path = "/md5", produces = MediaType.TEXT_PLAIN_VALUE)
	@Operation(summary = "Upload a file and calculate MD5 hash.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Calculated MD5") })
	public ResponseEntity<String> md5Hash(
			@Parameter(allowEmptyValue = false, required = true, description = "File to be calculated") @RequestParam(name = "f", required = true) MultipartFile f) {
		try {
			return ResponseEntity.ok(DigestUtils.md5Hex(f.getBytes()));
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something wrong happened in server");
		}
	}

}
