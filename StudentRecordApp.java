package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;

// ---------------- Student class ----------------
class Student {
    String name;
    int rollNo;
    String course;

    Student(String name, int rollNo, String course) {
        this.name = name;
        this.rollNo = rollNo;
        this.course = course;
    }

    @Override
    public String toString() {
        return rollNo + " - " + name + " (" + course + ")";
    }
}

// ---------------- Node for Linked List ----------------
class Node {
    Student student;
    Node next;

    Node(Student student) {
        this.student = student;
        this.next = null;
    }
}

// ---------------- Custom Linked List ----------------
class CustomLinkedList {
    private Node head;

    // Add student
    public void add(Student student) {
        Node newNode = new Node(student);
        if (head == null) {
            head = newNode;
        } else {
            Node temp = head;
            while (temp.next != null) temp = temp.next;
            temp.next = newNode;
        }
    }

    // Delete student by roll no
    public boolean delete(int rollNo) {
        if (head == null) return false;
        if (head.student.rollNo == rollNo) {
            head = head.next;
            return true;
        }
        Node temp = head;
        while (temp.next != null) {
            if (temp.next.student.rollNo == rollNo) {
                temp.next = temp.next.next;
                return true;
            }
            temp = temp.next;
        }
        return false;
    }

    // Update student by roll no
    public boolean update(int rollNo, String newName, String newCourse) {
        Node temp = head;
        while (temp != null) {
            if (temp.student.rollNo == rollNo) {
                temp.student.name = newName;
                temp.student.course = newCourse;
                return true;
            }
            temp = temp.next;
        }
        return false;
    }

    // Get all students
    public String getAllStudents() {
        StringBuilder sb = new StringBuilder();
        Node temp = head;
        while (temp != null) {
            sb.append(temp.student.toString()).append("\n");
            temp = temp.next;
        }
        return sb.toString();
    }

    // Save student list to file
    public void saveToFile(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        Node temp = head;
        while (temp != null) {
            writer.write(temp.student.toString() + "\n");
            temp = temp.next;
        }
        writer.close();
    }
}

// ---------------- Main App with Login ----------------
public class StudentRecordApp extends Application {
    CustomLinkedList studentList = new CustomLinkedList();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login - Student Record System");

        // ---------- Login UI ----------
        Label loginLabel = new Label("Login to Student Record System");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        Button loginBtn = new Button("Login");
        Label loginMsg = new Label();

        VBox loginLayout = new VBox(10, loginLabel, usernameField, passwordField, loginBtn, loginMsg);
        loginLayout.setPadding(new Insets(20));
        loginLayout.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        Scene loginScene = new Scene(loginLayout, 400, 250);

        // ---------- Student Record UI ----------
        TextField nameField = new TextField();
        nameField.setPromptText("Enter Name");

        TextField rollField = new TextField();
        rollField.setPromptText("Enter Roll No");

        TextField courseField = new TextField();
        courseField.setPromptText("Enter Course");

        Button addBtn = new Button("Add Student");
        Button delBtn = new Button("Delete Student");
        Button viewBtn = new Button("View Students");
        Button updateBtn = new Button("Update Student");
        Button saveBtn = new Button("Save to File");

        TextArea displayArea = new TextArea();
        displayArea.setEditable(false);

        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(
                new Label("Student Record Management System"),
                nameField, rollField, courseField,
                new HBox(10, addBtn, delBtn, updateBtn, viewBtn, saveBtn),
                displayArea
        );
        mainLayout.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        Scene mainScene = new Scene(mainLayout, 600, 450);

        // ---------- Login Button Action ----------
        loginBtn.setOnAction(e -> {
            String user = usernameField.getText();
            String pass = passwordField.getText();

            if (user.equals("admin") && pass.equals("1234")) {
                primaryStage.setScene(mainScene);
            } else {
                loginMsg.setText("❌ Invalid Username or Password!");
            }
        });

        // ---------- Add Student ----------
        addBtn.setOnAction(e -> {
            try {
                String name = nameField.getText();
                int roll = Integer.parseInt(rollField.getText());
                String course = courseField.getText();

                studentList.add(new Student(name, roll, course));
                displayArea.setText("✅ Student Added Successfully!\n\n" + studentList.getAllStudents());

                nameField.clear();
                rollField.clear();
                courseField.clear();
            } catch (Exception ex) {
                displayArea.setText("⚠ Error: Enter valid input!");
            }
        });

        // ---------- Delete Student ----------
        delBtn.setOnAction(e -> {
            try {
                int roll = Integer.parseInt(rollField.getText());
                boolean deleted = studentList.delete(roll);

                if (deleted) {
                    displayArea.setText("🗑 Student Deleted!\n\n" + studentList.getAllStudents());
                } else {
                    displayArea.setText("⚠ No student found with Roll No " + roll);
                }
                rollField.clear();
            } catch (Exception ex) {
                displayArea.setText("⚠ Enter valid Roll No to delete!");
            }
        });

        // ---------- Update Student ----------
        updateBtn.setOnAction(e -> {
            try {
                int roll = Integer.parseInt(rollField.getText());
                String newName = nameField.getText();
                String newCourse = courseField.getText();

                boolean updated = studentList.update(roll, newName, newCourse);
                if (updated) {
                    displayArea.setText("✏ Student Updated!\n\n" + studentList.getAllStudents());
                } else {
                    displayArea.setText("⚠ No student found with Roll No " + roll);
                }
                nameField.clear();
                rollField.clear();
                courseField.clear();
            } catch (Exception ex) {
                displayArea.setText("⚠ Enter valid input for update!");
            }
        });

        // ---------- View Students ----------
        viewBtn.setOnAction(e -> displayArea.setText(studentList.getAllStudents()));

        // ---------- Save Students to File ----------
        saveBtn.setOnAction(e -> {
            try {
                studentList.saveToFile("students.txt");
                displayArea.setText("💾 Students Saved to students.txt\n\n" + studentList.getAllStudents());
            } catch (IOException ex) {
                displayArea.setText("⚠ Error saving file!");
            }
        });

        // Show login scene first
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
