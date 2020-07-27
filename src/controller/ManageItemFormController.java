/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.jfoenix.controls.JFXTextField;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.ItemTM;

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
public class ManageItemFormController implements Initializable {

    public JFXTextField txtCode;
    public JFXTextField txtDescription;
    public JFXTextField txtQtyOnHand;
    public TableView<ItemTM> tblItems;
    public JFXTextField txtUnitPrice;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnDelete;
    @FXML
    private AnchorPane root;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tblItems.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("code"));
        tblItems.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblItems.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        tblItems.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        txtCode.setDisable(true);
        txtDescription.setDisable(true);
        txtQtyOnHand.setDisable(true);
        txtUnitPrice.setDisable(true);
        btnDelete.setDisable(true);
        btnSave.setDisable(true);

        loadAllItems();

        tblItems.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ItemTM>() {
            @Override
            public void changed(ObservableValue<? extends ItemTM> observable, ItemTM oldValue, ItemTM newValue) {
                ItemTM selectedItem = tblItems.getSelectionModel().getSelectedItem();

                if (selectedItem == null) {
                    btnSave.setText("Save");
                    btnDelete.setDisable(true);
                    txtCode.clear();
                    txtDescription.clear();
                    txtQtyOnHand.clear();
                    txtUnitPrice.clear();
                    return;
                }

                btnSave.setText("Update");
                btnSave.setDisable(false);
                btnDelete.setDisable(false);
                txtDescription.setDisable(false);
                txtQtyOnHand.setDisable(false);
                txtUnitPrice.setDisable(false);
                txtCode.setText(selectedItem.getCode());
                txtDescription.setText(selectedItem.getDescription());
                txtQtyOnHand.setText(selectedItem.getQtyOnHand() + "");
                txtUnitPrice.setText(selectedItem.getUnitPrice() + "");

            }
        });
    }

    private void loadAllItems() {
        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM Item");
            ObservableList<ItemTM> items = tblItems.getItems();
            items.clear();
            while (rst.next()) {
                String code = rst.getString(1);
                String description = rst.getString(2);
                double unitPrice = rst.getDouble(3);
                int qtyOnHand = rst.getInt(4);
                items.add(new ItemTM(code, description, qtyOnHand, unitPrice));
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

        if (txtDescription.getText().trim().isEmpty() ||
                txtQtyOnHand.getText().trim().isEmpty() ||
                txtUnitPrice.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Description, Qty. on Hand or Unit Price can't be empty").show();
            return;
        }

        int qtyOnHand = Integer.parseInt(txtQtyOnHand.getText().trim());
        double unitPrice = Double.parseDouble(txtUnitPrice.getText().trim());

        if (qtyOnHand < 0 || unitPrice <= 0) {
            new Alert(Alert.AlertType.ERROR, "Invalid Qty. or UnitPrice").show();
            return;
        }

        if (btnSave.getText().equals("Save")) {

            try {
                PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement("INSERT INTO Item VALUES (?,?,?,?)");
                pstm.setObject(1, txtCode.getText());
                pstm.setObject(2, txtDescription.getText());
                pstm.setObject(3, qtyOnHand);
                pstm.setObject(4, unitPrice);
                if (pstm.executeUpdate() == 0) {
                    new Alert(Alert.AlertType.ERROR, "Failed to save the item", ButtonType.OK).show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            btnAddNew_OnAction(event);
        } else {
            ItemTM selectedItem = tblItems.getSelectionModel().getSelectedItem();

            try {
                PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement("UPDATE Item SET description=?, unitPrice=?, qtyOnHand=? WHERE code=?");
                pstm.setObject(1, txtDescription.getText());
                pstm.setObject(2, unitPrice);
                pstm.setObject(3, qtyOnHand);
                pstm.setObject(4, selectedItem.getCode());
                if (pstm.executeUpdate() == 0) {
                    new Alert(Alert.AlertType.ERROR, "Failed to update the Item").show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


            tblItems.refresh();
            btnAddNew_OnAction(event);
        }
        loadAllItems();
    }

    @FXML
    private void btnDelete_OnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure whether you want to delete this item?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get() == ButtonType.YES) {
            ItemTM selectedItem = tblItems.getSelectionModel().getSelectedItem();
            try {
                PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement("DELETE FROM Item WHERE code=?");
                pstm.setObject(1, selectedItem.getCode());
                if (pstm.executeUpdate() == 0) {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete the item", ButtonType.OK).show();
                } else {
                    tblItems.getItems().remove(selectedItem);
                    tblItems.getSelectionModel().clearSelection();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void btnAddNew_OnAction(ActionEvent actionEvent) {
        txtCode.clear();
        txtDescription.clear();
        txtQtyOnHand.clear();
        txtUnitPrice.clear();
        tblItems.getSelectionModel().clearSelection();
        txtDescription.setDisable(false);
        txtQtyOnHand.setDisable(false);
        txtUnitPrice.setDisable(false);
        txtDescription.requestFocus();
        btnSave.setDisable(false);

        // Generate a new id
        int maxCode = 0;
        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            ResultSet rst = stm.executeQuery("SELECT code FROM Item ORDER BY code DESC LIMIT 1");
            if (rst.next()) {
                maxCode = Integer.parseInt(rst.getString(1).replace("I", ""));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        maxCode = maxCode + 1;
        String code = "";
        if (maxCode < 10) {
            code = "I00" + maxCode;
        } else if (maxCode < 100) {
            code = "I0" + maxCode;
        } else {
            code = "I" + maxCode;
        }
        txtCode.setText(code);

    }

}
