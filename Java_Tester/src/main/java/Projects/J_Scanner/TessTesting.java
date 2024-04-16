
package Projects.J_Scanner;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TessTesting {
    public static void main() {
        Scanner scanner = new Scanner(System.in);

        try {
            // Path to Tesseract OCR executable
            String tesseractPath = "C:\\Program Files\\Tesseract-OCR\\tesseract.exe";

            // Ask for the name of the test
            System.out.print("Enter the name of the test: ");
            String testName = scanner.nextLine();

            // Ask for the file path of the answer key
            System.out.print("Enter the file path of the answer key: ");
            String answerKeyPath = scanner.nextLine();

            processAnswerKey(tesseractPath, testName, answerKeyPath);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void processAnswerKey(String tesseractPath, String testName, String answerKeyPath)
            throws IOException, InterruptedException {
        // PSM value for page segmentation (PSM = Page Segmentation)
        String psmValue = "11"; // Adjust this value based on your image characteristics

        // Mapping of answer patterns to their respective options
        Map<String, Character> optionMap = new HashMap<>();
        optionMap.put("@bcd", 'a');
        optionMap.put("a@cd", 'b');
        optionMap.put("ab@d", 'c');
        optionMap.put("abc@", 'd');
        // Add mappings for other answer patterns

        // Build the command to execute Tesseract OCR with PSM
        ProcessBuilder processBuilder = new ProcessBuilder(
                tesseractPath,
                answerKeyPath,
                "stdout",
                "-c", "tessedit_pageseg_mode=" + psmValue
        );
        Process process = processBuilder.start();

        // Read the output of the process
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        // Create or append to the CSV file to store the answers
        String csvFileName = testName + ".csv";
        FileWriter csvWriter;
        if (Files.exists(Paths.get(csvFileName))) {
            csvWriter = new FileWriter(csvFileName, true); // Append mode
        } else {
            csvWriter = new FileWriter(csvFileName);
        }

        // Process the Tesseract output
        StringBuilder answerLine = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if (line.matches("[a-d@ ]+")) {
                // Remove spaces and map answer to option
                String answer = line.replaceAll("\\s+", "");
                char option = mapToOption(answer, optionMap);
                // Append the mapped character to the answer line followed by a comma
                answerLine.append(option).append(",");
            }
        }




// Write the answer line followed by a newline character
        csvWriter.write(answerLine.toString() + "\n");



        // Close CSV writer
        csvWriter.flush();
        csvWriter.close();

        System.out.println("Answer key processed successfully. Answers stored in " + csvFileName);

        // Wait for the process to finish
        process.waitFor();
    }

    // Method to map each answer to a specific option
    private static char mapToOption(String answer, Map<String, Character> optionMap) {
        for (Map.Entry<String, Character> entry : optionMap.entrySet()) {
            String pattern = entry.getKey();
            char option = entry.getValue();
            if (answer.matches(pattern)) {
                return option;
            }
        }
        // Default option if no match found
        return '-';
    }
}
