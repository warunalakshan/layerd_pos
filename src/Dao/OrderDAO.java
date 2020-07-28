package Dao;

import db.DBConnection;
import entity.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public static List<Order> findAllOrders() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM `Order`");
            List<Order> orders = new ArrayList<>();
            while (rst.next()) {
                orders.add(new Order(rst.getString(1),
                        rst.getDate(2),
                        rst.getString(3)));
            }
            return orders;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public static Order findOrder(String orderId) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM `Order` WHERE id=?");
            pstm.setObject(1, orderId);
            ResultSet rst = pstm.executeQuery();
            if (rst.next()) {
                return new Order(rst.getString(1),
                        rst.getDate(2),
                        rst.getString(3));
            }
            return null;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public static boolean saveOrder(Order order) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("INSERT INTO `Order` VALUES (?,?,?)");
            pstm.setObject(1, order.getId());
            pstm.setObject(2, order.getDate());
            pstm.setObject(3, order.getCustomerId());
            return pstm.executeUpdate() > 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public static boolean updateOrder(Order order) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("UPDATE Order SET date=?, customerId=? WHERE id=?");
            pstm.setObject(3, order.getId());
            pstm.setObject(1, order.getDate());
            pstm.setObject(2, order.getCustomerId());
            return pstm.executeUpdate() > 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public static boolean deleteOrder(String orderId) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("DELETE FROM Order WHERE id=?");
            pstm.setObject(1, orderId);
            return pstm.executeUpdate() > 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public static String getLastOrderId() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM `Order` ORDER BY id DESC LIMIT 1");
            if (!rst.next()){
                return null;
            }else{
                return rst.getString(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }
}
