package com.mokrousov.task5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class DecodeUtils {
  private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final int NGRAM_LENGTH = 4;
  private static final Map<String, Integer> NGRAM_FREQUENCIES;
  private static final double SURVIVERS_PERCENT = 0.2;
  private static final double MATING_PERCENT = 0.6;
  private static final double MUTATION_PROBABILITY = 0.6;
  
  static {
    NGRAM_FREQUENCIES = new HashMap<>();
    try {
      String text = Files.readString(Path.of("resources/ngrams.txt"));
      for (String row : text.split("\n")) {
        String[] parts = row.split(" ");
        NGRAM_FREQUENCIES.put(parts[0], Integer.parseInt(parts[1]));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static List<String> getKeysFromEncodings(List<List<Integer>> keys) {
    return keys.stream().map(DecodeUtils::getKeyFromEncoding).collect(Collectors.toList());
  }
  
  public static String getKeyFromEncoding(List<Integer> keyEncoding) {
    StringBuilder sb = new StringBuilder();
    for (int x : keyEncoding) {
      char c = (char) ('A' + x);
      sb.append(c);
    }
    
    return sb.toString();
  }
  
  public static String decodeInput(String input, List<List<Integer>> keyEncodings) {
    List<String> key = getKeysFromEncodings(keyEncodings);
    StringBuilder sb = new StringBuilder();
    int i = 0;
    for (char c : input.toCharArray()) {
      sb.append(key.get(i).charAt(c - 'A'));
      i = (i + 1) % keyEncodings.size();
    }
    return sb.toString();
  }
  
  private static Map<String, Integer> findNgramFrequency(String text) {
    Map<String, Integer> freqs = new HashMap<>();
    for (int i = 0; i < text.length() - NGRAM_LENGTH + 1; i++) {
      String ngram = text.substring(i, i + NGRAM_LENGTH);
      if (freqs.containsKey(ngram)) {
        freqs.put(ngram, freqs.get(ngram) + 1);
      } else {
        freqs.put(ngram, 1);
      }
    }
    
    return freqs;
  }
  
  private static double rankDecoding(String input, List<Integer> monoKeyEncoding, int keyIndex, List<List<List<Integer>>> populations) {
    List<List<Integer>> keys = new ArrayList<>();
    for (var keyPopulation : populations) keys.add(keyPopulation.get(0));
    keys.set(keyIndex, monoKeyEncoding);
    
    String output = decodeInput(input, keys);
    double rank = 0;
    
    Map<String, Integer> frequencyOfNgrams = findNgramFrequency(output);
    
    for (Map.Entry<String, Integer> entry : frequencyOfNgrams.entrySet()) {
      String ngram = entry.getKey();
      int freq = entry.getValue();
      if (NGRAM_FREQUENCIES.containsKey(ngram)) {
        rank += freq * (Math.log(NGRAM_FREQUENCIES.get(ngram)) / Math.log(2));
      }
    }
    
    return rank;
  }
  
  public static List<Integer> createShuffledList(int length) {
    List<Integer> list = new ArrayList<>();
    for (int i = 0; i < length; i++) {
      list.add(i);
    }
    Collections.shuffle(list);
    return list;
  }
  
  private static void sortKeys(List<List<Integer>> keys, String text, int keyIndex, List<List<List<Integer>>> population) {
    keys.sort(Comparator.comparingDouble(keyEncoding -> rankDecoding(text, (List<Integer>) keyEncoding, keyIndex, population)).reversed());
  }
  
  public static List<List<Integer>> limitKeysSize(List<List<Integer>> keys, int maxSize) {
    List<List<Integer>> newKeys = new ArrayList<>();
    for (int i = 0; i < maxSize; i++)
      newKeys.add(keys.get(i));
    return newKeys;
  }
  
  private static List<Integer> selectRandom(int n, int len) {
    List<Integer> list = createShuffledList(len);
    List<Integer> newList = new ArrayList<>();
    for (int i = 0; i < n; i++)
      newList.add(list.get(i));
    return newList;
  }
  
  private static List<Integer> selectDiff(List<Integer> selection) {
    List<Integer> list = createShuffledList(26);
    List<Integer> newList = new ArrayList<>();
    for (int i = 0; i < list.size(); i++) {
      int a = list.get(i);
      if (!selection.contains(a)) {
        newList.add(a);
      }
    }
    
    return newList;
  }
  
  private static List<Integer> combineGenes(List<Integer> key1, List<Integer> key2, List<Integer> genesSample) {
    List<Integer> result = new ArrayList<>(key1), cp = new ArrayList<>(key2);
    for (int elem : genesSample) cp.remove((Integer) elem);
    int j = 0;
    for (int i = 0; i < result.size(); i++) {
      if (genesSample.contains(result.get(i))) continue;
      result.set(i, cp.get(j++));
    }
    return result;
  }
  
  private static List<List<Integer>> generateCombinations(List<Integer> key1, List<Integer> key2) {
    List<Integer> key1Genes = selectRandom(ThreadLocalRandom.current().nextInt(1, 26), 26);
    List<Integer> key2Genes = selectDiff(key1Genes);
    return Arrays.asList(combineGenes(key1, key2, key1Genes), combineGenes(key1, key2, key2Genes));
  }
  
  private static List<Integer> generateMutation(List<Integer> combination) {
    List<Integer> cp = new ArrayList<>(combination);
    if (Math.random() < MUTATION_PROBABILITY) {
      List<Integer> rand = selectRandom(2, combination.size());
      int i = rand.get(0), j = rand.get(1);
      int x = cp.get(i);
      cp.set(i, combination.get(j));
      cp.set(j, x);
    }
    return cp;
  }
  
  private static List<List<Integer>> generateMutations(List<Integer> key1, List<Integer> key2) {
    List<List<Integer>> combinations = generateCombinations(key1, key2);
    List<List<Integer>> mutations = new ArrayList<>();
    for (List<Integer> comb : combinations) {
      mutations.add(generateMutation(comb));
    }
    return mutations;
  }
  
  public static List<List<Integer>> findMonoAlphabetKey(String text, int iterations, int populationSize, int keyIndex, List<List<List<Integer>>> population) {
    List<List<Integer>> possibleKeys = new ArrayList<>();
    int survivers = (int) (populationSize * SURVIVERS_PERCENT);
    int maters = (int) (populationSize * MATING_PERCENT);
    
    for (int i = 0; i < populationSize; i++) {
      possibleKeys.add(population.get(keyIndex).get(i));
    }
    
    for (int i = 0; i < iterations; i++) {
      System.out.println(i);
      sortKeys(possibleKeys, text, keyIndex, population);
      possibleKeys = limitKeysSize(possibleKeys, populationSize);
      List<List<Integer>> nextKeys = limitKeysSize(possibleKeys, survivers);
      while (nextKeys.size() < populationSize) {
        List<Integer> matingPair = selectRandom(2, maters);
        List<List<Integer>> nextGenerationMutations = generateMutations(
          possibleKeys.get(matingPair.get(0)), possibleKeys.get(matingPair.get(1)));
        nextKeys.addAll(nextGenerationMutations);
      }
      possibleKeys = nextKeys;
    }
    
    sortKeys(possibleKeys, text, keyIndex, population);
    return possibleKeys;
  }
}
