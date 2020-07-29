package dao;

import entity.OrderDetail;
import entity.OrderDetailPK;

import java.util.List;

public interface OrderDetailDAO {
    public List<OrderDetail> findAllOrderDetails();
    public OrderDetail findOrderDetail(OrderDetailPK orderDetailPK);
    public boolean saveOrderDetail(OrderDetail orderDetail);
    public boolean updateOrderDetail(OrderDetail orderDetail);
    public boolean deleteOrderDetail(OrderDetailPK orderDetailPK);
    public String getLastOrderId();
}
