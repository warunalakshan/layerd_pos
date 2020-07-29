package dao;

import dao.impl.CustomerDAOImpl;
import entity.Customer;

import java.util.List;

public class Test {
    public static void main(String[] args) {

        boolean result = CustomerDAOImpl.saveCustomer(new Customer("C012", "Gaka", "Panadura"));
        System.out.println(result);
        List<Customer> customers = CustomerDAOImpl.findAllCustomers();
        for (Customer customer : customers) {
            System.out.println(customer);
        }
    }
}
