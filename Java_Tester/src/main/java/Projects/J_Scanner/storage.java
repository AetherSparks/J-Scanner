


package Projects.J_Scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
import java.util.Scanner;


public class storage {
    private static void uploadAnswers() {
        String folderPath = "C:\\Users\\shrey\\IdeaProjects\\Java_Tester"; // Folder path

        List<String> csvFileNames = getAvailableTests(folderPath);

        if (csvFileNames.isEmpty()) {
            System.out.println("No available tests found in the specified folder.");
            return;
        }

        // Print the available CSV file names for the student to choose from
        System.out.println("Available Tests:");
        for (int i = 0; i < csvFileNames.size(); i++) {
            System.out.println((i + 1) + ". " + csvFileNames.get(i));
        }

        // Prompt the student to choose a test
        Scanner scanner = new Scanner(System.in);
        System.out.print("Choose a test to attempt (enter the number): ");
        int choice = scanner.nextInt();

        // Validate the choice
        if (choice < 1 || choice > csvFileNames.size()) {
            System.out.println("Invalid choice. Please enter a valid number.");
            return;
        }

        // Get the selected file name
        String selectedFileName = csvFileNames.get(choice - 1);

        System.out.println("You have selected the test: " + selectedFileName);

        // Get student name and enrollment number from login credentials or student.csv
        String studentName = ""; // Fetch student name
        String enrollmentNumber = ""; // Fetch enrollment number

        // Ask for the file path of the PNG containing answers
        System.out.print("Enter the file path of the PNG containing your answers: ");
        String pngFilePath = scanner.next();

        try {
            // Use Tesseract OCR to extract answers from PNG
            List<String> answers = extractAnswersFromPNG(pngFilePath);

            // Append student's answers to the selected test's CSV file
            appendStudentAnswers(selectedFileName, studentName, enrollmentNumber, answers);

            System.out.println("Answers uploaded successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while processing the PNG file.");
            e.printStackTrace();
        }
    }

    private static List<String> getAvailableTests(String folderPath) {
        List<String> availableTests = new ArrayList<>();
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".csv") &&
                        !file.getName().equalsIgnoreCase("students.csv") &&
                        !file.getName().equalsIgnoreCase("teachers.csv") &&
                        isTestOngoing(file)) {
                    String fileName = file.getName().replaceFirst("[.][^.]+$", ""); // Remove file extension
                    availableTests.add(fileName);
                }
            }
        }
        return availableTests;
    }

    private static boolean isTestOngoing(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null) {
                String[] cells = line.split(",");
                return cells.length > 1 && cells[1].trim().equalsIgnoreCase("Ongoing");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static List<String> extractAnswersFromPNG(String pngFilePath) throws IOException {
        // Use Tesseract OCR to extract answers from the PNG
        // Your Tesseract OCR code goes here
        return new ArrayList<>(); // Replace this with actual extracted answers
    }

    private static void appendStudentAnswers(String selectedFileName, String studentName, String enrollmentNumber, List<String> answers) throws IOException {
        String csvFileName = selectedFileName + ".csv";
        FileWriter csvWriter = new FileWriter(csvFileName, true); // Append mode

        // Write student's name and enrollment number
        csvWriter.write(studentName + "," + enrollmentNumber + ",");

        // Write student's answers
        for (String answer : answers) {
            csvWriter.write(answer + ",");
        }
        csvWriter.write("\n");

        csvWriter.flush();
        csvWriter.close();
    }
}
