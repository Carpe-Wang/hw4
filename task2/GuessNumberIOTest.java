package org.example.hw4;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Tests for the GuessNumber application.
 * This test suite identifies faults in the number guessing game by running the JAR
 * in separate processes and analyzing its behavior with different inputs.
 */
public class GuessNumberIOTest {

    private static final String JAR_PATH = "/Users/carpewang/IdeaProjects/hw4/src/main/guessnumber.jar";
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    /**
     * FAULT #1: Game crashes with empty input
     */
    @Test
    @DisplayName("Game crashes with empty input")
    public void testEmptyInput() throws Exception {
        // Verify the JAR exists
        Assertions.assertTrue(new File(JAR_PATH).exists(), "JAR file exists");

        // Run the game with empty input
        ProcessResult result = runGameProcess("\n");

        // Verify the process terminated abnormally (due to exception)
        Assertions.assertNotEquals(0, result.exitCode,
                "Game should handle empty input gracefully instead of crashing");
    }

    /**
     * FAULT #2: Game crashes with non-integer input
     */
    @Test
    @DisplayName("Game crashes with non-integer input")
    public void testNonIntegerInput() throws Exception {
        Assertions.assertTrue(new File(JAR_PATH).exists(), "JAR file exists");
        ProcessResult result = runGameProcess("abc\n");
        Assertions.assertNotEquals(0, result.exitCode,
                "Game should handle non-integer input gracefully instead of crashing");
    }

    /**
     * FAULT #3: Game doesn't validate input range
     */
    @Test
    @DisplayName("Game accepts out-of-range input")
    public void testOutOfRangeInput() throws Exception {
        Assertions.assertTrue(new File(JAR_PATH).exists(), "JAR file exists");
        ProcessResult result = runGameProcess("101\n1\n");

        // Check if the output contains any validation message
        Assertions.assertFalse(
                result.output.contains("Invalid input") && result.output.contains("between 1 and 100"),
                "Game should validate input range but doesn't"
        );
    }

    /**
     * FAULT #4: Game accepts negative numbers
     */
    @Test
    @DisplayName("Game accepts negative input")
    public void testNegativeNumberInput() throws Exception {
        Assertions.assertTrue(new File(JAR_PATH).exists(), "JAR file exists");
        ProcessResult result = runGameProcess("-5\n1\n");

        Assertions.assertFalse(
                result.output.contains("Invalid input") && result.output.contains("between 1 and 100"),
                "Game should reject negative numbers but doesn't"
        );
    }

    /**
     * FAULT #5: Game uses fixed seed for random number generation
     */
    @Test
    @DisplayName("Game uses fixed seed for random number generation")
    public void testRandomNumberGeneration() throws Exception {
        Assertions.assertTrue(new File(JAR_PATH).exists(), "JAR file exists");

        List<Integer> targetNumbers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            // Intentionally guess wrong multiple times
            ProcessResult result = runGameProcess("1\n2\n3\n4\n5\n");
            int targetNumber = extractTargetNumber(result.output);
            if (targetNumber > 0) {
                targetNumbers.add(targetNumber);
            }
        }

        // Check if we found at least 2 target numbers and they are distinct
        boolean allSame = targetNumbers.size() >= 2 &&
                targetNumbers.stream().distinct().count() == 1;

        Assertions.assertFalse(allSame,
                "Game appears to use a fixed seed, generating the same number across multiple runs");
    }

    /**
     * FAULT #6: Game accepts zero as input
     */
    @Test
    @DisplayName("Game accepts zero as input")
    public void testZeroInput() throws Exception {
        Assertions.assertTrue(new File(JAR_PATH).exists(), "JAR file exists");
        ProcessResult result = runGameProcess("0\n1\n");

        Assertions.assertFalse(
                result.output.contains("Invalid input") && result.output.contains("between 1 and 100"),
                "Game should reject zero as input but doesn't"
        );
    }

    /**
     * FAULT #7: No graceful exit mechanism
     */
    @Test
    @DisplayName("Game lacks graceful exit mechanism")
    public void testExitMechanism() throws Exception {
        Assertions.assertTrue(new File(JAR_PATH).exists(), "JAR file exists");
        ProcessResult result = runGameProcess("q\n1\n");

        Assertions.assertFalse(
                result.output.contains("quit") || result.output.contains("exit"),
                "Game should provide a way to exit gracefully but doesn't"
        );
    }

    /**
     * FAULT #8: No retry option after game ends
     */
    @Test
    @DisplayName("Game lacks retry option")
    public void testRetryOption() throws Exception {
        Assertions.assertTrue(new File(JAR_PATH).exists(), "JAR file exists");
        ProcessResult result = runGameProcess("1\n2\n3\n4\n5\n");

        Assertions.assertFalse(
                result.output.contains("play again") || result.output.contains("another game"),
                "Game should offer a retry option but doesn't"
        );
    }

    /**
     * FAULT #9: Limited feedback for guesses
     */
    @Test
    @DisplayName("Game provides limited feedback")
    public void testFeedbackQuality() throws Exception {
        Assertions.assertTrue(new File(JAR_PATH).exists(), "JAR file exists");
        ProcessResult result = runGameProcess("100\n1\n");

        Assertions.assertFalse(
                result.output.contains("much higher") ||
                        result.output.contains("far from") ||
                        result.output.contains("close"),
                "Game should provide more detailed feedback but doesn't"
        );
    }

    /**
     * FAULT #10: No hint system
     */
    @Test
    @DisplayName("Game lacks hint system")
    public void testHintSystem() throws Exception {
        Assertions.assertTrue(new File(JAR_PATH).exists(), "JAR file exists");
        ProcessResult result = runGameProcess("hint\n1\n");

        Assertions.assertFalse(
                result.output.contains("Hint:") ||
                        result.output.contains("clue"),
                "Game should provide hints but doesn't"
        );
    }

    // ----------------------------------------------------------------------
    //                    Additional Suggested Tests
    // ----------------------------------------------------------------------

    /**
     * Normal Scenario #1: Whether the game provides instructions or a prompt at startup.
     * Some games print a prompt such as "Please enter a number" at the beginning.
     * This test checks for the existence of such information.
     * If the game itself does not include such a prompt, you may remove or adjust this assertion.
     */
    @Test
    @DisplayName("Game provides instructions or prompt at the start")
    public void testGameInstructions() throws Exception {
        Assertions.assertTrue(new File(JAR_PATH).exists(), "JAR file exists");
        // Provide a minimal input, for example "1\n", and observe the initial output of the program.
        ProcessResult result = runGameProcess("1\n");

        // Check whether the output contains "guess", "number", or similar prompt (adjust the specific string based on the actual game)
        String outputLower = result.output.toLowerCase();
        Assertions.assertTrue(
                outputLower.contains("guess") || outputLower.contains("number") || outputLower.contains("please enter"),
                "Expected the game to prompt the user to guess a number, but prompt not found."
        );
    }

    /**
     * Normal Scenario #2: Winning the game on the first try (if the random number is predictable or fixed)
     * If you know that the game uses a fixed seed and the answer is 42, you can directly use this test.
     * If the answer is uncertain, you can modify the test to loop until the correct guess is made.
     */
    @Test
    @DisplayName("Game is won immediately with a correct guess (if random is predictable)")
    public void testGuessCorrectOnFirstTry() throws Exception {
        Assertions.assertTrue(new File(JAR_PATH).exists(), "JAR file exists");
        // Here we assume the answer is 42. Modify according to your actual scenario if needed.
        ProcessResult result = runGameProcess("42\n");

        // If the guess is correct, the game should output a message containing "congrat" or "win", and the exitCode is typically 0.
        // This depends on how your program is written.
        String outputLower = result.output.toLowerCase();
        boolean hasWinMessage = outputLower.contains("congrat") || outputLower.contains("win");

        // If the game is designed to exit normally on a correct guess (exitCode = 0), then you can also assert that:
        // Assertions.assertEquals(0, result.exitCode, "Expected exit code 0 after a correct guess.");

        Assertions.assertTrue(
                hasWinMessage,
                "Expected success message after guessing the correct number, but not found."
        );
    }

    /**
     * Normal Scenario #3: Winning after multiple guesses.
     * If the game is truly random, you cannot guarantee a win; however, this test provides one approach:
     * entering several incorrect guesses followed by the correct guess.
     * If the game is truly unpredictable, you may omit this test or detect the multi-guess process in another way.
     */
    @Test
    @DisplayName("Multiple guesses before success (if answer is known or partially known)")
    public void testMultipleGuessesBeforeSuccess() throws Exception {
        Assertions.assertTrue(new File(JAR_PATH).exists(), "JAR file exists");

        String input = "10\n20\n30\n50\n";
        ProcessResult result = runGameProcess(input);

        String outputLower = result.output.toLowerCase();
        boolean hasWinMessage = outputLower.contains("congrat") || outputLower.contains("win");
        Assertions.assertTrue(
                hasWinMessage,
                "Expected the game to indicate a correct guess after multiple tries, but it's not successful."
        );
    }

    /**
     * Abnormal Scenario: Multiple consecutive invalid inputs followed by a valid input.
     * This test is used to verify whether the game maintains robustness after encountering multiple erroneous inputs,
     * rather than simply crashing.
     */
    @Test
    @DisplayName("Multiple invalid inputs in a row, followed by valid input")
    public void testMultipleInvalidInputsInARow() throws Exception {
        Assertions.assertTrue(new File(JAR_PATH).exists(), "JAR file exists");

        // Multiple invalid inputs: non-numeric, negative number, excessively large number, zero, and finally a valid number (1)
        // Observe whether the game crashes or whether it outputs error messages.
        String input = "abc\n-5\n999999999\n0\n1\n";
        ProcessResult result = runGameProcess(input);

        // Ideally, the game should continue running until a valid input is provided.
        // You can check if the output contains error messages such as "Invalid input" (if your program prints them).
        // The following is just an example assertion: if the program crashes, the exitCode might not equal 0.
        // If the program does not print any error message, this assertion might also pass; adjust according to your game's logic.
        Assertions.assertNotEquals(
                0,
                result.exitCode,
                "If the game doesn't handle multiple invalid inputs, it may crash or produce a non-zero exit code. Adjust checks accordingly."
        );
    }

    // ----------------------------------------------------------------------
    //                 Original Helper Methods (do not modify)
    // ----------------------------------------------------------------------

    /**
     * Helper method to extract the target number from game output
     */
    private int extractTargetNumber(String output) {
        Scanner scanner = new Scanner(output);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains("The number was")) {
                String[] parts = line.split(" ");
                try {
                    return Integer.parseInt(parts[parts.length - 1]);
                } catch (NumberFormatException e) {
                    return -1;  // Couldn't parse the number
                }
            }
        }
        return -1;  // Couldn't find the number
    }

    /**
     * Helper method to run the game as a separate process with the given input
     */
    private ProcessResult runGameProcess(String input) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                "java", "-cp", JAR_PATH, "GuessNumber"
        );

        Process process = pb.start();

        // Write input to the process
        OutputStream outputStream = process.getOutputStream();
        outputStream.write(input.getBytes());
        outputStream.flush();
        outputStream.close();

        // Collect the output
        Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\\A");
        String output = scanner.hasNext() ? scanner.next() : "";

        // Wait for the process to finish (with timeout to prevent hanging)
        boolean completed = process.waitFor(5, TimeUnit.SECONDS);
        int exitCode = completed ? process.exitValue() : -1;

        // If not completed, force termination
        if (!completed) {
            process.destroyForcibly();
        }

        return new ProcessResult(output, exitCode);
    }


    /**
     * Helper class to store process execution results
     */
    private static class ProcessResult {
        public final String output;
        public final int exitCode;

        public ProcessResult(String output, int exitCode) {
            this.output = output;
            this.exitCode = exitCode;
        }
    }
}
