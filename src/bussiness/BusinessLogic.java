package bussiness;

import Dao.DaodataLayer;
import util.CustomerTM;
import util.ItemTM;
import util.OrderDetailTM;
import util.OrderTM;

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
        return DaodataLayer.placeOrder(order, orderdetails);
    }
}
