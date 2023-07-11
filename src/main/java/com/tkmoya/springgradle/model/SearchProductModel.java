package com.tkmoya.springgradle.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class SearchProductModel {
	private String productCnt;
	private String categoryId;
	private String productName;
	private String maker;

	@Pattern(regexp = "^[0-9]+$", message = "金額上限は半角数字で入力してください")
	@Pattern(regexp = "^[1-9][0-9]+$", message = "金額上限は正の数で入力してください")
	private String unitPriceMax;

	@Pattern(regexp = "^[0-9]+$", message = "金額下限は半角数字で入力してください")
	@Pattern(regexp = "^[1-9][0-9]+$", message = "金額下限は正の数で入力してください")
	private String unitPriceMin;

    @AssertTrue(message = "金額上限は金額下限より大きい値を入力してください。")
    public boolean isCheckMax() {
    	if (unitPriceMax != null && unitPriceMin != null) {
    		return Integer.parseInt(unitPriceMax) >= Integer.parseInt(unitPriceMin);
    	}
    	return true;
    }
}