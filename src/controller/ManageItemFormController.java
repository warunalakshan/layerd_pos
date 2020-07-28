/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bussiness.BusinessLogic;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
        ObservableList<ItemTM> item = tblItems.getItems();
        item.clear();
        item = FXCollections.observableArrayList(BusinessLogic.getAllItems());
        tblItems.setItems(item);
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

            boolean result = BusinessLogic.saveItem(txtCode.getText(), txtDescription.getText(), qtyOnHand, unitPrice);
            if (!result){
                new Alert(Alert.AlertType.ERROR, "Failed to save the item", ButtonType.OK).show();
            }
            btnAddNew_OnAction(event);
        } else {
            ItemTM selectedItem = tblItems.getSelectionModel().getSelectedItem();

            boolean result = BusinessLogic.updateItem(txtDescription.getText(),  qtyOnHand, unitPrice, selectedItem.getCode());
            if (!result) {
                new Alert(Alert.AlertType.ERROR, "Failed to update the Item").show();
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
            boolean result = BusinessLogic.deleteItem(selectedItem.getCode());
            if (!result) {

            } else {
                tblItems.getItems().remove(selectedItem);
                tblItems.getSelectionModel().clearSelection();
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
       txtCode.setText(BusinessLogic.getNewItemCode());

    }

}
