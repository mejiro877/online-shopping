package com.tkmoya.springgradle.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.tkmoya.springgradle.entity.OrderEntity;

@Repository
public class OrderDao {

	@Autowired
	private NamedParameterJdbcTemplate template;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private class OrderRowMapper extends BeanPropertyRowMapper<OrderEntity> {
        @Override
        public OrderEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        	OrderEntity entity = new OrderEntity();
        	entity.setOrderNo(rs.getString("ORDER_NO"));
        	entity.setMemberNo(rs.getString("MEMBER_NO"));
        	entity.setTotalMoney(rs.getString("TOTAL_MONEY"));
        	entity.setTotalTax(rs.getString("TOTAL_TAX"));
        	entity.setOrderDate(rs.getDate("ORDER_DATE"));
        	entity.setCollectNo(rs.getString("COLLECT_NO"));
        	entity.setLastUpdDate(rs.getDate("LAST_UPD_DATE"));
        	entity.setListNo(rs.getString("LIST_NO"));
        	entity.setProductCode(rs.getString("PRODUCT_CODE"));
        	entity.setOrderCount(rs.getString("ORDER_COUNT"));
        	entity.setOrderPrice(rs.getString("ORDER_PRICE"));
            return entity;
        }
    }

	public int InsertOrder(OrderEntity orderEntity) {
		String sql = "INSERT INTO ONLINE_ORDER(MEMBER_NO,TOTAL_MONEY,TOTAL_TAX,ORDER_DATE,COLLECT_NO,LAST_UPD_DATE) VALUES"
				+ "(:memberNo,:totalMoney,:totalTax,NOW(),:collectNo,NOW())";
		MapSqlParameterSource param = new MapSqlParameterSource();
		param.addValue("memberNo",orderEntity.getMemberNo());
		param.addValue("totalMoney",orderEntity.getTotalMoney());
		param.addValue("totalTax",orderEntity.getTotalTax());
//		String collectNo = orderEntity.getMemberNo() + "-" + "4";
		String collectNo = String.valueOf(findMaxCollectNo());
		param.addValue("collectNo",collectNo);
		int count = this.template.update(sql, param);

		return count;

	}

	public int InsertOrderList(OrderEntity orderEntity) {
	String sql2 = "INSERT INTO ONLINE_ORDER_LIST(COLLECT_NO,PRODUCT_CODE,ORDER_COUNT,ORDER_PRICE) VALUES"
			+ "(:collectNo,:productCode,:orderCount,:orderPrice)";
	MapSqlParameterSource param2 = new MapSqlParameterSource();
	String collectNo = String.valueOf(findMaxCollectNoNow());
	param2.addValue("collectNo",collectNo);
	param2.addValue("productCode",orderEntity.getProductCode());
	param2.addValue("orderCount",orderEntity.getOrderCount());
	param2.addValue("orderPrice",orderEntity.getOrderPrice());
	int count2 = this.template.update(sql2, param2);

	return count2;

}

	public int UpdateStock(OrderEntity orderEntity) {
	String sql3 = "UPDATE ONLINE_PRODUCT SET STOCK_COUNT=STOCK_COUNT-(:orderCount) WHERE PRODUCT_CODE = :productCode";
	MapSqlParameterSource param3 = new MapSqlParameterSource();
	param3.addValue("productCode",orderEntity.getProductCode());
	param3.addValue("orderCount",orderEntity.getOrderCount());
	int count3 = this.template.update(sql3, param3);

	return count3;

}

    public Integer findMaxCollectNo() {
        String sql = "select MAX(CAST(COLLECT_NO as SIGNED))+1 from ONLINE_ORDER";
        Integer max = this.jdbcTemplate.queryForObject(sql, Integer.class);
        if (max == null) {
			max = 1;
		}
        return max;
    }

    public Integer findMaxCollectNoNow() {
        String sql = "select MAX(CAST(COLLECT_NO as SIGNED)) from ONLINE_ORDER";
        Integer max = this.jdbcTemplate.queryForObject(sql, Integer.class);
        if (max == null) {
			max = 1;
		}
        return max;
    }
}