package com.tkmoya.springgradle.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.tkmoya.springgradle.entity.OrderListEntity;

@Repository
public class OrderListDao {

	@Autowired
	private NamedParameterJdbcTemplate template;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private class OrderListRowMapper extends BeanPropertyRowMapper<OrderListEntity> {
        @Override
        public OrderListEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        	OrderListEntity entity = new OrderListEntity();
        	entity.setListNo(rs.getString("LIST_NO"));
        	entity.setCollectNo(rs.getString("COLLECT_NO"));
        	entity.setProductCode(rs.getString("PRODUCT_CODE"));
        	entity.setOrderCount(rs.getString("ORDER_COUNT"));
        	entity.setOrderPrice(rs.getString("ORDER_PRICE"));
            return entity;
        }
    }

	public int InsertOrderList(OrderListEntity orderListEntity) {
		String sql = "INSERT INTO ONLINE_ORDER_LIST(COLLECT_NO,PRODUCT_CODE,ORDER_COUNT,ORDER_PRICE) VALUES"
				+ "(:collectNo,:productCode,:orderCount,:orderPrice)";
		MapSqlParameterSource param = new MapSqlParameterSource();
		param.addValue("collectNo",orderListEntity.getCollectNo());
		param.addValue("productCode",orderListEntity.getProductCode());
		param.addValue("orderCount",orderListEntity.getOrderCount());
		param.addValue("orderPrice",orderListEntity.getOrderPrice());
		int count = this.template.update(sql, param);
		return count;

	}

	public List<OrderListEntity> getOrderListByOrDerNo(int orderNo) {
		String sql = "select * from ORDER_LIST where ORDER_NUM = :orderNo";
		MapSqlParameterSource param = new MapSqlParameterSource();
		param.addValue("orderNo", orderNo);
		List<OrderListEntity> orderListList = this.template.query(sql, param, new OrderListRowMapper());
		return orderListList;
	}

}