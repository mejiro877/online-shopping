package com.tkmoya.springgradle.model;

import java.sql.Date;

import javax.validation.GroupSequence;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.tkmoya.springgradle.model.LoginFormModel.First;
import com.tkmoya.springgradle.model.LoginFormModel.Second;

@GroupSequence({ First.class, Second.class, EditFormModel.class })
public class EditFormModel {

	public interface First {
	}

	public interface Second {
	}

	private String memberNo;

    @NotEmpty(message = "氏名を入力してください")
    @Size(max = 20, message = "氏名は{max}文字以内で入力してください")
    private String name;

	@NotEmpty(message = "現在のパスワードを入力してください", groups = { First.class })
	@Size(max = 8, message = "現在のパスワードは{max}文字以内で入力してください", groups = { Second.class })
	private String password;

    @Size(max = 8, message = "パスワードは{max}文字以内で入力してください", groups = { Second.class })
    private String newPassword;

    @Size(max = 8, message = "確認用パスワードは{max}文字以内で入力してください")
    private String confNewPassword;

    @NotEmpty(message = "年齢を入力してください")
    @Pattern(regexp = "^[0-9]+$", message = "年齢は半角数字で入力してください")
    @Pattern(regexp = "^[1-9][0-9]+$", message = "年齢は正の数で入力してください")
    private String age;

    @NotEmpty(message = "性別を選択してください", groups = { First.class })
    private String sex;

    @NotEmpty(message = "郵便番号を入力してください")
    @Pattern(regexp = "\\d{3}-\\d{4}", message = "正しいフォーマットで入力してください")
    private String zip;

    @NotEmpty(message = "住所を入力してください")
    @Size(max = 50, message = "住所は{max}文字以内で入力してください")
    private String address;

    @NotEmpty(message = "電話番号を入力してください")
    @Pattern(regexp = "0\\d{1,4}-\\d{1,4}-\\d{4}", message = "正しいフォーマットで入力してください")
    @Size(max = 20, message = "電話番号は{max}文字以内で入力してください")
    private String tel;

	private Date registerDate;

	@AssertTrue(message = "新しいパスワードは現在のパスワードと異なるものにしてください。")
	public boolean isDiffPass() {
		if (!password.equals(newPassword)) {
			return true;
		} else {
			return false;
		}
	}

//	edit_updateへ遷移する時にも呼び出されるので、newPasswordとconfNewPasswordがNullのためエラーとなる。そのため、ifでエラー回避する。
	@AssertTrue(message = "パスワードが一致しません")
	public boolean isEqualPass() {
		if (newPassword != null) {
		if (newPassword.equals(confNewPassword)) {
			return true;
		} else {
			return false;
		}
	}
		return true;
	}

	public String getMemberNo() {
		return memberNo;
	}

	public void setMemberNo(String memberNo) {
		this.memberNo = memberNo;
	}

	public String getNowPassword() {
		return password;
	}

	public void setNowPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfNewPassword() {
		return confNewPassword;
	}

	public void setConfNewPassword(String confNewPassword) {
		this.confNewPassword = confNewPassword;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}


}
