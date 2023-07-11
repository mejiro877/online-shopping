package com.tkmoya.springgradle.model;

import java.util.ArrayList;
import java.util.List;

import com.tkmoya.springgradle.entity.OnlineMemberEntity;

public class UserInfoModel {

    List<OnlineMemberEntity> ubList = new ArrayList<OnlineMemberEntity>();

	public List<OnlineMemberEntity> getUbList() {
		return ubList;
	}

	public void setUbList(List<OnlineMemberEntity> ubList) {
		this.ubList = ubList;
	}
}
