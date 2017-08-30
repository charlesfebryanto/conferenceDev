package com.conference.user;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.Date;

import static com.conference.Conference.loginScene;

public class Administrator extends Visitor {
    public Administrator(int userId, String firstName, String lastName, String gender, String contactNumber, String address, Date dob, int position) {
        super(userId, firstName, lastName, gender, contactNumber, address, dob, position);
    }

    @Override
    public void view(Stage stage) {
        BorderPane layout = new BorderPane();

        MenuBar menuBar = new MenuBar();

        Menu profile = new Menu("Profile");
        MenuItem edit = new MenuItem("Edit");
        MenuItem logout = new MenuItem("Log out");
        logout.setOnAction(e -> logout(stage, loginScene));

        profile.getItems().addAll(edit, logout);

        Menu view = new Menu("View");
        MenuItem staff = new MenuItem("Retailer");
        MenuItem product = new MenuItem("Product");
        MenuItem stall = new MenuItem("Retailer");
        MenuItem booth = new MenuItem("Booth");
        MenuItem lecture = new MenuItem("Lecture");
        MenuItem visitor = new MenuItem("Visitor");

        view.getItems().addAll(staff, product, stall, booth, lecture, visitor);

        menuBar.getMenus().addAll(profile, view);

        GridPane body = new GridPane();
        body.setVgap(10);
        body.setHgap(10);
        body.setPadding( new Insets(10));

        Label loginId = new Label("ID : " + getUserId());
        GridPane.setConstraints(loginId, 0,0);

        Label loginName = new Label("Name : " + getFirstName() + " " + getLastName());
        GridPane.setConstraints(loginName, 0,1);

        Label loginLevel = new Label("Login Level : " + getPosition());
        GridPane.setConstraints(loginLevel, 0,2);

        body.getChildren().addAll(loginId, loginName, loginLevel);

        layout.setCenter(body);
        layout.setTop(menuBar);

        Scene scene = new Scene(layout, 800, 480);
        stage.setTitle("Login As : Administrator");
        stage.setScene(scene);
    }
}
