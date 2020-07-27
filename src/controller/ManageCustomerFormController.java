/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.CustomerTM;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author ranjith-suranga
 */
public class ManageCustomerFormController implements Initializable {

    @FXML
    private Button btnSave;
    @FXML
    private Button btnDelete;
    @FXML
    private AnchorPane root;
    @FXML
    private TextField txtCustomerId;
    @FXML
    private TextField txtCustomerName;
    @FXML
    private TextField txtCustomerAddress;

    @FXML
    private TableView<CustomerTM> tblCustomers;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tblCustomers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblCustomers.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCustomers.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));

        txtCustomerId.setDisable(true);
        txtCustomerName.setDisable(true);
        txtCustomerAddress.setDisable(true);
        btnDelete.setDisable(true);
        btnSave.setDisable(true);

        loadAllCustomers();

        tblCustomers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CustomerTM>() {
            @Override
            public void changed(ObservableValue<? extends CustomerTM> observable, CustomerTM oldValue, CustomerTM newValue) {
                CustomerTM selectedItem = tblCustomers.getSelectionModel().getSelectedItem();

                if (selectedItem == null) {
                    btnSave.setText("Save");
                    btnDelete.setDisable(true);
                    txtCustomerId.clear();
                    txtCustomerName.clear();
                    txtCustomerAddress.clear();
                    return;
                }

                btnSave.setText("Update");
                btnSave.setDisable(false);
                btnDelete.setDisable(false);
                txtCustomerName.setDisable(false);
                txtCustomerAddress.setDisable(false);
                txtCustomerId.setText(selectedItem.getId());
                txtCustomerName.setText(selectedItem.getName());
                txtCustomerAddress.setText(selectedItem.getAddress());
            }
        });
    }

    private void loadAllCustomers() {
        try {
            ObservableList<CustomerTM> customers = tblCustomers.getItems();
            customers.clear();
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM Customer");
            while (rst.next()) {
                String id = rst.getString(1);
                String name = rst.getString(2);
                String address = rst.getString(3);
                customers.add(new CustomerTM(id, name, address));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToHome(MouseEvent event) throws IOException {
        URL resource = this.getClass().getResource("/view/MainForm.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.root.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    @FXML
    private void btnSave_OnAction(ActionEvent event) {
        String name = txtCustomerName.getText();
        String address = txtCustomerAddress.getText();

        // Validation
        if (name.trim().length() == 0 || address.trim().length() == 0) {
            new Alert(Alert.AlertType.ERROR, "Customer Name, Address can't be empty", ButtonType.OK).show();
            return;
        }

        if (btnSave.getText().equals("Save")) {

            try {
                PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement("INSERT INTO Customer VALUES (?,?,?)");
                pstm.setObject(1, txtCustomerId.getText());
                pstm.setObject(2, txtCustomerName.getText());
                pstm.setObject(3, txtCustomerAddress.getText());
                int affectedRows = pstm.executeUpdate();
                if (affectedRows == 0) {
                    new Alert(Alert.AlertType.ERROR, "Failed to add the customer", ButtonType.OK).show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            btnAddNew_OnAction(event);
        } else {
            CustomerTM selectedItem = tblCustomers.getSelectionModel().getSelectedItem();

            try {
                PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement("UPDATE Customer SET name=?, address=? WHERE id=?");
                pstm.setObject(1, txtCustomerName.getText());
                pstm.setObject(2, txtCustomerAddress.getText());
                pstm.setObject(3, selectedItem.getId());
                if (pstm.executeUpdate() == 0) {
                    new Alert(Alert.AlertType.ERROR, "Failed to update the customer", ButtonType.OK).show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            tblCustomers.refresh();
            btnAddNew_OnAction(event);
        }
        loadAllCustomers();
    }

    @FXML
    private void btnDelete_OnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure whether you want to delete this customer?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get() == ButtonType.YES) {
            CustomerTM selectedItem = tblCustomers.getSelectionModel().getSelectedItem();

            try {
                PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement("DELETE FROM Customer WHERE id=?");
                pstm.setObject(1, selectedItem.getId());
                if (pstm.executeUpdate() == 0) {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete the customer", ButtonType.OK).show();
                } else {
                    tblCustomers.getItems().remove(selectedItem);
                    tblCustomers.getSelectionModel().clearSelection();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void btnAddNew_OnAction(ActionEvent actionEvent) {
        txtCustomerId.clear();
        txtCustomerName.clear();
        txtCustomerAddress.clear();
        tblCustomers.getSelectionModel().clearSelection();
        txtCustomerName.setDisable(false);
        txtCustomerAddress.setDisable(false);
        txtCustomerName.requestFocus();
        btnSave.setDisable(false);

        // Generate a new id
        int maxId = 0;
        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            ResultSet rst = stm.executeQuery("SELECT id FROM Customer ORDER BY id DESC LIMIT 1");
            if (rst.next()) {
                maxId = Integer.parseInt(rst.getString(1).replace("C", ""));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        maxId = maxId + 1;
        String id = "";
        if (maxId < 10) {
            id = "C00" + maxId;
        } else if (maxId < 100) {
            id = "C0" + maxId;
        } else {
            id = "C" + maxId;
        }
        txtCustomerId.setText(id);

    }

}
