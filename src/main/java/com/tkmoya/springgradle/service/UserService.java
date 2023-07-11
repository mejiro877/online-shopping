package com.tkmoya.springgradle.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tkmoya.springgradle.dao.UserInfoDao;
import com.tkmoya.springgradle.entity.OnlineMemberEntity;
import com.tkmoya.springgradle.model.EditFormModel;
import com.tkmoya.springgradle.model.LoginFormModel;
import com.tkmoya.springgradle.model.RegisterFormModel;

@Service
public class UserService {

	@Autowired
	UserInfoDao userInfoDao;

	/*
	 * 認証処理
	 */
	public OnlineMemberEntity doLogin(LoginFormModel model) throws Exception {

		OnlineMemberEntity entity = new OnlineMemberEntity();
		entity.setMemberNo(Integer.parseInt(model.getMemberNo()));
		entity.setPassword(model.getPassword());

		// 入力された会員Noとパスワードを元にSelect文を実行し、ユーザ情報を取得する。
		List<OnlineMemberEntity> resultList = userInfoDao.login(entity);
		if (resultList.size() > 1) {
			// 本課題の仕様では、同ユーザーは登録することができない⇒重複するIDは存在しない
			// よってIDを使用した検索結果は必ず「1件」であるという想定。それ以外は「異常」とみなす。
			System.out.println("【ERROR：ログイン処理】ログイン対象ユーザが1件以上存在しています");
			throw new Exception();
		}
		return resultList.get(0);
	}

	public boolean isUserId(int memberNo) {
		OnlineMemberEntity entity = new OnlineMemberEntity();
		entity.setMemberNo(memberNo);
		// 本課題の仕様では、同ユーザーは登録することができない⇒重複するIDは存在しない
		// よってIDを使用した検索結果は必ず「1件」であるという想定。
		List<OnlineMemberEntity> resultList = userInfoDao.selectById(entity);
		if (resultList.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public EditFormModel findUserAnsByMemberNo(int memberNo) {
        List<OnlineMemberEntity> ubList = userInfoDao.findAnsByMemberNo(memberNo);
		if (ubList.size() == 1) {
			OnlineMemberEntity entity = ubList.get(0);
			EditFormModel model = new EditFormModel();
			model.setMemberNo(String.valueOf(entity.getMemberNo()));
			model.setNowPassword(entity.getPassword());
			model.setName(entity.getName());
			model.setAge(String.valueOf(entity.getAge()));
			model.setSex(entity.getSex());
			model.setZip(entity.getZip());
			model.setAddress(entity.getAddress());
			model.setTel(entity.getTel());
			model.setRegisterDate(entity.getRegisterDate());
			return model;
		} else {
			// エラー
			return null;
		}
    }


	public boolean nowPassCk(int memberNo, String nowPassword) {
		OnlineMemberEntity entity = new OnlineMemberEntity();
		entity.setMemberNo(memberNo);
		entity.setPassword(nowPassword);
		List<OnlineMemberEntity> resultList = userInfoDao.login(entity);
		if (resultList.size() > 0) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 登録処理
	 */
	@Transactional(rollbackForClassName = "Exception")
	public boolean register(RegisterFormModel model) throws Exception {
		OnlineMemberEntity entity = new OnlineMemberEntity();
		model.setMemberNo(userInfoDao.findMaxMemberNo().toString());
		entity.setMemberNo(Integer.parseInt(model.getMemberNo()));
		entity.setName(model.getName());
		entity.setPassword(model.getPassword());
		entity.setAge(Integer.parseInt(model.getAge()));
		entity.setSex(model.getSex());
		entity.setZip(model.getZip());
		entity.setAddress(model.getAddress());
		entity.setTel(model.getTel());

		int result = userInfoDao.register(entity);
		if (result > 0) {
			return true;
		} else {
			throw new Exception();
		}
	}

	/**
	 * ユーザ情報変更処理
	 */
	@Transactional(rollbackForClassName = "Exception")
	public boolean updateUser(int memberNo, EditFormModel model) throws Exception {
		OnlineMemberEntity entity = new OnlineMemberEntity();
		entity.setMemberNo(memberNo);
		entity.setName(model.getName());
		entity.setPassword(model.getNewPassword());
		entity.setAge(Integer.parseInt(model.getAge()));
		entity.setSex(model.getSex());
		entity.setZip(model.getZip());
		entity.setAddress(model.getAddress());
		entity.setTel(model.getTel());

		int result = userInfoDao.updateUser(entity, model.getNowPassword());
		//throw new Exception();
		if (result > 0) {
			return true;
		} else {
			throw new Exception();
		}
	}

	/**
	 * ユーザー削除処理（論理削除）
	 */
	@Transactional(rollbackForClassName = "Exception")
	public boolean delUser(int memberNo) throws Exception {
		OnlineMemberEntity entity = new OnlineMemberEntity();
		entity.setMemberNo(memberNo);

		int result = userInfoDao.delUser(entity);
		if (result > 0) {
			return true;
		} else {
			throw new Exception();
		}
	}
}
