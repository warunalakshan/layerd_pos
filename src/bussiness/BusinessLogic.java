package bussiness;

import Dao.DaodataLayer;
import util.CustomerTM;
import util.ItemTM;
import util.OrderDetailTM;
import util.OrderTM;

import java.util.List;

public class BusinessLogic {

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

    public static boolean saveCustomer(ItemTM item){
        return DaodataLayer.saveItem(item);
    }

    public static boolean deleteItem(String itemCode) {
        return DaodataLayer.deleteItem(itemCode);
    }

    public static boolean updateItem(ItemTM item) {
        return DaodataLayer.updateItem(item);
    }

    public static boolean placeOrder(OrderTM order, List<OrderDetailTM> orderdetails){
        return DaodataLayer.placeOrder(order, orderdetails);
    }
}
