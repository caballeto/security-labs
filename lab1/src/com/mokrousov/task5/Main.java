package com.mokrousov.task5;

import java.util.ArrayList;
import java.util.List;

public class Main {
  private static final double SURVIVERS_PERCENT = 0.2;
  private static final double MATING_PERCENT = 0.6;
  private static final double MUTATION_PROBABILITY = 0.6;
  
  
  public static int findKeyLength(String s) {
    for (int len = 1; len <= 25; len++) {
      double averageIndex = 0;
      
      for (int sequence = 1; sequence <= len; sequence++) {
        int[] distribution = new int[26];
        int count = 0;
        double indexOfCoincidence = 0;
        
        for (int k = sequence - 1; k < s.length(); k += len) {
          int c = s.charAt(k) - 'A';
          distribution[c]++;
          count++;
        }
        
        for (double freq : distribution) {
          indexOfCoincidence += freq * (freq - 1);
        }
        
        indexOfCoincidence /= count * (count - 1);
        averageIndex += indexOfCoincidence;
      }
      
      averageIndex /= len;
      if (averageIndex >= 0.07) {
        System.out.println("Key length: " + len);
        return len;
      }
    }
    
    return -1;
  }
  
  private static List<List<List<Integer>>> initPopulations(int populationSize, int keyLen) {
    List<List<List<Integer>>> populations = new ArrayList<>();
    for (int i = 0; i < keyLen; i++) {
      List<List<Integer>> population = new ArrayList<>();
      for (int j = 0; j < populationSize; j++) {
        population.add(DecodeUtils.createShuffledList(26));
      }
      populations.add(population);
    }
    return populations;
  }
  
  public static List<List<Integer>> findKey(String text, int iterations, int populationSize, int keyLen) {
    List<List<List<Integer>>> populations = initPopulations(populationSize, keyLen);
    for (int i = 0; i < iterations; i++) {
      System.out.println("Iteration: " + i);
      for (int j = 0; j < keyLen; j++) {
        List<List<Integer>> nextGeneration = DecodeUtils.findMonoAlphabetKey(text, 1, populationSize, j, populations);
        populations.set(j, DecodeUtils.limitKeysSize(nextGeneration, populationSize));
      }
    }
    
    List<List<Integer>> result = new ArrayList<>();
    for (var r : populations) result.add(r.get(0));
    return result;
  }
  
  public static void main(String[] args) {
    String input = "KZBWPFHRAFHMFSNYSMNOZYBYLLLYJFBGZYYYZYEKCJVSACAEFLMAJZQAZYHIJFUNHLCGCINWFIHHHTLNVZLSHSVOZDPYSMNYJXHMNODNHPATXFWGHZPGHCVRWYSNFUSPPETRJSIIZSAAOYLNEENGHYAMAZBYSMNSJRNGZGSEZLNGHTSTJMNSJRESFRPGQPSYFGSWZMBGQFBCCEZTTPOYNIVUJRVSZSCYSEYJWYHUJRVSZSCRNECPFHHZJBUHDHSNNZQKADMGFBPGBZUNVFIGNWLGCWSATVSSWWPGZHNETEBEJFBCZDPYJWOSFDVWOTANCZIHCYIMJSIGFQLYNZZSETSYSEUMHRLAAGSEFUSKBZUEJQVTDZVCFHLAAJSFJSCNFSJKCFBCFSPITQHZJLBMHECNHFHGNZIEWBLGNFMHNMHMFSVPVHSGGMBGCWSEZSZGSEPFQEIMQEZZJIOGPIOMNSSOFWSKCRLAAGSKNEAHBBSKKEVTZSSOHEUTTQYMCPHZJFHGPZQOZHLCFSVYNFYYSEZGNTVRAJVTEMPADZDSVHVYJWHGQFWKTSNYHTSZFYHMAEJMNLNGFQNFZWSKCCJHPEHZZSZGDZDSVHVYJWHGQFWKTSNYHTSZFYHMAEDNJZQAZSCHPYSKXLHMQZNKOIOKHYMKKEIKCGSGYBPHPECKCJJKNISTJJZMHTVRHQSGQMBWHTSPTHSNFQZKPRLYSZDYPEMGZILSDIOGGMNYZVSNHTAYGFBZZYJKQELSJXHGCJLSDTLNEHLYZHVRCJHZTYWAFGSHBZDTNRSESZVNJIVWFIVYSEJHFSLSHTLNQEIKQEASQJVYSEVYSEUYSMBWNSVYXEIKWYSYSEYKPESKNCGRHGSEZLNGHTSIZHSZZHCUJWARNEHZZIWHZDZMADNGPNSYFZUWZSLXJFBCGEANWHSYSEGGNIVPFLUGCEUWTENKCJNVTDPNXEIKWYSYSFHESFPAJSWGTYVSJIOKHRSKPEZMADLSDIVKKWSFHZBGEEATJLBOTDPMCPHHVZNYVZBGZSCHCEZZTWOOJMBYJSCYFRLSZSCYSEVYSEUNHZVHRFBCCZZYSEUGZDCGZDGMHDYNAFNZHTUGJJOEZBLYZDHYSHSGJMWZHWAFTIAAY";
    int keyLen = findKeyLength(input);
    System.out.println("Key length: " + keyLen);
    List<List<Integer>> keys = findKey(input, 500, 300, keyLen);
    System.out.println("Keys: " + DecodeUtils.getKeysFromEncodings(keys));
    System.out.println("Decoding: " + DecodeUtils.decodeInput(input, keys));
    // Output:
    // Keys: [LFNGWOMTJECQVSYRDKHIZPUBXA, QPYLEABRTDZUIVJGFWNHMKCXSO, ANDUKZBTCMVIPSHLFJEQROGXYW, LZDVCPEOYJAXISWFQUNRBKGMTH]
    // Decoding: CONGRATULATIONSTHISWASNTQUITEANEASYTASKANDONLYACOUPLEOFLASTYEARSTUDENTSGOTTOTHISPOINTNOWALLTHISTEXTISJUSTGARBAGETOLETYOUUSESOMEFREQUENCYANALYSISWESETSAILONTHISNEWSEABECAUSETHEREISNEWKNOWLEDGETOBEGAINEDANDNEWRIGHTSTOBEWONANDTHEYJUSTBEWONANDUSEDFORTHEPROGRESSOFALLPEOPLEFORSPACESCIENCELIKENUCLEARSCIENCEANDALLTECHNOLOGYHASNOCONSCIENCEOFITSOWNWHETHERITWILLBECOMEAFORCEFORGOODORILLDEPENDSONMANANDONLYIFTHEUNITEDSTATESOCCUPIESAPOSITIONOFPREEMINENCECANWEHELPDECIDEWHETHERTHISNEWOCEANWILLBEASEAOFPEACEORANEWTERRIFYINGTHEATEROFWARIDONOTSAYTHEWESHOULDORWILLGOUNPROTECTEDAGAINSTTHEHOSTILEMISUSEOFSPACEANYMORETHANWEGOUNPROTECTEDAGAINSTTHEHOSTILEUSEOFLANDORSEABUTIDOSAYTHATSPACECANBEEXPLOREDANDMASTEREDWITHOUTFEEDINGTHEFIRESOFWARWITHOUTREPEATINGTHEMISTAKESTHATMANHASMADEINEXTENDINGHISWRITAROUNDTHISGLOBEOFOURSWECHOOSETOGOTOTHEMOONINTHISDECADEANDDOTHEOTHERTHINGSNOTBECAUSETHEYAREEASYBUTBECAUSETHEYAREHARDBECAUSETHATGOALWILLSERVETOORGANIMEANDMEASURETHEBESTOFOURENERGIESANDSKILLSBECAUSETHATCHALLENGEISONETHATWEAREWILLINGTOACCEPTONEWEAREUNWILLINGTOPOSTPONEANDONEWHICHWEINTENDTOWINANDTHEOTHERSTOOOKANDNOWTHEREALDEALBITLYSLASHTHREEDHCAPITALTTHREEEIGHTCAPITALX
  }
}
