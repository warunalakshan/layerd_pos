package dao;

import entity.Item;

import java.util.List;

public interface ItemDAO {

    public List<Item> findAllItems();
    public Item findItem(String itemCode);
    public boolean saveItem(Item item);
    public boolean updateItem(Item item);
    public boolean deleteItem(String itemCode);
    public String getLastItemCode();

}
