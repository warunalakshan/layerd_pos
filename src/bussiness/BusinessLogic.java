package bussiness;

import Dao.DaodataLayer;
import db.DBConnection;
import util.CustomerTM;
import util.ItemTM;
import util.OrderDetailTM;
import util.OrderTM;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BusinessLogic {

    public static  String getNewCustomerId(){
        String lastCustId = DaodataLayer.getLastCustId();
        if (lastCustId ==null){
            return "C001";
        }else{
            int maxId = Integer.parseInt(lastCustId.replace("C",""));
            maxId = maxId + 1;
            String id = "";
            if (maxId < 10) {
                id = "C00" + maxId;
            } else if (maxId < 100) {
                id = "C0" + maxId;
            } else {
                id = "C" + maxId;
            }
            return id;
        }
    }

    public static String getNewItemCode(){
        String lastitemCode = DaodataLayer.getLastItemCode();
        if (lastitemCode == null){
            return "I001";
        }else{
            int maxId=  Integer.parseInt(lastitemCode.replace("I",""));
            maxId = maxId + 1;
            String id = "";
            if (maxId < 10) {
                id = "I00" + maxId;
            } else if (maxId < 100) {
                id = "I0" + maxId;
            } else {
                id = "I" + maxId;
            }
            return id;
        }
    }

    public static String getNewOrderId(){
        String lastOrderId = DaodataLayer.getLastOrderId();
        if (lastOrderId == null){
            return "OD001";
        }else{
            int maxId=  Integer.parseInt(lastOrderId.replace("I",""));
            maxId = maxId + 1;
            String id = "";
            if (maxId < 10) {
                id = "OD00" + maxId;
            } else if (maxId < 100) {
                id = "OD0" + maxId;
            } else {
                id = "OD" + maxId;
            }
            return id;
        }
    }

    public static List<CustomerTM> getAllCustomers(){
        return DaodataLayer.getAllCustomer();
    }

    public static boolean saveCustomer(CustomerTM customer){
        return DaodataLayer.saveCustomer(customer);
    }

    public static boolean deleteCustomer(String customerId){
        return DaodataLayer.deleteCustomer(customerId);
    }

    public static boolean updateCustomer(CustomerTM customer){
        return DaodataLayer.updateCustomer(customer);
    }

    public static List<ItemTM> getAllItems(){
        return DaodataLayer.getAllItems();
    }

    public static boolean saveItem(ItemTM item){
        return DaodataLayer.saveItem(item);
    }

    public static boolean deleteItem(String itemCode) {
        return DaodataLayer.deleteItem(itemCode);
    }

    public static boolean updateItem(ItemTM item) {
        return DaodataLayer.updateItem(item);
    }

    public static boolean placeOrder(OrderTM order, List<OrderDetailTM> orderdetails){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);

            boolean result = DaodataLayer.saveOrder(order);
            if (!result){
                connection.rollback();
                return false;
            }

            result = DaodataLayer.saveOrderDetail(order.getOrderId(),orderdetails);
            if (!result){
                connection.rollback();
                return false;
            }

            result = DaodataLayer.updateQty(orderdetails);
            if (!result){
                connection.rollback();
                return false;
            }

            connection.commit();
            return true;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
