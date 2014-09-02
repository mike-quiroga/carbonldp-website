package com.base22.carbon.authentication;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.springframework.security.crypto.codec.Hex;

public abstract class AuthenticationUtil {

	private static final String REGEX_UUID = "[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}";
	private static Pattern uuidPattern = null;

	public static String generateRandomSalt() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}

	public static String saltPassword(String password, String salt) {
		String saltedPassword = null;

		saltedPassword = password.concat(salt);

		return saltedPassword;
	}

	public static String hashPassword(String saltedPassword) throws NoSuchAlgorithmException {
		String hashedPassword = null;

		MessageDigest digest = null;
		digest = MessageDigest.getInstance("SHA-256");

		digest.update(saltedPassword.getBytes());

		hashedPassword = new String(Hex.encode(digest.digest()));

		return hashedPassword;
	}

	public static String minimizeUUID(UUID uuid) {
		String uuidString = uuid.toString();
		uuidString = uuidString.replace("-", "");
		return uuidString;
	}

	public static String minimizeUUID(String uuidString) {
		uuidString = uuidString.replace("-", "");
		return uuidString;
	}

	public static String maximizeUUID(String uuidString) {
		if ( uuidString.length() == 32 ) {
			StringBuilder uuidStringBuilder = new StringBuilder(uuidString);
			uuidStringBuilder.insert(8, "-");
			uuidStringBuilder.insert(13, "-");
			uuidStringBuilder.insert(18, "-");
			uuidStringBuilder.insert(23, "-");
			uuidString = uuidStringBuilder.toString();
		}
		return uuidString;
	}

	public static UUID restoreUUID(String uuidString) {
		uuidString = maximizeUUID(uuidString);
		return UUID.fromString(uuidString);
	}

	public static boolean isUUIDString(String stringToTest) {
		boolean itIs = false;

		stringToTest = maximizeUUID(stringToTest);

		if ( stringToTest.length() == 36 ) {
			Matcher uuidMatcher = getUuidPattern().matcher(stringToTest);
			itIs = uuidMatcher.find();
		}

		return itIs;
	}

	public static Pattern getUuidPattern() {
		if ( uuidPattern == null ) {
			uuidPattern = Pattern.compile(REGEX_UUID, Pattern.CASE_INSENSITIVE);
		}
		return uuidPattern;
	}

	public static String buildExtendedInformation(UUID agentUUID, DateTime now) {
		StringBuilder extendedInformationBuilder = new StringBuilder();
		//@formatter:off
		extendedInformationBuilder
			.append("Agent: ")
			.append(agentUUID.toString())
			.append(", Created: ")
			.append(now.toString())
		;
		//@formatter:on
		return extendedInformationBuilder.toString();
	}

	public static UUID getUUIDFromExtendedInformation(String extendedInformation) {
		int createdIndex = extendedInformation.indexOf(", Created: ");
		if ( createdIndex == - 1 ) {
			return null;
		}

		String agentSubstring = extendedInformation.substring(0, createdIndex - 1);
		if ( ! agentSubstring.startsWith("Agent: ") ) {
			return null;
		}

		String uuidString = agentSubstring.replace("Agent: ", "");
		return restoreUUID(uuidString);
	}

	public static DateTime getCreationTimeFromExtendedInformation(String extendedInformation) {
		int createdIndex = extendedInformation.indexOf(", Created: ");
		if ( createdIndex == - 1 ) {
			return null;
		}

		String createdInstanceSubstring = extendedInformation.substring(createdIndex);
		if ( ! createdInstanceSubstring.startsWith(", Created: ") ) {
			return null;
		}

		String dateTimeString = createdInstanceSubstring.replace(", Created: ", "");
		return DateTime.parse(dateTimeString);
	}
}
