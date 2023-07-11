package com.tkmoya.springgradle.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class OrderModel {

	private String orderNo;
	private String memberNo;
	private String totalMoney;
	private String totalTax;
	private Date orderDate;
	private String collectNo;
	private Date lastUpdDate;

	private String listNo;
	private String productCode;
	private String orderCount;
	private String orderPrice;
}