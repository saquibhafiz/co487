import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DifferentialCryptanalysis {
    private static Map<Integer, Integer> sInverse = new HashMap<Integer, Integer>();

    private static class Pair {
        private final int p1;
        private final int p2;
        private final int c1;
        private final int c2;

        private Pair(String[] elements) {
            p1 = Integer.parseInt(elements[0], 2);
            p2 = Integer.parseInt(elements[1], 2);
            c1 = Integer.parseInt(elements[2], 2);
            c2 = Integer.parseInt(elements[3], 2);
        }

        private int getDeltaU4(int key) {
            int v4c1 = c1 ^ key;
            int v4c2 = c2 ^ key;

            int u4c1 = (sInverse.get((v4c1 & 0b1111000000000000) >> 12) << 12) + (sInverse.get((v4c1 & 0b111100000000) >> 8) << 8) + (sInverse.get((v4c1 & 0b11110000) >> 4) << 4) + sInverse.get(v4c1 & 0b1111);
            int u4c2 = (sInverse.get((v4c2 & 0b1111000000000000) >> 12) << 12) + (sInverse.get((v4c2 & 0b111100000000) >> 8) << 8) + (sInverse.get((v4c2 & 0b11110000) >> 4) << 4) + sInverse.get(v4c2 & 0b1111);

            return u4c1 ^ u4c2;
        }
    }

    private static int count = 5000;
    private static Pair[] pairs = new Pair[count];

    public static void main(String[] args) {
        loadStaticData();
        createSInverse();

        new DifferentialCryptanalysis().a();
    }

    public void a() {
        int[] maxKeys = { 0, 0 };
        int maxCount = 0;

        for (int k1 = 0; k1 < 16; k1++) {
            for (int k2 = 0; k2 < 16; k2++) {
                int bias = getCount(k1, k2);

                if (bias > maxCount) {
                    maxCount = bias;
                    maxKeys[0] = k1;
                    maxKeys[1] = k2;
                }
            }
        }


        System.out.println("a:");
        System.out.println("key: " + Integer.toBinaryString(maxKeys[0]) + " " + Integer.toBinaryString(maxKeys[1]));
        System.out.println("count: " + maxCount + " / " + count + " = " + ((double) maxCount / count));
    }

    public int getCount(int key1, int key2) {
        int matches = 0;
        
        for (int i = 0; i < count; i++) {
            if (pairs[i].getDeltaU4((key1 << 8) + key2) == 0b0000011000000110) {
                matches++;
            }
        }

        return matches;
    }

    public static void loadStaticData() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Saquib Hafiz\\Documents\\1 University Homework\\Winter 2016\\CO 487\\a2\\ciphertext94.txt"));

            for (int i = 0; i < count; i++) {
                pairs[i] = new Pair(reader.readLine().split(","));
            }

            reader.close();
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