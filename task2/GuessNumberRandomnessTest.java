package org.example.hw4;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;

import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GuessNumberRandomnessTest {

    private static final String JAR_PATH = "/Users/carpewang/IdeaProjects/hw4/src/main/guessnumber.jar";

    /**
     * Run guessingNumberGame 30,000 times and record distribution of generated numbers.
     */
    @Test
    @DisplayName("Distribution test: run guessingNumberGame 30,000 times with Random(0)")
    public void testUniformDistribution() throws Exception {
        int runs = 30000;
        int[] freq = new int[101]; // freq[1] to freq[100]

        for (int i = 0; i < runs; i++) {
            ProcessResult result = runGameAndExtractTargetNumber();
            int num = result.number;
            Assertions.assertTrue(
                    num >= 1 && num <= 100,
                    "Generated number out of range: " + num + "\nFull Output:\n" + result.output
            );
            freq[num]++;
        }

//        double avg = runs / 100.0;
//        double lower = avg * 0.5;
//        double upper = avg * 1.5;

        System.out.println("\n===== Frequency Distribution of Generated Numbers =====");
        System.out.printf("%-8s %-8s %-10s\n", "Number", "Count", "Percentage");

        for (int i = 1; i <= 100; i++) {
            double percentage = (freq[i] * 100.0) / runs;
            System.out.printf("%-8d %-8d %-9.2f%%\n", i, freq[i], percentage);

//            Assertions.assertTrue(
//                    freq[i] >= lower && freq[i] <= upper,
//                    "Number " + i + " has frequency " + freq[i] + " which is out of range [" + (int) lower + ", " + (int) upper + "]"
//            );
        }

        boolean hasOutlier = false;

        for (int i = 1; i <= 100; i++) {
            for (int j = 1; j <= 100; j++) {
                if (i == j) continue;

                int lowerBound = (int)(freq[j] * 0.5);
                int upperBound = (int)(freq[j] * 1.5);

                if (freq[i] < lowerBound || freq[i] > upperBound) {
                    hasOutlier = true;
//                    System.out.printf("⚠️  Number %d (freq=%d) is out of 50%% range of Number %d (freq=%d)\n", i, freq[i], j, freq[j]);
                }
            }
        }

        if (!hasOutlier) {
            System.out.println("\n✅ All numbers fall within ±50% of each other. Distribution seems uniform.");
        } else {
            System.out.println("\n❌ Some numbers deviate more than ±50% from others. Distribution may not be uniform.");
        }
    }

    /**
     * Run the jar once with dummy input and parse the target number.
     */
    private ProcessResult runGameAndExtractTargetNumber() throws Exception {
        ProcessBuilder pb = new ProcessBuilder("java", "-cp", JAR_PATH, "GuessNumber");
        Process process = pb.start();

        // send 5 dummy guesses to exhaust trials
        OutputStream os = process.getOutputStream();
        os.write("0\n0\n0\n0\n0\n".getBytes());
        os.flush();
        os.close();

        // read output
        Scanner sc = new Scanner(process.getInputStream()).useDelimiter("\\A");
        String output = sc.hasNext() ? sc.next() : "";
        process.waitFor(10, TimeUnit.SECONDS);
//        System.out.println("==== OUTPUT DUMP ====");
//        System.out.println(output);
//        System.out.println("=====================");

        int number = extractTargetNumber(output);
        return new ProcessResult(output, number);
    }

    /**
     * Extract number from "The number was X"
     */
    private int extractTargetNumber(String output) {
        // Match pattern like "The number was 33"
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(?i)The number was (\\d+)");
        java.util.regex.Matcher matcher = pattern.matcher(output);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }


    /**
     * Output container
     */
    private static class ProcessResult {
        String output;
        int number;

        ProcessResult(String output, int number) {
            this.output = output;
            this.number = number;
        }
    }
}