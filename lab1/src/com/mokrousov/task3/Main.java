package com.mokrousov.task3;

import java.util.*;

public class Main {
  public static int findKeyLengthText(String text) {
    for (int len = 1; len <= Math.min(text.length(), 25); len++) {
      double averageIndex = 0;
      for (int sequence = 1; sequence <= len; sequence++) {
        int[] distribution = new int[26];
        double indexOfCoincidence = 0;
        int count = 0;
        
        for (int k = sequence - 1; k < text.length(); k += len) {
          distribution[text.charAt(k) - 'a']++;
          count++;
        }
        
        for (double frequency : distribution) {
          indexOfCoincidence += frequency * (frequency - 1);
        }
        
        indexOfCoincidence /= (count * (count - 1));
        averageIndex += indexOfCoincidence;
      }
      
      averageIndex /= len;
      if (averageIndex >= 0.06) {
        return len;
      }
    }
    
    return -1;
  }
  
  public static int findKeyLength(byte[] s) {
    for (int len = 1; len <= 25; len++) {
      double averageIndex = 0;
      
      for (int sequence = 1; sequence <= len; sequence++) {
        int[] distribution = new int[128];
        int count = 0;
        double indexOfCoincidence = 0;
        
        for (int k = sequence - 1; k < s.length; k += len) {
          if (s[k] < 0) continue; // skip invalid chars
          distribution[s[k]]++;
          count++;
        }
        
        for (double freq : distribution) {
          indexOfCoincidence += freq * (freq - 1);
        }
        
        indexOfCoincidence /= count * (count - 1);
        averageIndex += indexOfCoincidence;
      }
      
      averageIndex /= len;
      
      if (averageIndex >= 0.057) {
        System.out.println("Key length: " + len);
        return len;
      }
    }
    
    return -1;
  }
  
  public static String findKey(byte[] s, int keyLen) {
    StringBuilder keyBuilder = new StringBuilder();
    for (int i = 0; i < keyLen; i++) {
      Map<Byte, Integer> freqs = new HashMap<>();
      for (int j = i; j < s.length; j += keyLen) {
        byte key = s[j];
        if (freqs.containsKey(key)) {
          freqs.put(key, freqs.get(key) + 1);
        } else {
          freqs.put(key, 1);
        }
      }
  
      List<Byte> bytes = new ArrayList<>(freqs.keySet());
      bytes.sort(Comparator.comparingInt(freqs::get).reversed());
      keyBuilder.append((char) (bytes.get(0) ^ ((int) ' ')));
    }
    
    System.out.println("Key: " + keyBuilder);
    return keyBuilder.toString();
  }
  
  public static String decode(byte[] s, String key) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < s.length; i++) {
      int c = s[i];
      int x = key.charAt(i % key.length());
      result.append((char) (c ^ x));
    }
    return result.toString();
  }
  
  public static String decrypt(byte[] s) {
    int keyLen = findKeyLength(s);
    String key = findKey(s, keyLen);
    return decode(s, key);
  }
  
  public static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
        + Character.digit(s.charAt(i + 1), 16));
    }
    return data;
  }
  
  public static void main(String[] args) {
    String input = "1c41023f564b2a130824570e6b47046b521f3f5208201318245e0e6b40022643072e13183e51183f5a1f3e4702245d4b285a1b23561965133f2413192e571e28564b3f5b0e6b50042643072e4b023f4a4b24554b3f5b0238130425564b3c564b3c5a0727131e38564b245d0732131e3b430e39500a38564b27561f3f5619381f4b385c4b3f5b0e6b580e32401b2a500e6b5a186b5c05274a4b79054a6b67046b540e3f131f235a186b5c052e13192254033f130a3e470426521f22500a275f126b4a043e131c225f076b431924510a295f126b5d0e2e574b3f5c4b3e400e6b400426564b385c193f13042d130c2e5d0e3f5a086b52072c5c192247032613433c5b02285b4b3c5c1920560f6b47032e13092e401f6b5f0a38474b32560a391a476b40022646072a470e2f130a255d0e2a5f0225544b24414b2c410a2f5a0e25474b2f56182856053f1d4b185619225c1e385f1267131c395a1f2e13023f13192254033f13052444476b4a043e131c225f076b5d0e2e574b22474b3f5c4b2f56082243032e414b3f5b0e6b5d0e33474b245d0e6b52186b440e275f456b710e2a414b225d4b265a052f1f4b3f5b0e395689cbaa186b5d046b401b2a500e381d4b23471f3b4051641c0f2450186554042454072e1d08245e442f5c083e5e0e2547442f1c5a0a64123c503e027e040c413428592406521a21420e184a2a32492072000228622e7f64467d512f0e7f0d1a";
    byte[] bytes = hexStringToByteArray(input);
    System.out.println(decrypt(bytes));
  }
}
