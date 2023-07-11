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

import com.tkmoya.springgradle.entity.OnlineMemberEntity;

@Repository
public class UserInfoDao {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterTemplate;

	@Autowired
    private JdbcTemplate jdbcTemplate;

	private class UserInfoRowMapper extends BeanPropertyRowMapper<OnlineMemberEntity> {
		@Override
		public OnlineMemberEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
			OnlineMemberEntity entity = new OnlineMemberEntity();
            entity.setMemberNo(rs.getInt("MEMBER_NO"));
            entity.setPassword(rs.getString("PASSWORD"));
            entity.setName(rs.getString("NAME"));
            entity.setAge(rs.getInt("AGE"));
            entity.setSex(rs.getString("SEX"));
            entity.setZip(rs.getString("ZIP"));
            entity.setAddress(rs.getString("ADDRESS"));
            entity.setTel(rs.getString("TEL"));
            entity.setRegisterDate(rs.getDate("REGISTER_DATE"));
            entity.setDeleteFlg(rs.getString("DELETE_FLG"));
            entity.setLastUpdDate(rs.getDate("LAST_UPD_DATE"));
			return entity;
		}
	}

	public List<OnlineMemberEntity> login(OnlineMemberEntity entity) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		String sql = "select * from ONLINE_MEMBER where MEMBER_NO = :memberNo and PASSWORD = :password and DELETE_FLG = '0'";
		paramMap.addValue("memberNo", entity.getMemberNo());
		paramMap.addValue("password", entity.getPassword());
		List<OnlineMemberEntity> userInfoList = this.namedParameterTemplate.query(sql, paramMap,
				new UserInfoRowMapper());
		return userInfoList;
	}

	public List<OnlineMemberEntity> selectById(OnlineMemberEntity entity) {
		String sql = "select * from ONLINE_MEMBER where MEMBER_NO = :memberNo and DELETE_FLG = '0'";
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("memberNo", entity.getMemberNo());
		List<OnlineMemberEntity> userInfoList = this.namedParameterTemplate.query(sql, paramMap,
				new UserInfoRowMapper());
		return userInfoList;
	}

    public List<OnlineMemberEntity> findAnsByMemberNo(int memberNo) {
        String sql = "select * from ONLINE_MEMBER where MEMBER_NO = :memberNo";
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        paramMap.addValue("memberNo", memberNo);
        List<OnlineMemberEntity> userInfoList = this.namedParameterTemplate.query(sql, paramMap, new UserInfoRowMapper());
        return userInfoList;
    }

	public int register(OnlineMemberEntity entity) {
		String sql = "insert into ONLINE_MEMBER (MEMBER_NO,NAME,PASSWORD,AGE,SEX,ZIP,ADDRESS,TEL,DELETE_FLG,REGISTER_DATE,LAST_UPD_DATE)  "
				+ "values ( :memberNo, :name, :password, :age, :sex, :zip, :address, :tel, '0', NOW(), NOW())";
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("memberNo", findMaxMemberNo());
		paramMap.addValue("name", entity.getName());
		paramMap.addValue("password", entity.getPassword());
		paramMap.addValue("age", entity.getAge());
		paramMap.addValue("sex", entity.getSex());
		paramMap.addValue("zip", entity.getZip());
		paramMap.addValue("address", entity.getAddress());
		paramMap.addValue("tel", entity.getTel());
		int resultCount = this.namedParameterTemplate.update(sql, paramMap);
		return resultCount;
	}

    public Integer findMaxMemberNo() {
        String sql = "select MAX(MEMBER_NO)+1 from ONLINE_MEMBER";
        Integer max = this.jdbcTemplate.queryForObject(sql, Integer.class);
        if (max == null) {
			max = 1;
		}
        return max;
    }

	public int updateUser(OnlineMemberEntity entity, String nowPassword) {
		String sql = "update ONLINE_MEMBER set NAME = :newName,AGE = :newAge,"
				+ "SEX = :newSex,ZIP = :newZip,ADDRESS = :newAddress,TEL = :newTel";
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		if(!(entity.getPassword().equals(""))) {
			sql += ",PASSWORD = :newPassword";
			paramMap.addValue("newPassword", entity.getPassword());
		}
		sql += ",LAST_UPD_DATE = NOW() where MEMBER_NO = :memberNo and PASSWORD = :nowPassword and DELETE_FLG = '0'";
		paramMap.addValue("newName", entity.getName());
		paramMap.addValue("newAge", entity.getAge());
		paramMap.addValue("newSex", entity.getSex());
		paramMap.addValue("newZip", entity.getZip());
		paramMap.addValue("newAddress", entity.getAddress());
		paramMap.addValue("newTel", entity.getTel());
		paramMap.addValue("memberNo", entity.getMemberNo());
		paramMap.addValue("nowPassword", nowPassword);
		int resultCount = this.namedParameterTemplate.update(sql, paramMap);
		return resultCount;
	}

	public int delUser(OnlineMemberEntity entity) {
		String sql = "update ONLINE_MEMBER set DELETE_FLG = '1',LAST_UPD_DATE = NOW() where MEMBER_NO = :memberNo and DELETE_FLG = '0'";
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("memberNo", entity.getMemberNo());
		int resultCount = this.namedParameterTemplate.update(sql, paramMap);
		return resultCount;
	}
}
