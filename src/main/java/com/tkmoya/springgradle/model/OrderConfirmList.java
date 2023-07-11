package com.tkmoya.springgradle.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

@Getter
@Setter
public class OrderConfirmList {
	@Valid
	List<OrderConfirmModel> productList = new ArrayList<>();
}