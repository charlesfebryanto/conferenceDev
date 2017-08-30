package com.conference.user;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Date;

public class Member {
    private int userId;
    private String firstName;
    private String lastName;
    private String gender;
    private String contactNumber;
    private String address;
    private Date dob;
    private int position;

    public Member(int userId, String firstName, String lastName, String gender, String contactNumber, String address, Date dob, int position) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.address = address;
        this.dob = dob;
        this.position = position;
    }

    public void view(Stage stage) {
        BorderPane layout = new BorderPane();

        MenuBar menuBar = new MenuBar();

        Menu profile = new Menu("Profile");
//        MenuItem edit = new MenuItem("Edit");
        MenuItem logout = new MenuItem("Log out");
        logout.setOnAction(e -> logout());
        profile.getItems().addAll(logout);

        Menu view = new Menu("View");
        MenuItem productPurchased = new MenuItem("Product Purchased");
        productPurchased.setOnAction(e -> System.out.println("prod"));
        MenuItem boothEngagement = new MenuItem("Booth Engagement");
        view.getItems().addAll(productPurchased, boothEngagement);

        menuBar.getMenus().addAll(profile, view);

        GridPane body = new GridPane();
        body.setVgap(10);
        body.setHgap(10);
        body.setPadding( new Insets(10));

        Label loginId = new Label("ID : " + getUserId());
        GridPane.setConstraints(loginId, 0,0);
        Label loginName = new Label("Name : " + getFirstName() + " " + getLastName());
        GridPane.setConstraints(loginName, 0,1);
        body.getChildren().addAll(loginId, loginName);

//        Label lbl_username = new Label("Username");
//        GridPane.setConstraints(lbl_username, 0,0);
//        TextField txt_username = new TextField();
//        txt_username.setPromptText("Username");
//        GridPane.setConstraints(txt_username, 1, 0);



        layout.setCenter(body);
        layout.setTop(menuBar);

        Scene scene = new Scene(layout, 600, 400);
        stage.setTitle("Login As : Visitor");
        stage.setScene(scene);
    }

    public void logout() {
        System.out.println("Log out");
    }

    // get and set
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
