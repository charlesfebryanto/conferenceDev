package com.conference.user;

import com.conference.company.Company;
import com.conference.DialogBox;
import com.conference.Lecture;
import com.conference.MySqlConnect;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.*;

import static com.conference.Conference.loginScene;

public class Receptionist extends Member {
    private Connection cn = null;
    private PreparedStatement pst = null;
    private Statement st = null;
    private ResultSet rs = null;

    private ToggleGroup genderGroup;
    private RadioButton maleRadio, femaleRadio;
    private DatePicker dobPicker;
    private Label firstName, lastName, gender, contactNo, address, memberId, dob;
    private TextField firstNameField, lastNameField, contactField, memberIdField, idScanner;
    private TextArea addressField;
    private ToggleGroup engagementGroup;
    private RadioButton lectureEngagement, boothEngagement;
    private ComboBox<String> selectionBox;
    private ObservableList<Lecture> lectures;
    private ObservableList<Company> companies;

    public Receptionist(int userId, String firstName, String lastName, String gender, String contactNumber, String address, Date dob, int position) {
        super(userId, firstName, lastName, gender, contactNumber, address, dob, position);
    }

    @Override
    public void view(Stage stage) {
        BorderPane layout = new BorderPane();

        MenuBar menuBar = new MenuBar();

        Menu profile = new Menu("Profile");
        MenuItem logout = new MenuItem("Log out");
        logout.setOnAction(e -> logout(stage, loginScene));

        profile.getItems().addAll(logout);

        Menu manage = new Menu("Manage");
        MenuItem registerVisitor = new MenuItem("Register Visitor");
        registerVisitor.setOnAction(e -> layout.setCenter(registerVisitorView()));
        MenuItem addVisitorEngagement = new MenuItem("Add Visitor Engagement");
        addVisitorEngagement.setOnAction(e -> layout.setCenter(addVisitorView()));
        manage.getItems().addAll(registerVisitor, addVisitorEngagement);

        menuBar.getMenus().addAll(profile, manage);

        layout.setCenter(mainView());
        layout.setTop(menuBar);

        Scene scene = new Scene(layout, 1024, 768);
        stage.setTitle("Login As : Receptionist");
        stage.setScene(scene);
    }

    private GridPane registerVisitorView() {
        GridPane body = new GridPane();
        body.setVgap(10);
        body.setHgap(10);
        body.setPadding(new Insets(10));

        firstName = new Label("First Name : ");
        GridPane.setConstraints(firstName, 0, 0);
        firstNameField = new TextField();
        firstNameField.setPromptText("Insert First Name");
        GridPane.setConstraints(firstNameField, 1, 0);
        firstNameField.textProperty().addListener(e ->
                DialogBox.stringOnly(firstNameField, 20,
                        "Warning", "First Name is Too Long"));

        lastName = new Label("Last Name : ");
        GridPane.setConstraints(lastName, 0, 1);
        lastNameField = new TextField();
        lastNameField.setPromptText("Insert Last Name");
        GridPane.setConstraints(lastNameField, 1, 1);
        lastNameField.textProperty().addListener(e ->
                DialogBox.stringOnly(lastNameField, 20,
                        "Warning", "Last Name is Too Long"));

        gender = new Label("Gender : ");
        GridPane.setConstraints(gender, 0, 2);
        genderGroup = new ToggleGroup();
        maleRadio = new RadioButton("Male");
        GridPane.setConstraints(maleRadio, 1, 2);
        maleRadio.setToggleGroup(genderGroup);
        femaleRadio = new RadioButton("Female");
        GridPane.setConstraints(femaleRadio, 2, 2);
        femaleRadio.setToggleGroup(genderGroup);
        maleRadio.setSelected(true);


        contactNo = new Label("Contact Number : ");
        GridPane.setConstraints(contactNo, 0, 3);
        contactField = new TextField();
        contactField.setPromptText("Insert Contact Number");
        GridPane.setConstraints(contactField, 1, 3);
        contactField.textProperty().addListener(e ->
                DialogBox.numberOnly(contactField, 20,
                        "Warning", "Contact is Too Long"));

        address = new Label("Address : ");
        GridPane.setConstraints(address, 0, 4);
        addressField = new TextArea();
        addressField.setPromptText("Insert Address");
        addressField.setMaxWidth(300);
        addressField.setMaxHeight(100);
        GridPane.setConstraints(addressField, 1, 4);
        addressField.setWrapText(true);
        addressField.textProperty().addListener(e ->
                DialogBox.lengthCheck(addressField, 80,
                        "Warning", "Address is Too Long"));

        dob = new Label("Date of Birth : ");
        GridPane.setConstraints(dob, 0, 5);
        dobPicker = new DatePicker();
        GridPane.setConstraints(dobPicker, 1, 5);


        // maybe need to be removed
//        position = new Label("Position : ");
//        GridPane.setConstraints(position, 0, 6);
//        ComboBox<String> positionBox = new ComboBox<>();
//        positionBox.getItems().addAll("Visitor", "Retailer", "Receptionist", "Administrator");
//        positionBox.setPromptText("Select Position");
//            System.out.println(positionBox.getSelectionModel().getSelectedIndex());
//        GridPane.setConstraints(positionBox, 1, 6);


        memberId = new Label("ID : ");
        GridPane.setConstraints(memberId, 0, 6);
        memberIdField = new TextField();
        memberIdField.setPromptText("Scan Member ID");
        GridPane.setConstraints(memberIdField, 1, 6);
        memberIdField.textProperty().addListener(e -> {
            if(DialogBox.numberOnly(memberIdField) && memberIdField.getText().length() >= 7) {
                    insertVisitor();
            }
        });

        Button addMember = new Button("Add Member");
        GridPane.setConstraints(addMember, 1, 7);
        addMember.setOnAction(e -> insertVisitor());

        body.getChildren().addAll(firstName, lastName, gender, contactNo, address, dob);
        body.getChildren().addAll(firstNameField, lastNameField, maleRadio, femaleRadio, contactField, addressField,
                dobPicker, memberId, memberIdField, addMember);

        return body;
    }

    private GridPane addVisitorView() {
        GridPane body = new GridPane();
        body.setVgap(10);
        body.setHgap(10);
        body.setPadding(new Insets(10));

        Label engagementBy = new Label("Select type : ");
        GridPane.setConstraints(engagementBy, 0, 0);
        engagementGroup = new ToggleGroup();
        lectureEngagement = new RadioButton("Lecture");
        GridPane.setConstraints(lectureEngagement, 1, 0);
        lectureEngagement.setToggleGroup(engagementGroup);
        boothEngagement = new RadioButton("Booth");
        GridPane.setConstraints(boothEngagement, 2, 0);
        boothEngagement.setToggleGroup(engagementGroup);
        lectureEngagement.setSelected(true);
        engagementGroup.selectedToggleProperty().addListener(e -> engagementSelectionSwitch());

        // make a combobox that connected to database and get list of lecture / following the type of radio
        Label selectionLabel = new Label("Select");
        GridPane.setConstraints(selectionLabel, 0, 1);
        selectionBox = new ComboBox<>();
        GridPane.setConstraints(selectionBox, 1, 1);
        selectionBox.setMinWidth(250);
        engagementSelectionSwitch();

        idScanner = new TextField();
        idScanner.setPromptText("Scan ID Tag");
        GridPane.setConstraints(idScanner, 1, 2);
        idScanner.textProperty().addListener(e -> {
            if (idScanner.getText().length() >= 7) {
                insertEngagement();
            } else {
                DialogBox.numberOnly(idScanner);
            }
        });

        Button saveEngagement = new Button("Save");
        GridPane.setConstraints(saveEngagement, 1, 3);
        saveEngagement.setOnAction(e -> insertEngagement());

        body.getChildren().addAll(engagementBy, lectureEngagement, boothEngagement, selectionLabel, selectionBox, idScanner, saveEngagement);
        return body;
    }

    private void insertVisitor() {
        if (firstNameField.getText().isEmpty()) {
            DialogBox.alertBox("Warning", "First Name is Empty");
        } else if (lastNameField.getText().isEmpty()) {
            DialogBox.alertBox("Warning", "Last Name is Empty");
        } else if (contactField.getText().isEmpty()) {
            DialogBox.alertBox("Warning", "Contact is Empty");
        } else if (addressField.getText().isEmpty()) {
            DialogBox.alertBox("Warning", "Address is Empty");
        } else if (dobPicker.getEditor().getText().isEmpty()) {
            DialogBox.alertBox("Warning", "Date is Empty");
        } else if (memberIdField.getText().isEmpty()) {
            DialogBox.alertBox("Warning", "ID is Empty");
        } else if (memberIdField.getText().length() < 7) {
            DialogBox.alertBox("Warning", "ID is Too Short");
        } else {
            String selectedGender;
            if (genderGroup.getSelectedToggle() == maleRadio) {
                selectedGender = maleRadio.getText().charAt(0) + "";
            } else {
                selectedGender = femaleRadio.getText().charAt(0) + "";
            }
            try {
                cn = MySqlConnect.connectDB();
                String sql = "INSERT INTO member VALUES(?,?,?,?,?,?,?,?)";
                pst = cn.prepareStatement(sql);
                pst.setString(1, memberIdField.getText());
                pst.setString(2, firstNameField.getText());
                pst.setString(3, lastNameField.getText());
                pst.setString(4, selectedGender);
                pst.setString(5, contactField.getText());
                pst.setString(6, addressField.getText());
                pst.setDate(7, Date.valueOf(dobPicker.getValue()));
                pst.setInt(8, 0);
                pst.executeUpdate();
                DialogBox.alertBox("Success", "Visitor " + firstNameField.getText() + " " + lastNameField.getText() + " Successfuly added.");
                Platform.runLater(() -> {
                    firstNameField.clear();
                    lastNameField.clear();
                    maleRadio.setSelected(true);
                    contactField.clear();
                    addressField.clear();
                    dobPicker.getEditor().clear();
                    memberIdField.clear();
                });
            } catch (MySQLIntegrityConstraintViolationException e) {
                if (e.getErrorCode() == 1062) {
                    DialogBox.alertBox("Error", memberIdField.getText() + " Already Registered.");
                    Platform.runLater(() -> memberIdField.clear());
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "");
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (Exception e) {
                    DialogBox.alertBox("Error", e + "rs");
                }
                try {
                    if (st != null) {
                        st.close();
                    }
                } catch (Exception e) {
                    DialogBox.alertBox("Error", e + "st");
                }
                try {
                    if (pst != null) {
                        pst.close();
                    }
                } catch (Exception e) {
                    DialogBox.alertBox("Error", e + "st");
                }
                try {
                    if (cn != null) {
                        cn.close();
                    }
                } catch (Exception e) {
                    DialogBox.alertBox("Error", e + "cn");
                }
            }
        }
    }

    private void insertEngagement() {
        String tableName = "";
        String column1 = "";
        if(engagementGroup.getSelectedToggle() == lectureEngagement) {
            if(selectionBox.getSelectionModel().getSelectedIndex() == -1) {
                DialogBox.alertBox("Warning", "Select Lecture First");
            } else {
                tableName = "attend";
                column1 = lectures.get(selectionBox.getSelectionModel().getSelectedIndex()).getLectureId();
            }
        } else if (engagementGroup.getSelectedToggle() == boothEngagement) {
            if (selectionBox.getSelectionModel().getSelectedIndex() == -1) {
                DialogBox.alertBox("Warning", "Select Booth First");
            } else {
                tableName = "engage";
                column1 = companies.get(selectionBox.getSelectionModel().getSelectedIndex()).getCompanyId();
            }
        }
        if(idScanner.getText().isEmpty()) {
            DialogBox.alertBox("Warning", "ID is Empty");
        } else {
            try {
                cn = MySqlConnect.connectDB();
                String sql = "INSERT INTO " + tableName + " VALUES(?,?)";
                pst = cn.prepareStatement(sql);
                pst.setString(1, idScanner.getText());
                pst.setString(2, column1);
                pst.executeUpdate();
                DialogBox.alertBox("Success", idScanner.getText() + " Successfuly Recorded with " + selectionBox.getSelectionModel().getSelectedItem());
            } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException e) {
                if(e.getErrorCode() == 1062) {
                    DialogBox.alertBox("Error", idScanner.getText() + " already attend/engage the current booth/lecture");
                } else if(e.getErrorCode() == 1452) {
                    DialogBox.alertBox("Error", idScanner.getText() + " is not in the Database");
                } else {
                    DialogBox.alertBox("Error", e + " Error code : " + e.getErrorCode());
                }
            } catch(Exception e) {
                DialogBox.alertBox("Error", e + "");
            } finally {
                try {
                    if(rs != null) {
                        rs.close();
                    }
                } catch (Exception e) {
                    DialogBox.alertBox("Error", e + "rs");
                }
                try {
                    if(st != null) {
                        st.close();
                    }
                } catch (Exception e) {
                    DialogBox.alertBox("Error", e + "st");
                }
                try {
                    if(pst != null) {
                        pst.close();
                    }
                } catch (Exception e) {
                    DialogBox.alertBox("Error", e + "st");
                }
                try {
                    if(cn != null) {
                        cn.close();
                    }
                } catch (Exception e) {
                    DialogBox.alertBox("Error", e + "cn");
                }
            }
        }
        Platform.runLater(() -> idScanner.clear());
    }

    private void engagementSelectionSwitch() {
        selectionBox.getItems().remove(0, selectionBox.getItems().size());
        if (engagementGroup.getSelectedToggle() == lectureEngagement) {
            selectionBox.setPromptText("Select Lecture");
            lectures = FXCollections.observableArrayList();
            try {
                cn = MySqlConnect.connectDB();
                String sql = "SELECT lecture.lectureId, lecture.title, room.roomId, lecture.date " +
                        "FROM lecture, room, occupy " +
                        "WHERE lecture.lectureId = occupy.lectureId " +
                        "AND occupy.roomId = room.roomId";

                st = cn.createStatement();
                rs = st.executeQuery(sql);

                int i = 0;
                while(rs.next()) {
                    lectures.add(new Lecture(rs.getString(1), rs.getString(2), rs.getString(3), rs.getDate(4)));
                    selectionBox.getItems().add(lectures.get(i).getLectureId() + " - " + lectures.get(i).getTitle());
                    i++;
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "");
            } finally {
                try {
                    if(rs != null) {
                        rs.close();
                    }
                } catch (Exception e) {
                    DialogBox.alertBox("Error", e + "rs");
                }
                try {
                    if(st != null) {
                        st.close();
                    }
                } catch (Exception e) {
                    DialogBox.alertBox("Error", e + "st");
                }
                try {
                    if(pst != null) {
                        pst.close();
                    }
                } catch (Exception e) {
                    DialogBox.alertBox("Error", e + "st");
                }
                try {
                    if(cn != null) {
                        cn.close();
                    }
                } catch (Exception e) {
                    DialogBox.alertBox("Error", e + "cn");
                }
            }
        } else {
            selectionBox.setPromptText("Select Booth");
            companies = FXCollections.observableArrayList();
            try {
                cn = MySqlConnect.connectDB();
                String sql = "SELECT company.companyId, company.name FROM company";

                st = cn.createStatement();
                rs = st.executeQuery(sql);

                int i = 0;
                while(rs.next()) {
                    companies.add(new Company(rs.getString(1), rs.getString(2)));
                    selectionBox.getItems().add(companies.get(i).getCompanyId() + " - " + companies.get(i).getName());
                    i++;
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "");
            } finally {
                try {
                    if(rs != null) {
                        rs.close();
                    }
                } catch (Exception e) {
                    DialogBox.alertBox("Error", e + "rs");
                }
                try {
                    if(st != null) {
                        st.close();
                    }
                } catch (Exception e) {
                    DialogBox.alertBox("Error", e + "st");
                }
                try {
                    if(pst != null) {
                        pst.close();
                    }
                } catch (Exception e) {
                    DialogBox.alertBox("Error", e + "st");
                }
                try {
                    if(cn != null) {
                        cn.close();
                    }
                } catch (Exception e) {
                    DialogBox.alertBox("Error", e + "cn");
                }
            }
        }
    }
}