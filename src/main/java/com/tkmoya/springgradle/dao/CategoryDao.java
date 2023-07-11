package com.tkmoya.springgradle.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.tkmoya.springgradle.entity.CategoryEntity;

@Repository
public class CategoryDao {

	@Autowired
	private NamedParameterJdbcTemplate template;

    private class CategoryRowMapper extends BeanPropertyRowMapper<CategoryEntity> {
        @Override
        public CategoryEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        	CategoryEntity category = new CategoryEntity();
        	category.setCtgrId(rs.getString("CTGR_ID"));
        	category.setName(rs.getString("NAME"));
            return category;
        }
    }

	public List<CategoryEntity> findAllCategory() {
		String sql = "select * from ONLINE_CATEGORY";
		List<CategoryEntity> categoryEntityList = this.template.query(sql, new CategoryRowMapper());
		return categoryEntityList;
	}

}