package com.tkmoya.springgradle.service;

import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tkmoya.springgradle.dao.OrderDao;
import com.tkmoya.springgradle.dao.OrderListDao;
import com.tkmoya.springgradle.entity.OrderEntity;
import com.tkmoya.springgradle.model.OrderModel;
import com.tkmoya.springgradle.model.SearchProductResultModel;

@Service
public class OrderService {

	@Autowired
	private OrderDao orderDao;
	@Autowired
	private OrderListDao orderListDao;

	@Transactional(rollbackForClassName = "Exception")
	public boolean OrderExecute(OrderModel order,TreeMap<String, SearchProductResultModel> orderProductMap)
			throws SQLException {

		// OrderEntityに値収納
		OrderEntity orderEntity = new OrderEntity();
		orderEntity.setMemberNo(order.getMemberNo());
		orderEntity.setTotalMoney(order.getTotalMoney());
		orderEntity.setTotalTax(order.getTotalTax());
		orderEntity.setCollectNo(order.getCollectNo());


		orderEntity.setProductCode(order.getProductCode());
		orderEntity.setOrderCount(order.getOrderCount());
		orderEntity.setOrderPrice(order.getOrderPrice());

		int result1 = orderDao.InsertOrder(orderEntity);

		for (Map.Entry<String, SearchProductResultModel> orderelement : orderProductMap.entrySet()) {
			SearchProductResultModel element = orderelement.getValue();
			orderEntity.setProductCode(element.getProductCode());
			orderEntity.setOrderCount(element.getProductCnt());
			orderEntity.setOrderPrice(element.getUnitPrice());


		int result2 = orderDao.InsertOrderList(orderEntity);
		}

		for (Map.Entry<String, SearchProductResultModel> orderelement : orderProductMap.entrySet()) {
			SearchProductResultModel element = orderelement.getValue();
			orderEntity.setProductCode(element.getProductCode());
			orderEntity.setOrderCount(element.getProductCnt());

		int result3 = orderDao.UpdateStock(orderEntity);
		}

		return true;
	}
}