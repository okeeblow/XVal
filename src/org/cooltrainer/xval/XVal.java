package org.cooltrainer.xval;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * The XVal class decrypts the 'X' value displayed on the "System Info" 
 * screen of the Xbox 360 dashboard. This encrypted string gives
 * information about the security event history of the console.
 * 
 * @author Nicole Reid <root@cooltrainer.org>
 * @since 2012-06-23
 */
public class XVal {
	private static int FLAG_SSB_AUTH_EX_FAILURE = 0x0001;
	private static int FLAG_SSB_AUTH_EX_NO_TABLE = 0x0002;
	private static int FLAG_SSB_AUTH_EX_RESERVED = 0x0004;
	private static int FLAG_SSB_INVALID_DVD_GEOMETRY = 0x0008;
	private static int FLAG_SSB_INVALID_DVD_DMI = 0x0010;
	private static int FLAG_SSB_DVD_KEYVAULT_PAIR_MISMATCH = 0x0020;
	private static int FLAG_SSB_CRL_DATA_INVALID = 0x0040;
	private static int FLAG_SSB_CRL_CERTIFICATE_REVOKED = 0x0080;
	private static int FLAG_SSB_UNAUTHORIZED_INSTALL = 0x0100;
	private static int FLAG_SSB_KEYVAULT_POLICY_VIOLATION = 0x0200;
	private static int FLAG_SSB_CONSOLE_BANNED = 0x0400;
	private static int FLAG_SSB_ODD_VIOLATION = 0x0800;
	
	private static String ssb = "XBOX360SSB"; //Magic string
	private byte[] decryptedXVal;
	private int[] splitXVal;
	
	/**
	 * 
	 * @param serial			An Xbox 360 serial number, 12 numeric digits.
	 * @param xval				An 'X' value from the same console, 16 hexadecimal digits
	 * @throws XValException	On decryption error
	 */
	public XVal(String serial, String xval) throws XValException {
		this.decryptedXVal = DecryptXVal(serial, xval);
		this.splitXVal = splitXVal(this.decryptedXVal);
	}
	
	/**
	 * Validates a serial number / X value pair. A non-matching serial
	 * and X value won't throw an exception in {@link #DecryptXVal(String, byte[])}
	 * but will return a junk decrypted XVal.
	 * 
	 * @return		true if instantiated serial + xval match, false if not
	 */
	public boolean isValidPair() {
		return !(this.splitXVal[0] != 0 && this.splitXVal[1] != 0);
	}
	
	/**
	 * Gives quick pass or fail status of a successfully decrypted X value. {@link #isValidPair()}
	 * should be checked first for sanity.
	 * 
	 * Use {@link #flags()} to get flags if this returns false.
	 * 
	 * @return		True if this X value is clean, false if it is flagged
	 */
	public boolean isClean() {
		return (this.splitXVal[0] == 0 && this.splitXVal[1] == 0);
	}
	
	/**
	 * Displays this instance's secdata.bin flags in human-readable form. 
	 * 
	 * @return		secdata.bin flags recorded in the instantiated X value
	 */
	public String flags() {
		return flags(this.splitXVal);
	}
	
	/**
	 * Prints the decrypted 'X' Value for an instance of this class.
	 */
	public String toString() {
		return bytesToHex(this.decryptedXVal);
	}
    
	/**
	 * Converts a byte array to a hexadecimal string.
	 * 
	 * @param in		A one-dimensional byte array
	 * @return			A hexadecimal String object representing the contents of the input
	 */
	private static String bytesToHex(byte[] in) {
		StringBuilder sb = new StringBuilder(in.length * 2);
		for (int i = 0; i < in.length; i++) {
			int val = in[i] & 0xff;
			sb.append(String.format("%#04x ", val));
		}
		return sb.toString();
	}

	/**
	 * Converts a hexadecimal String to a byte array
	 * 
	 * @param in		A hexadecimal String, "^[0-9A-F]$"
	 * @return			A byte array representing the contents of the input
	 */
	private static byte[] hexToBytes(String in) {
		int len = in.length();
		byte[] bytes = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			bytes[i / 2] = (byte) ((Character.digit(in.charAt(i), 16) << 4) + Character.digit(in.charAt(i + 1), 16));
		}
		return bytes;
	}

	/**
	 * Converts a section of a byte array to an integer
	 * 
	 * @param b			An input byte array
	 * @param offset	The start index (offset % 4) to return
	 * @return			An integer representation of the four bytes in b following offset
	 */
	private static int bytesToInt(byte[] b, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i + offset] & 0x000000FF) << shift;
		}
		return value;
	}

	/**
	 * Decrypts a serial number / 'X' value pair
	 * 
	 * @param serial			Console serial number
	 * @param encryptedXval		Encrypted 'X' value
	 * @return					A byte array representation of the decrypted 'X' value
	 * @throws XValException
	 */
	public static byte[] DecryptXVal(String serial, String xval) throws XValException {
		return DecryptXVal(serial, hexToBytes(xval.replace("-", "").toUpperCase()));
	}
	
	/**
	 * Decrypts a serial number / 'X' value pair
	 * 
	 * @param serial			Console serial number
	 * @param encryptedXval		Encrypted 'X' value
	 * @return					A byte array representation of the decrypted 'X' value
	 * @throws XValException
	 */
	public static byte[] DecryptXVal(String serial, byte[] encryptedXval) throws XValException {
		byte[] decryptedXval = null;
		serial += "\0"; // Zero-pad serial

		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec secret = new SecretKeySpec(serial.getBytes(),	"HmacSHA1");
			mac.init(secret);
			byte[] digest = mac.doFinal(ssb.getBytes());

			DESKeySpec desKeySpec = new DESKeySpec(digest);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

			Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			decryptedXval = cipher.doFinal(encryptedXval);
		} catch (NoSuchAlgorithmException e) {
			/* Thrown by Mac, SecretKeyFactory, and Cipher if "HmacSHA1", "DES", and "DES" (respectively)
			   aren't available. This shouldn't happen. */
			throw new XValException("A needed decryption algorithm isn't available in this JVM", e);
		} catch (InvalidKeyException e) {
			/* Thrown by MAC.init if initialized with invalid key,
			 * by DESKeySpec if instantiated with invalid digest,
			 * or by Cipher.init if initialized with invalid secretKey
			 */
			throw new XValException(e);
		} catch (InvalidKeySpecException e) {
			/* Thrown by SecretKeyFactory.generateKeySpec if used with invalid DES keySpec */
			throw new XValException(e);
		} catch (javax.crypto.NoSuchPaddingException e) {
			/* Thrown by Cipher.getInstance if instantiated with an unavailable padding
			 * ("NoPadding" in our case). This shouldn't happen.
			 */
			throw new XValException("NoPadding for javax.crypto.Cipher is unavailable", e);
		} catch (javax.crypto.BadPaddingException e) {
			/* Thrown by Cipher.doFinal. Somewhat confusingly, this usually means we're trying to decrypt
			 * with an incorrect key for our data or are using an incorrect Algo, Mode, or Padding.
			 */
			throw new XValException("Attempting to decrypt with incorrect key", e);
		} catch (IllegalBlockSizeException e) {
			/* Thrown by Cipher.doFinal when its input isn't a multiple of eight bytes */
			throw new XValException("XVal not a multiple of eight bytes", e);
		}

		return decryptedXval;
	}
	
	/**
	 * Splits a decrypted X value byte array into two integers
	 * 
	 * Index zero is the 'low' X value, and index one is the 'high' X value
	 * 
	 * @param decryptedXVal		Byte array of decrypted X value
	 * @return					Integer array of decrypted X value
	 */
	public static int[] splitXVal(byte[] decryptedXVal) {
		int[] xval = new int[2];
		xval[1] = bytesToInt(decryptedXVal, 0); //highXVal
		xval[0] = bytesToInt(decryptedXVal, 4); //lowXVal
		return xval;
	}
	
	/**
	 * Displays secdata.bin flags in human-readable form.
	 * 
	 * @param decryptedXval		Decrypted XVal byte array
	 * @return					String of flags in X value, or 'clean' otherwise
	 */
	public static String flags(byte[] decryptedXVal) {
		return flags(splitXVal(decryptedXVal));
	}
	
	/**
	 * Displays secdata.bin flags in human-readable form.
	 * 
	 * @param decryptedXval		{lowXval, highXval} integer array
	 * @return					String of flags in X value, or 'clean' otherwise
	 */
	public static String flags(int[] decryptedXval) {
		int highXval = decryptedXval[1];
		int lowXval = decryptedXval[0];
		
		StringBuilder flags = new StringBuilder();
		if(highXval == 0 && lowXval == 0)
			flags.append("Secdata clean");
		else if(highXval == 0xFFFFFFFF && lowXval == 0xFFFFFFFF)
			flags.append("Secdata invalid");
		else if(highXval != 0 && lowXval != 0)
			flags.append("Secdata decryption error");
		else {
			
			if((lowXval & FLAG_SSB_AUTH_EX_FAILURE) != 0)
				flags.append("AuthEx (AP25) Challenge Failure");
			if((lowXval & FLAG_SSB_AUTH_EX_NO_TABLE) != 0)
				flags.append("AuthEx (AP25) Table Missing");
			if((lowXval & FLAG_SSB_AUTH_EX_RESERVED) != 0)
				flags.append("AuthEx (AP25) Reserved Flag");
			if((lowXval & FLAG_SSB_INVALID_DVD_GEOMETRY) != 0)
				flags.append("Invalid DVD Geometry");
			if((lowXval & FLAG_SSB_INVALID_DVD_DMI) != 0)
				flags.append("Invalid DVD DMI");
			if((lowXval & FLAG_SSB_DVD_KEYVAULT_PAIR_MISMATCH) != 0)
				flags.append("DVD Keyvault Pair Mismatch");
			if((lowXval & FLAG_SSB_CRL_DATA_INVALID) != 0)
				flags.append("Invalid CRL Data");
			if((lowXval & FLAG_SSB_CRL_CERTIFICATE_REVOKED) != 0)
				flags.append("CRL Certificate Revoked");
			if((lowXval & FLAG_SSB_UNAUTHORIZED_INSTALL) != 0)
				flags.append("Unauthorized Install");
			if((lowXval & FLAG_SSB_KEYVAULT_POLICY_VIOLATION) != 0)
				flags.append("KeyVault Policy Violation");
			if((lowXval & FLAG_SSB_CONSOLE_BANNED) != 0)
				flags.append("Console Banned");
			if((lowXval & FLAG_SSB_ODD_VIOLATION) != 0)
				flags.append("ODD Violation");
			if((lowXval & 0xFFFFF000) != 0)
				flags.append("Unknown Violation(s)");
		}
		
		return flags.toString();
	}
}
