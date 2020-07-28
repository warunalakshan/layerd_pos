package Dao;

import db.DBConnection;
import util.CustomerTM;
import util.ItemTM;
import util.OrderDetailTM;
import util.OrderTM;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DaodataLayer {
//==========================customer================

    public  static String getLastCustId(){
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from Customer order by id desc limit 1");
            if (resultSet.next()) {
                return resultSet.getString(1);
            } else {
                return null;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public static String getLastItemCode(){
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from Item order by code desc limit 1");
            if (resultSet.next()){
                return resultSet.getString(1);
            }else{
                return null;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public static String getLastOrderId(){
        try{
            Connection connection = DBConnection.getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from `Order` order by code desc limit 1");
            if (resultSet.next()){
                return resultSet.getString(1);
            }else {
                return null;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public static List<CustomerTM> getAllCustomer() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet resultSet = stm.executeQuery("select * from Customer");
            ArrayList<CustomerTM> customers = new ArrayList<>();
            while (resultSet.next()) {
                customers.add(new CustomerTM(resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3)));
            }
            return customers;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;

    }

    public static boolean saveCustomer(CustomerTM customer) {

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement("insert into Customer values(?,?,?)");
            ps.setObject(1, customer.getId());
            ps.setObject(2, customer.getName());
            ps.setObject(3, customer.getAddress());
            return ps.executeUpdate() > 0;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

    }

    public static boolean deleteCustomer(String customerId) {

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(" delete from Customer where id+?");
            ps.setObject(1, customerId);
            return ps.executeUpdate() > 0;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public static boolean updateCustomer(CustomerTM customer) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement("update Customer set name=?, address=? where id=?");
            ps.setObject(1, customer.getName());
            ps.setObject(2, customer.getAddress());
            ps.setObject(3, customer.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

// ========================Item=========================

    public static List<ItemTM> getAllItems() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet resultSet = stm.executeQuery("select * from Item");
            ArrayList<ItemTM> items = new ArrayList<>();
            while (resultSet.next()) {
                items.add(new ItemTM(resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        resultSet.getDouble(4)));
            }
            return items;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;

    }

    public static boolean saveItem(ItemTM item) {

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement("insert into Item values(?,?,?,?)");
            ps.setObject(1, item.getCode());
            ps.setObject(2, item.getDescription());
            ps.setObject(3, item.getQtyOnHand());
            ps.setObject(4,item.getUnitPrice());
            return ps.executeUpdate() > 0;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

    }

    public static boolean deleteItem(String itemCode) {

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(" delete from Item where id+?");
            ps.setObject(1, itemCode);
            return ps.executeUpdate() > 0;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public static boolean updateItem(ItemTM item) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement("update Item set description=?, qtyOnHand=?, unitPrice=? where code=?");
            ps.setObject(1, item.getDescription());
            ps.setObject(2, item.getQtyOnHand());
            ps.setObject(3, item.getUnitPrice());
            ps.setObject(4,item.getCode());
            return ps.executeUpdate() > 0;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

//    =======================Place Order==================================

    public static boolean placeOrder(OrderTM order , List<OrderDetailTM> orderDetails){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement("insert into `Order` values(?,?,?) ");
            ps.setObject(1,order.getOrderId());
            ps.setObject(2,order.getOrderDate());
            ps.setObject(3,order.getCustomerId());
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0){
                connection.rollback();
                return false;
            }
            for (OrderDetailTM orderDetail : orderDetails){
                ps = connection.prepareStatement("insert into OrderDetail values(?,?,?,?)");
                ps.setObject(1, order.getOrderId());
                ps.setObject(2, orderDetail.getCode());
                ps.setObject(3,orderDetail.getQty());
                ps.setObject(4,orderDetail.getUnitPrice());
                affectedRows = ps.executeUpdate();

                if (affectedRows == 0){
                    connection.rollback();
                    return false;
                }
                ps = connection.prepareStatement("update Item set qtyOnHand =? qtyOnHand - ? where code = ?");
                ps.setObject(1,orderDetail.getQty());
                affectedRows = ps.executeUpdate();
                if (affectedRows == 0){
                    connection.rollback();
                    return false;
                }
            }
            connection.commit();
            return true;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try{
                connection.rollback();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return false;
        }finally{
            try {
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
    public static boolean saveOrder(OrderTM order){
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("INSERT INTO `Order` VALUES (?,?,?)");
            pstm.setObject(1, order.getOrderId());
            pstm.setObject(2, order.getOrderDate());
            pstm.setObject(3, order.getCustomerId());
            return pstm.executeUpdate()> 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public static boolean saveOrderDetail(String orderId, List<OrderDetailTM> orderDetails){
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("INSERT INTO `OrderDetail` VALUES (?,?,?,?)");
            boolean result = false;
            for (OrderDetailTM orderDetail: orderDetails) {
                pstm.setObject(1, orderId);
                pstm.setObject(2, orderDetail.getCode());
                pstm.setObject(3, orderDetail.getQty());
                pstm.setObject(4, orderDetail.getUnitPrice());
                result =  pstm.executeUpdate()> 0;
                if (!result){
                    return false;
                }
            }
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public static boolean updateQty(List<OrderDetailTM> orderDetails){
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("UPDATE Item SET qtyOnHand=qtyOnHand-? WHERE code=?");
            boolean result = false;
            for (OrderDetailTM orderDetail: orderDetails) {
                pstm.setObject(1, orderDetail.getQty());
                pstm.setObject(2, orderDetail.getCode());
                result =  pstm.executeUpdate()> 0;
                if (!result){
                    return false;
                }
            }
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

}

