package dao;

import entity.Customer;

import java.util.List;

public interface CustomerDAO {
    public List<Customer> findAllCustomers();
    Customer findCustomer(String customerId);
    public boolean saveCustomer(Customer customer);
    public boolean updateCustomer(Customer customer);
    public boolean deleteCustomer(String customerId);
    public String getLastCustomerId();
}
