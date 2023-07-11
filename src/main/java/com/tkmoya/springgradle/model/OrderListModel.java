package com.tkmoya.springgradle.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderListModel {
	private String listNo;
	private String collectNo;
	private String productCode;
	private String orderCount;
	private String orderPrice;
}