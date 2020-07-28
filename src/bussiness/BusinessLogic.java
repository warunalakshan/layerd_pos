package bussiness;

import Dao.CustomerDAO;
import Dao.ItemDAO;
import Dao.OrderDAO;
import Dao.OrderDetailDAO;
import db.DBConnection;
import entity.Customer;
import entity.Item;
import entity.Order;
import entity.OrderDetail;
import util.CustomerTM;
import util.ItemTM;
import util.OrderDetailTM;
import util.OrderTM;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BusinessLogic {

    public static  String getNewCustomerId(){
        String oldId = CustomerDAO.getLastCustomerId();
        oldId = oldId.substring(2,5);

        int maxId = Integer.parseInt(oldId) + 1;

        if (maxId < 10) {
            return  "C00" + maxId;
        } else if (maxId < 100) {
            return  "C0" + maxId;
        } else {
            return  "C" + maxId;
        }
    }

    public static String getNewItemCode(){
        String oldId = ItemDAO.getLastItemCode();
        oldId = oldId.substring(2,5);

        int maxId = Integer.parseInt(oldId) + 1;

        if (maxId < 10) {
            return  "I00" + maxId;
        } else if (maxId < 100) {
            return  "I0" + maxId;
        } else {
            return  "I" + maxId;
        }
    }

    public static String getNewOrderId(){

        String oldId = OrderDetailDAO.getLastOrderId();
        oldId = oldId.substring(2,5);

        int maxId = Integer.parseInt(oldId) + 1;

            if (maxId < 10) {
                return  "OD00" + maxId;
            } else if (maxId < 100) {
                return  "OD0" + maxId;
            } else {
                return  "OD" + maxId;
            }
        }

    public static List<CustomerTM> getAllCustomers(){
        List<Customer> allCustomers = CustomerDAO.findAllCustomers();
        List<CustomerTM> customers = new ArrayList<>();
        for (Customer customer : allCustomers) {
            customers.add(new CustomerTM(customer.getId(),customer.getName(), customer.getAddress()));
        }
        return customers;
    }

    public static boolean saveCustomer( String id, String name, String address){
        return CustomerDAO.saveCustomer(new Customer(id, name, address));
    }

    public static boolean deleteCustomer(String customerId){
        return CustomerDAO.deleteCustomer(customerId);
    }

    public static boolean updateCustomer(String name, String address, String customerId){
        return CustomerDAO.updateCustomer(new Customer(customerId, name, address));
    }

    public static List<ItemTM> getAllItems(){
        List<Item> allItems = ItemDAO.findAllItems();
        List<ItemTM> items = new ArrayList<>();
        for (Item item : allItems) {
            items.add(new ItemTM(item.getCode(),item.getDescription(),item.getQtyOnHand(),item.getUnitPrice().doubleValue()));
        }
        return items;
    }

    public static boolean saveItem(String code, String description, int qtyOnHand, double unitPrice){
        return ItemDAO.saveItem(new Item(code, description, BigDecimal.valueOf(unitPrice), qtyOnHand));
    }

    public static boolean deleteItem(String itemCode) {
        return ItemDAO.deleteItem(itemCode);
    }

    public static boolean updateItem(String description, int qtyOnHand, double unitPrice, String itemCode) {
        return ItemDAO.updateItem(new Item(itemCode, description,
                BigDecimal.valueOf(unitPrice), qtyOnHand));
    }

    public static boolean placeOrder(OrderTM order, List<OrderDetailTM> orderDetails) {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);
            boolean result = OrderDAO.saveOrder(new Order(order.getOrderId(),
                    Date.valueOf(order.getOrderDate()),
                    order.getCustomerId()));
            if (!result) {
                connection.rollback();
                return false;
            }
            for (OrderDetailTM orderDetail : orderDetails) {
                result = OrderDetailDAO.saveOrderDetail(new OrderDetail(
                        order.getOrderId(), orderDetail.getCode(),
                        orderDetail.getQty(), BigDecimal.valueOf(orderDetail.getUnitPrice())
                ));
                if (!result){
                    connection.rollback();
                    return false;
                }
                Item item = ItemDAO.findItem(orderDetail.getCode());
                item.setQtyOnHand(item.getQtyOnHand() - orderDetail.getQty());
                result = ItemDAO.updateItem(item);
                if (!result){
                    connection.rollback();
                    return false;
                }
            }
            connection.commit();
            return true;
        } catch (Throwable throwables) {
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
