


// J-Scanner Created by Abhiraj Ghose E23CSEU0014 and Pallav Sharma E23CSEU0022

//Package and Importing

package Projects.J_Scanner;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.FileReader;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;



//Main Class

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        // Introduction of J-Scanner, Choosing LogIn or SignUp
        System.out.println("Welcome to J-Scanner Created by Abhiraj Ghose and Pallav Sharma");
        System.out.println("Choose an option:");
        System.out.println("1. Log in");
        System.out.println("2. Sign up");
        System.out.print("Enter your choice (1 or 2): ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline character

        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                signUp();
                break;
            default:
                System.out.println("Invalid choice. Please enter 1 or 2.");
        }
    }


    private static void login() {

        // Choose Log In as a student or as a Teacher

        Scanner scanner = new Scanner(System.in);


        System.out.println("Choose an option:");
        System.out.println("1. Log in as Teacher");
        System.out.println("2. Log in as Student");
        System.out.print("Enter your choice (1 or 2): ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline character

        switch (choice) {
            case 1:
                loginAsTeacher();
                break;
            case 2:
                loginAsStudent();
                break;
            default:
                System.out.println("Invalid choice. Please enter 1 or 2.");
        }
    }

    private static String teacherCode = ""; // Global variable to store the teacher's 5-letter code

    private static void loginAsTeacher() {
        // Logging in as a Teacher

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        try (BufferedReader reader = new BufferedReader(new FileReader("teachers.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[1].equals(username) && parts[2].equals(password)) {
                    System.out.println("Login successful! Welcome, " + parts[0] + ".");
                    teacherCode = parts[4]; // Store the teacher's 5-letter code
                    showTeacherOptions();
                    return;
                }
            }
            System.out.println("Incorrect username or password for Teacher.");
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private static void createNewTest() {
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
            // Write header for the CSV file
            csvWriter.write(teacherCode + ",Ongoing,");
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

        System.exit(0);
    }

    private static void showTeacherOptions() {
        // Display additional options for teachers

        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\nChoose an option:");
            System.out.println("1. Create a New Test");
            System.out.println("2. Conclude Test");
            System.out.println("3. View Result");
            System.out.println("4. Logout");

            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    createNewTest();

                    break;
                case 2:
                    concludeTest();
                    break;
                case 3:
                    viewResult();
                    break;
                case 4:
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 4.");
            }
        } while (choice != 4);
    }


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


// Define methods for each option below



        // Helper method to map each answer to a specific option



    // Method to map each answer to a specific option



    private static void concludeTest() {
        try {
            // Construct the file path
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the name of the Test which needs to be concluded: ");
            String testName = scanner.nextLine();
            String csvFileName = testName + ".csv";

            // Read all lines from the CSV file
            List<String> lines = Files.readAllLines(Paths.get(csvFileName));

            // Update the status to "Concluded"
            String[] cells = lines.get(0).split(",");
            cells[1] = "Concluded"; // Update the status in the second cell
            lines.set(0, String.join(",", cells));

            // Process answer key to store correct answers
            String[] answerKey = lines.get(0).split(",");
            Map<Integer, Character> correctAnswers = new HashMap<>();
            for (int i = 2; i < answerKey.length; i++) {
                correctAnswers.put(i, answerKey[i].charAt(0));
            }

            // Process student submissions
            for (int i = 1; i < lines.size(); i++) {
                String[] submission = lines.get(i).split(",");
                int totalMarks = 0;
                for (int j = 2; j < submission.length; j++) {
                    if (submission[j].charAt(0) == correctAnswers.get(j)) {
                        totalMarks++;
                    }
                }
                // Store total marks in the next cell
                submission = Arrays.copyOf(submission, submission.length + 1);
                submission[submission.length - 1] = Integer.toString(totalMarks);
                lines.set(i, String.join(",", submission)); // Update the line with total marks
            }

            // Write the updated lines back to the CSV file
            Files.write(Paths.get(csvFileName), lines);

            System.out.println("Test '" + testName + "' has been concluded successfully.");
            System.exit(0);
        } catch (IOException e) {
            System.err.println("Error concluding test: " + e.getMessage());
        }
    }



    private static void viewResult() {
        try {
            // Construct the file path
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the name of the Test to view results: ");
            String testName = scanner.nextLine();
            String csvFileName = testName + ".csv";

            // Read all lines from the CSV file
            List<String> lines = Files.readAllLines(Paths.get(csvFileName));

            // Check if the test is concluded
            String[] cells = lines.get(0).split(",");
            if (!cells[1].equals("Concluded")) {
                System.out.println("Test '" + testName + "' is not concluded yet.");
                return;
            }

            // List all the tests concluded for the teacher to choose from
            System.out.println("Concluded Tests:");
            for (String line : lines) {
                String[] cellsData = line.split(",");
                if (cellsData.length >= 2 && cellsData[1].equals("Concluded")) {
                    System.out.println("- " + testName);
                    break;
                }
            }

            // Choose an option
            System.out.println("\nChoose an option:");
            System.out.println("1. Print all results");
            System.out.println("2. Print a specific result");
            System.out.print("Enter your choice (1 or 2): ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    printAllResults(lines);
                    break;
                case 2:
                    printSpecificResult(lines);
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1 or 2.");
            }
        } catch (IOException e) {
            System.err.println("Error viewing results: " + e.getMessage());
        }
    }

    private static void printAllResults(List<String> lines) {
        // Print all results
        System.out.println("All Results:");
        for (int i = 1; i < lines.size(); i++) {
            String[] cells = lines.get(i).split(",");
            if (cells.length >= 3) {
                System.out.println("Name: " + cells[0] + ", Enrollment Number: " + cells[1] + ", Marks Achieved: " + cells[cells.length - 1]);
            }
        }
    }

    private static void printSpecificResult(List<String> lines) {
        // Print a specific result
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the enrollment number to view result: ");
        String enrollmentNumber = scanner.nextLine();

        boolean found = false;
        for (String line : lines) {
            String[] cells = line.split(",");
            if (cells.length >= 3 && cells[1].equals(enrollmentNumber)) {
                System.out.println("Student Details:");
                System.out.println("Name: " + cells[0]);
                System.out.println("Enrollment Number: " + cells[1]);
                System.out.println("Answers:");
                for (int i = 2; i < cells.length - 1; i++) {
                    System.out.println("Question " + (i - 1) + ": " + cells[i]);
                }
                System.out.println("Marks Achieved: " + cells[cells.length - 1]);
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("Result for enrollment number '" + enrollmentNumber + "' not found.");
        }
    }











    private static String studentName;
    private static String enrollmentNumber;

    private static String studentCode = ""; // Global variable to store the student's 5-digit code

    private static void loginAsStudent() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        // Check student credentials
        String studentName = authenticateStudent(username, password);
        if (studentName != null) {
            // Student authenticated
            System.out.println("Login successful. Welcome, " + studentName + "!");
            selectOption(); // Proceed with test selection
        } else {
            System.out.println("Invalid username or password. Please try again.");
            loginAsStudent(); // Retry login
        }
    }

    private static String authenticateStudent(String username, String password) {
        String studentsFilePath = "students.csv";

        try (BufferedReader reader = new BufferedReader(new FileReader(studentsFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] cells = line.split(",");
                if (cells.length >= 5 && cells[2].trim().equals(username) && cells[3].trim().equals(password)) {
                    studentName = cells[0].trim(); // Save student name
                    enrollmentNumber = cells[1].trim(); // Save enrollment number
                    studentCode = cells[4].trim(); // Save student code
                    return studentName; // Return student name
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private static void selectOption() {
        Scanner scanner = new Scanner(System.in);
        String choice;

        do {
            System.out.println("Select an option:");
            System.out.println("1. Attempt a test");
            System.out.println("2. Log out");
            System.out.print("Enter your choice: ");
            choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    attemptTest();
                    break;


                case "2":
                    System.out.println("Logged out successfully.");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
                    break;
            }
        } while (!choice.equals("2"));
    }

    private static void attemptTest() {
        String directoryPath = "C:\\Users\\shrey\\IdeaProjects\\Java_Tester";
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        List<String> testFiles = new ArrayList<>();

        if (files != null) {
            int count = 1;
            for (File file : files) {
                if (file.isFile() && !file.getName().equals("students.csv") && !file.getName().equals("teachers.csv")) {
                    try (Scanner scanner = new Scanner(file)) {
                        String firstLine = scanner.nextLine();
                        String[] firstLineCells = firstLine.split(",");

                        if (firstLineCells.length >= 2 && firstLineCells[0].equals(studentCode) && firstLineCells[1].trim().equals("Ongoing")) {
                            testFiles.add(file.getName());
                            System.out.println(count + ". " + file.getName());
                            count++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        if (testFiles.isEmpty()) {
            System.out.println("No ongoing tests available.");
            return;
        }


        // Ask the student to choose a test
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of the test you want to attempt: ");
        int choice = scanner.nextInt();

        if (choice > 0 && choice <= testFiles.size()) {
            String selectedTest = testFiles.get(choice - 1);
            System.out.println("You have selected the test: " + selectedTest);
            // Proceed with the test
            // Get file path of answer key and proceed with processing
            System.out.print("Enter the file path of the answer key: ");
            String answerKeyPath = scanner.next();
            try {
                studentAnswerProcess("C:\\Program Files\\Tesseract-OCR\\tesseract.exe", selectedTest, answerKeyPath, studentName, enrollmentNumber);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private static void studentAnswerProcess(String tesseractPath, String testName, String answerKeyPath, String studentName, String enrollmentNumber)
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
        List<String> command = new ArrayList<>();
        command.add(tesseractPath);
        command.add("\"" + answerKeyPath + "\""); // Enclose file path in double quotes
        command.add("stdout");
        command.add("-c");
        command.add("tessedit_pageseg_mode=" + psmValue);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        // Read the output of the process
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;

            // Create or append to the CSV file to store the answers
            String csvFileName = testName.endsWith(".csv") ? testName : testName + ".csv";
            FileWriter csvWriter;
            if (Files.exists(Paths.get(csvFileName))) {
                csvWriter = new FileWriter(csvFileName, true); // Append mode
            } else {
                csvWriter = new FileWriter(csvFileName);
                // Write header for the CSV file
                csvWriter.write("Student Name, Enrollment Number,");
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

            // Print the answer line before appending to the CSV file
            System.out.println("Final Answer Line: " + answerLine.toString()); // Debugging statement

            // Append student name, enrollment number, and answers to the CSV file
            csvWriter.write(studentName + "," + enrollmentNumber + "," + answerLine.toString() + "\n");

            // Close CSV writer
            csvWriter.flush();
            csvWriter.close();

            System.out.println("Student's answers processed successfully and stored in " + csvFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
















    private static void signUp() {
        // Signing Up Function

        Scanner scanner = new Scanner(System.in);
        System.out.println("Are you a teacher or a student? Enter 'teacher' or 'student': ");
        String userType = scanner.nextLine();

        if ("teacher".equalsIgnoreCase(userType)) {
            signUpTeacher();
        } else if ("student".equalsIgnoreCase(userType)) {
            signUpStudent();
        } else {
            System.out.println("Invalid user type. Please enter 'teacher' or 'student'.");
        }
    }

    private static void signUpTeacher() {
        //Signing up as a Teacher

        try (FileWriter writer = new FileWriter("teachers.csv", true)) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter your name: ");
            String name = scanner.nextLine();

            System.out.println("Enter your username: ");
            String username = scanner.nextLine();

            System.out.println("Enter your password: ");
            String password = scanner.nextLine();

            System.out.println("Enter your email: ");
            String email = scanner.nextLine();

            // Generate a 5-letter code (e.g., random or based on name/email)
            String code = generateCode(name, email);

            writer.write(name + "," + username + "," + password + "," + email + "," + code + "\n");
            System.out.println("Teacher signup successful. Your code is: " + code);
        } catch (IOException e) {
            System.out.println("An error occurred while signing up the teacher.");
            e.printStackTrace();
        }
    }

    private static void signUpStudent() {
        // Signing up as a Student

        try (FileWriter writer = new FileWriter("students.csv", true);
             Scanner scanner = new Scanner(System.in);
             Scanner teachersReader = new Scanner(new File("teachers.csv"))) {

            System.out.println("Enter your name: ");
            String name = scanner.nextLine();

            System.out.println("Enter your enrollment number: "); // Prompt for enrollment number
            String enrollmentNumber = scanner.nextLine(); // Read enrollment number

            System.out.println("Enter your username: ");
            String username = scanner.nextLine();

            System.out.println("Enter your password: ");
            String password = scanner.nextLine();

            System.out.println("Enter the 5-letter code of the teacher you want to sign up with: ");
            String code = scanner.nextLine();

            // Check if the teacher code exists in the teachers.csv file
            boolean teacherFound = false;
            String teacherName = "";
            while (teachersReader.hasNextLine()) {
                String[] teacherData = teachersReader.nextLine().split(",");
                if (teacherData.length >= 3 && teacherData[4].equalsIgnoreCase(code)) {
                    teacherFound = true;
                    teacherName = teacherData[0];
                    // Write name, enrollment number, username, password, and code to students.csv
                    writer.write(name + "," + enrollmentNumber + "," + username + "," + password + "," + code + "\n");
                    break;
                }
            }
            // Checking if the Code is assigned to a Teacher
            if (teacherFound) {
                System.out.println("Student signup successful. You are enrolled with teacher: " + teacherName);
            } else {
                System.out.println("Teacher with code '" + code + "' not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while signing up the student.");
            e.printStackTrace();
        }
    }


    private static String generateCode(String name, String email) {
        //Random Code Generator for each Teacher

        StringBuilder code = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < 5; i++) {
            int index = (int) (Math.random() * characters.length());
            code.append(characters.charAt(index));
        }
        return code.toString();
    }
}
