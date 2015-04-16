package model;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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

  public String generate(File file) {
    try (FileInputStream inputStream = new FileInputStream(file)) {
      MessageDigest messageDigest = MessageDigest.getInstance(this.getName());

      FileChannel channel = inputStream.getChannel();
      int bufferSize = (int)Math.min(file.length(), 4 * 1024 * 1024);
      ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
      while (channel.read(buffer) != -1) {
        buffer.flip();
        messageDigest.update(buffer);
        buffer.clear();
      }

      return toHexadecimalFormat(messageDigest.digest());
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  private static String toHexadecimalFormat(byte[] bytes) {
    return DatatypeConverter.printHexBinary(bytes);
  }
}
