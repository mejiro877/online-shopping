package com.tkmoya.springgradle.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tkmoya.springgradle.dao.CategoryDao;
import com.tkmoya.springgradle.entity.CategoryEntity;
import com.tkmoya.springgradle.model.CategoryModel;

@Service
public class FormInitService {

	@Autowired
	private CategoryDao categoryDao;

	public List<CategoryModel> getCategoryChoices() {
		// データベースから値を取得
		List<CategoryEntity> categoryEntityList = categoryDao.findAllCategory();
		// データベースから取得した値の格納先としてListを宣言する
		List<CategoryModel> categoryList = new ArrayList<>();
		for (CategoryEntity entity : categoryEntityList) {
			// EntityからModelへ値を格納し、categoryListに詰めていく
			CategoryModel model = new CategoryModel();
			model.setCtgrId(entity.getCtgrId());
			model.setName(entity.getName());
			categoryList.add(model);
		}
		return categoryList;
	}
}