package model;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

import javax.xml.bind.DatatypeConverter;

public enum ChecksumFunction {
  MD5("MD5"),
  SHA1("SHA-1"),
  SHA256("SHA-256"),
  SHA512("SHA-512");

  private String name;

  ChecksumFunction(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public String generate(File input) {
    try (FileInputStream in = new FileInputStream(input)) {
      MessageDigest messageDigest = MessageDigest.getInstance(this.getName());
      byte[] block = new byte[2048];
      int length;
      while ((length = in.read(block)) > 0) {
        messageDigest.update(block, 0, length);
      }

      return toHexadecimalFormat(messageDigest.digest());
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private static String toHexadecimalFormat(byte[] bytes) {
    return DatatypeConverter.printHexBinary(bytes);
  }
}
