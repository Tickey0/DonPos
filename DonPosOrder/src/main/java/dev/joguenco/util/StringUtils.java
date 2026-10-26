package dev.joguenco.util;

public class StringUtils {
  private static final char[] hexchars = {
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
  };

  public static String byte2hex(byte[] binput) {

    StringBuilder sb = new StringBuilder(binput.length * 2);
    for (int i = 0; i < binput.length; i++) {
      int high = ((binput[i] & 0xF0) >> 4);
      int low = (binput[i] & 0x0F);
      sb.append(hexchars[high]);
      sb.append(hexchars[low]);
    }
    return sb.toString();
  }
}
