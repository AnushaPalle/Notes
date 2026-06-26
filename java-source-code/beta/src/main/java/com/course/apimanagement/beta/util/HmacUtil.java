package com.course.apimanagement.beta.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

public class HmacUtil {

	public enum Output {
		HEX, BASE64
	}

	public String hmacSha256(String message, String secret, HmacUtil.Output output)
			throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
		var algorithm = "HmacSHA256";
		var hasher = Mac.getInstance(algorithm);
		var secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.US_ASCII), algorithm);
		hasher.init(secretKey);

		if (output.equals(Output.HEX)) {
			return Hex.encodeHexString(hasher.doFinal(message.getBytes(StandardCharsets.US_ASCII)));
		} else if (output.equals(Output.BASE64)) {
			return Base64.encodeBase64String(hasher.doFinal(message.getBytes(StandardCharsets.US_ASCII)));
		}

		return StringUtils.EMPTY;
	}

}
