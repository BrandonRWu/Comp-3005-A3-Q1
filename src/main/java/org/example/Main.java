package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String url = "jdbc:postgresql://localhost:5432/students";
    private static final String user = "postgres";
    private static final String password = "admin";
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        int userInput = 0;
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            if(conn == null){
                System.out.println("Connection Failed! Check output console");
            }else {
                ResultSet result = conn.createStatement().executeQuery("SELECT current_database()");
                result.next();
                System.out.println("Database " + result.getString(1) + " connected successfully!");
                result.close();

            }
            userInput = getUserInput(input);
            while (userInput != 0){
                switch(userInput){
                    case 1:
                        System.out.println("\n--- All Students ---");
                        getAllStudents(conn).forEach(System.out::println);
                        break;
                    case 2:
                        userAddNewStudent(input, conn);
                        break;
                    case 3:
                        userUpdateStudentEmail(input, conn);
                        break;
                    case 4:
                        userDeleteStudent(input, conn);
                        break;
                }
                userInput = getUserInput(input);
            }
            System.out.println("Exiting Program...");
            System.out.println("Press Ctrl + C to exit. Thank You for using my program!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    Author - Brandon Wu
    Purpose - This function takes in a number that corresponds to the user's desired action
    Parameters -
        Scanner input - This is the scanner variable that will read what number the user inputs
    Return - The function returns the number the user inputted.
     */
    public static int getUserInput(Scanner input){
        int userInput = 0;
        System.out.println("\nPlease enter 1 to view the table");
        System.out.println("Please enter 2 to add a student to the table");
        System.out.println("Please enter 3 to update a student's email");
        System.out.println("Please enter 4 to delete a student from the table");
        System.out.println("Please enter 0 to exit the program");
        userInput = input.nextInt();
        while(userInput < 0 || userInput > 4){
            System.out.println("Your input '" + userInput +"' was invalid. Please try again");
            userInput = getUserInput(input);
        }
        return userInput;
    }

    /*
    Author - Brandon Wu
    Purpose - This function tells the database to return the table so it can be added to a list of strings that will be
              that will be returned to print out.
    Parameters -
        Connection conn - The variable required to query the database
    Returns - A list of all the students within the table
     */
    public static List<String> getAllStudents(Connection conn) throws SQLException {
        List<String> students = new ArrayList<>();
        String sql = "SELECT student_id, first_name, last_name, email, enrollment_date FROM students ORDER BY student_id";

        try (PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                String row = result.getInt("student_id") + " - " +
                        result.getString("first_name") + " " +
                        result.getString("last_name") + " | " +
                        result.getString("email") + " | " +
                        result.getDate("enrollment_date");

                students.add(row);
            }
        }
        return students;
    }

    /*
    Author - Brandon Wu
    Purpose - This function allows the user to input the student's information to be added to the database
    Parameters -
        Scanner input - This variable allows us to read the user's input
        Connection conn - This variable will be used by the addStudent() function to interact with the database
    Return - N/A
     */
    public static void userAddNewStudent(Scanner input, Connection conn) throws SQLException {
        input.nextLine();
        System.out.println("Enter the student's first name:");
        String firstName = input.nextLine();
        System.out.println("Enter the student's last name:");
        String lastName = input.nextLine();
        System.out.println("Enter the student's email address:");
        String email = input.nextLine();
        System.out.println("Enter the student's enrollment date(YYYY-MM-DD):");
        String enrollmentDate = input.nextLine();

        if(addStudent(conn, firstName, lastName, email, enrollmentDate)){
            System.out.println(firstName + " " + lastName + " added successfully!");
        }
    }

    /*
    Author - Brandon Wu
    Purpose - This function interacts with the database to add students into the database
    Parameters -
        Connection conn - This variable allows the function to add students to teh database
        String firstName - Student's firstname
        String lastName - Student's lastname
        String email - Student's email
        String enrollmentDate - Student's enrollmentDate
    Returns - True to signify the action has been done
     */
    public static boolean addStudent(Connection conn, String firstName, String lastName, String email, String enrollmentDate) throws SQLException {
        String sql = "INSERT INTO students (first_name, last_name, email, enrollment_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, email);
            statement.setDate(4, Date.valueOf(enrollmentDate));
            statement.executeUpdate();
            return true;
        }
    }

    /*
    Author - Brandon Wu
    Purpose - This function takes in user input to then update a student's email based on the inputted student id
    Parameters -
        Scanner input - This variable allows us to read the user's input
        Connection conn - This variable will be used by the addStudent() function to interact with the database
    Returns - N/A
     */
    public static void userUpdateStudentEmail(Scanner input, Connection conn) throws SQLException {
        input.nextLine();
        System.out.println("Enter the student's id you wish to update:");
        int studentID = input.nextInt();
        input.nextLine();
        System.out.println("Enter the new email address:");
        String newEmail = input.nextLine();

        if(updateStudentEmail(conn, studentID, newEmail)){
            System.out.println("Email address changed to " + newEmail);
        }
    }

    /*
    Author - Brandon Wu
    Purpose - This function updates a student's email based on the given parameters
    Parameters -
        Connection conn - This variable allows the function to add students to teh database
        int studentID - Student's id
        String newEmail - Student's new email
    Returns - True to signify the action has been done
     */
    public static boolean updateStudentEmail(Connection conn, int studentId, String newEmail) throws SQLException {
        String sql = "UPDATE students SET email = ? WHERE student_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, newEmail);
            statement.setInt(2, studentId);
            statement.executeUpdate();
            return true;
        }
    }

    /*
    Author - Brandon Wu
    Purpose - This function takes in user input to then delete a student from the database based on the inputted
              student id
    Parameters -
        Scanner input - This variable allows us to read the user's input
        Connection conn - This variable will be used by the addStudent() function to interact with the database
    Returns - N/A
     */
    public static void userDeleteStudent(Scanner input, Connection conn) throws SQLException {
        input.nextLine();
        System.out.println("Enter the student's id you wish to delete:");
        int studentID = input.nextInt();

        if(deleteStudent(conn, studentID)){
            System.out.println("Student deleted successfully!");
        }
    }

    /*
    Author - Brandon Wu
    Purpose - This function deletes a student from the database based on the given parameters
    Parameters -
        Connection conn - This variable allows the function to add students to teh database
        int studentID - Student's id
    Returns - True to signify the action has been done
     */
    public static boolean deleteStudent(Connection conn, int studentId) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            statement.executeUpdate();
            return true;
        }
    }
}


