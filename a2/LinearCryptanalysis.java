import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinearCryptanalysis {
    private static int count = 20000;
    private static String[] plaintext = new String[count];
    private static String[] ciphertext = new String[count];
    private static Map<Integer, Integer> sInverse = new HashMap<Integer, Integer>();

    public static void main(String[] args) {
        loadStaticData();
        createSInverse();

        new LinearCryptanalysis().a();
        new LinearCryptanalysis().b();
        new LinearCryptanalysis().d();
    }

    public void a() {
        int bias = getBiasForKeyForPartsAB(0b0111, 0b0110);
        System.out.println("a:");
        System.out.println("bias: " + bias + " / " + count + " - 1/2 = " + ((double) bias / count - 0.5d));
        System.out.println();
    }

    public void b() {
        int[] maxKeys = { 0, 0 };
        int maxBias = 0;

        for (int k1 = 0; k1 < 16; k1++) {
            for (int k2 = 0; k2 < 16; k2++) {
                int bias = getBiasForKeyForPartsAB(k1, k2);

                if (bias > maxBias) {
                    maxBias = bias;
                    maxKeys[0] = k1;
                    maxKeys[1] = k2;
                }
            }
        }


        System.out.println("b:");
        System.out.println("key: " + Integer.toBinaryString(maxKeys[0]) + " " + Integer.toBinaryString(maxKeys[1]));
        System.out.println("bias: " + maxBias + " / " + count + " - 1/2 = " + ((double) maxBias / count - 0.5d));
        System.out.println();
    }

    public void d() {
        int maxKey = 0;
        int maxBias = 0;

        for (int k = 0; k < 16; k++) {
            for (int l = 0; l < 16; l++) {
                int key = (k << 12) + (l << 4) + 0b1111;
                int bias = getBiasForKeyForPartD(key);

                if (bias > maxBias) {
                    maxBias = bias;
                    maxKey = key;
                }
            }
        }


        System.out.println("d:");
        System.out.println("key: " + Integer.toBinaryString(maxKey));
        System.out.println("bias: " + maxBias + " / " + count + " - 1/2 = " + ((double) maxBias / count - 0.5d));
        System.out.println();
    }

    public int getBiasForKeyForPartsAB(int key1, int key2) {
        int countZeroes = 0;
        
        for (int i = 0; i < count; i++) {
            String p = plaintext[i];
            String c = ciphertext[i];

            if (getResultForPartsAB(p, c, key1, key2) == 0) {
                countZeroes++;
            }
        }

        return countZeroes;
    }

    public int getBiasForKeyForPartD(int key) {
        int countZeroes = 0;
        
        for (int i = 0; i < count; i++) {
            String p = plaintext[i];
            String c = ciphertext[i];

            if (getResultForPartD(p, c, key) == 0) {
                countZeroes++;
            }
        }

        return countZeroes;
    }

    public int getResultForPartsAB(String p, String c, int key1, int key2) {
        int v1 = Integer.parseInt(c.substring(4, 8), 2) ^ key1;
        int v2 = Integer.parseInt(c.substring(12, 16), 2) ^ key2;
        
        int u1 = sInverse.get(v1);
        int u2 = sInverse.get(v2);

        int u46 = (u1 & 4) >> 2;
        int u48 = (u1 & 1);
        int u414 = (u2 & 4) >> 2;
        int u416 = (u2 & 1);
        int p5 = Integer.parseInt(p.charAt(4)+"", 2);
        int p7 = Integer.parseInt(p.charAt(6)+"", 2);
        int p8 = Integer.parseInt(p.charAt(7)+"", 2);

        return u46 ^ u48 ^ u414 ^ u416 ^ p5 ^ p7 ^ p8;
    }

    public int getResultForPartD(String p, String c, int key) {
        int v = Integer.parseInt(c, 2) ^ key;
        
        int u = (sInverse.get((v & 0b1111000000000000) >> 12) << 12) +
                (sInverse.get((v & 0b111100000000) >> 8) << 8) +
                (sInverse.get((v & 0b11110000) >> 4) << 4) +
                sInverse.get(v & 0b1111);

        int u42 = (u & 0b0100000000000000) >> 14;
        int u46 = (u & 0b010000000000) >> 10;
        int u410 = (u & 0b01000000) >> 6;
        int u414 = (u & 0b0100) >> 2;
        int p1 = Integer.parseInt(p.charAt(0)+"", 2);
        int p4 = Integer.parseInt(p.charAt(3)+"", 2);
        int p9 = Integer.parseInt(p.charAt(8)+"", 2);
        int p12 = Integer.parseInt(p.charAt(11)+"", 2);

        return u42 ^ u46 ^ u410 ^ u414 ^ p1 ^ p4 ^ p9 ^ p12;
    }

    public static void loadStaticData() {
        try {
            BufferedReader pt = new BufferedReader(new FileReader("C:\\Users\\Saquib Hafiz\\Documents\\1 University Homework\\Winter 2016\\CO 487\\a2\\plaintext.txt"));
            BufferedReader ct = new BufferedReader(new FileReader("C:\\Users\\Saquib Hafiz\\Documents\\1 University Homework\\Winter 2016\\CO 487\\a2\\ciphertext.txt"));

            for (int i = 0; i < count; i++) {
                plaintext[i] = pt.readLine();
                ciphertext[i] = ct.readLine();
            }

            pt.close();
            ct.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createSInverse() {
        sInverse.put(14, 0);
        sInverse.put(4, 1);
        sInverse.put(13, 2);
        sInverse.put(1, 3);
        sInverse.put(2, 4);
        sInverse.put(15, 5);
        sInverse.put(11, 6);
        sInverse.put(8, 7);
        sInverse.put(3, 8);
        sInverse.put(10, 9);
        sInverse.put(6, 10);
        sInverse.put(12, 11);
        sInverse.put(5, 12);
        sInverse.put(9, 13);
        sInverse.put(0, 14);
        sInverse.put(7, 15);
    }
}