package com.tkmoya.springgradle.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tkmoya.springgradle.dao.ProductDao;
import com.tkmoya.springgradle.entity.ProductEntity;
import com.tkmoya.springgradle.model.SearchProductModel;
import com.tkmoya.springgradle.model.SearchProductResultModel;

@Service
public class ProductService {

	@Autowired
	private ProductDao productDao;

	/**
	 * 検索条件モデルからDB検索結果を得て、ページングして返す
	 * @param searchProduct
	 * @return
	 */

	public boolean checkDeleteFlg(String productCode) throws Exception {
		List<ProductEntity> checkDeleteFlgList = productDao.checkDeleteFlg(productCode);
		String dflg = String.valueOf(checkDeleteFlgList.get(0));
		if (!(dflg.equals("0"))) {
			return true;
		}
		return false;
	}

	public List<List<SearchProductResultModel>> searchProduct(SearchProductModel searchProduct) {
		// 検索条件Mapを作成
		HashMap<String, String> conditionMap = new HashMap<>();
		if (searchProduct.getCategoryId() != null && !searchProduct.getCategoryId().equals("")) {
			conditionMap.put("categoryId", searchProduct.getCategoryId());
		}
		if (searchProduct.getProductName() != null && !searchProduct.getProductName().equals("")) {
			conditionMap.put("productName", searchProduct.getProductName());
		}
		if (searchProduct.getMaker() != null && !searchProduct.getMaker().equals("")) {
			conditionMap.put("maker", searchProduct.getMaker());
		}
		if (searchProduct.getUnitPriceMin() != null && !searchProduct.getUnitPriceMin().equals("")) {
			conditionMap.put("unitPriceMin", searchProduct.getUnitPriceMin());
		}
		if (searchProduct.getUnitPriceMax() != null && !searchProduct.getUnitPriceMax().equals("")) {
			conditionMap.put("unitPriceMax", searchProduct.getUnitPriceMax());
		}

		// 検索条件を引き渡しメンバー情報を検索
		List<ProductEntity> productList = productDao.checkProduct(conditionMap);
		// モデルに情報を格納し呼び出し元に返す
		List<SearchProductResultModel> searchMemberResult = new ArrayList<>();
		for (ProductEntity entity : productList) {
			SearchProductResultModel model = new SearchProductResultModel();
			model.setProductCode(entity.getProductCode());
			model.setProductName(entity.getProductName());
			model.setMaker(entity.getMaker());
			model.setUnitPrice(entity.getUnitPrice());
			model.setMemo(entity.getMemo());
			searchMemberResult.add(model);
		}

		return CreatePageList(searchMemberResult);
	}

	/**
	 * ProductCodeから商品情報を得る
	 * @param selectProductCode
	 * @return
	 */
	public SearchProductResultModel searchProductByCode(String selectProductCode) {
		HashMap<String, String> conditionMap = new HashMap<>();
		conditionMap.put("productCode", selectProductCode);
		// 検索条件を引き渡しメンバー情報を検索
		List<ProductEntity> productList = productDao.checkProduct(conditionMap);
		// モデルに情報を格納し呼び出し元に返す
		if (productList.size() == 1) {
			ProductEntity entity = productList.get(0);
			SearchProductResultModel model = new SearchProductResultModel();
			model.setProductCode(entity.getProductCode());
			model.setCategoryId(entity.getCategoryId());
			model.setProductName(entity.getProductName());
			model.setMaker(entity.getMaker());
			model.setStockCount(entity.getStockCount());
			model.setUnitPrice(entity.getUnitPrice());
			model.setPictureName(entity.getPictureName());
			model.setMemo(entity.getMemo());
			model.setDeleteFlg(entity.getDeleteFlg());
			return model;
		} else {
			// エラー
			return null;
		}
	}

	/**
	 * 検索結果を最大10件のページにかくのうする
	 * @param list
	 * @return
	 */
	public List<List<SearchProductResultModel>> CreatePageList(List<SearchProductResultModel> list) {
		List<SearchProductResultModel> page = new ArrayList<>();
		List<List<SearchProductResultModel>> pageList = new ArrayList<>();
		for (SearchProductResultModel item : list) {
			// 10項目貯まったら、ページリストにページを保存し、新しくページを作成する
			if (page.size() >= 10) {
				pageList.add(page);
				page = new ArrayList<>();
			}
			page.add(item);
		}
		// ページリストにページを保存する
		pageList.add(page);
		return pageList;
	}

}