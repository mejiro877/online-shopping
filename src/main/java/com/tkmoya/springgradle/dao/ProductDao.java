package com.tkmoya.springgradle.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.tkmoya.springgradle.entity.ProductEntity;

@Repository
public class ProductDao {

	@Autowired
	private NamedParameterJdbcTemplate template;

    private class ProductRowMapper extends BeanPropertyRowMapper<ProductEntity> {
        @Override
        public ProductEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        	ProductEntity product = new ProductEntity();
        	product.setProductCode(rs.getString("PRODUCT_CODE"));
        	product.setCategoryId(rs.getString("CATEGORY_ID"));
        	product.setProductName(rs.getString("PRODUCT_NAME"));
        	product.setMaker(rs.getString("MAKER"));
        	product.setStockCount(rs.getString("STOCK_COUNT"));
        	product.setUnitPrice(rs.getString("UNIT_PRICE"));
        	product.setPictureName(rs.getString("PICTURE_NAME"));
        	product.setMemo(rs.getString("MEMO"));
        	product.setDeleteFlg(rs.getString("DELETE_FLG"));
            return product;
        }
    }


	public List<ProductEntity> checkDeleteFlg(String productCode) {
		String sql = "SELECT * FROM ONLINE_PRODUCT WHERE PRODUCT_CODE = :productCode";
		MapSqlParameterSource param = new MapSqlParameterSource();
		param.addValue("productCode", productCode);
		List<ProductEntity> checkDeleteFlgList = this.template.query(sql, param, new ProductRowMapper());
		return checkDeleteFlgList;
	}

	public List<ProductEntity> checkProduct(HashMap<String, String> conditionMap) {

		// SQL生成。バインド変数をparamに格納。
		int bindCnt = 0;
		String sql = "select * from ONLINE_PRODUCT where DELETE_FLG = '0' ";
		MapSqlParameterSource param = new MapSqlParameterSource();
		for (String key : conditionMap.keySet()) {
			if (!conditionMap.get(key).equals("")) {
				sql += "and ";
				// 検索フォームで入力があるものをswitchで分ける
				switch (key) {
				case "productCode":
					sql += "PRODUCT_CODE = :productCode ";
					param.addValue("productCode", conditionMap.get(key));
					break;
				case "categoryId":
					sql += "CATEGORY_ID = :categoryId ";
					param.addValue("categoryId", Integer.parseInt(conditionMap.get(key)));
					break;
				case "productName":
					sql += "PRODUCT_NAME like concat('%',:productName,'%') ";
					param.addValue("productName", conditionMap.get(key));
					break;
				case "maker":
					sql += "MAKER like concat('%',:maker,'%') ";
					param.addValue("maker", conditionMap.get(key));
					break;
				case "unitPriceMin":
					sql += "UNIT_PRICE >= :unitPriceMin ";
					param.addValue("unitPriceMin", Integer.parseInt(conditionMap.get(key)));
					break;
				case "unitPriceMax":
					sql += "UNIT_PRICE <= :unitPriceMax ";
					param.addValue("unitPriceMax", Integer.parseInt(conditionMap.get(key)));
					break;
				}
				bindCnt++;
			}
		}

		List<ProductEntity> productList = this.template.query(sql, param, new ProductRowMapper());
		return productList;
	}
}