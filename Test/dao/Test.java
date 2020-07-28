package dao;

import Dao.CustomerDAO;
import entity.Customer;

import java.util.List;

public class Test {
    public static void main(String[] args) {

        boolean result = CustomerDAO.saveCustomer(new Customer("C012", "Gaka", "Panadura"));
        System.out.println(result);
        List<Customer> customers = CustomerDAO.findAllCustomers();
        for (Customer customer : customers) {
            System.out.println(customer);
        }
    }
}
