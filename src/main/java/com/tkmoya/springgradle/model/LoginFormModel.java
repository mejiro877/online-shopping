package com.tkmoya.springgradle.model;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import com.tkmoya.springgradle.model.LoginFormModel.First;
import com.tkmoya.springgradle.model.LoginFormModel.Second;
import lombok.Getter;
import lombok.Setter;

@GroupSequence({ First.class, Second.class, LoginFormModel.class })
@Getter
@Setter
public class LoginFormModel {

	public interface First {
	}

	public interface Second {
	}

	@NotEmpty(message = "会員NOを入力してください", groups = { First.class })
	@Pattern(regexp = "^[0-9]+$", message = "会員NOは半角数字で入力してください", groups = { Second.class })
	private String memberNo;

	@NotEmpty(message = "パスワードを入力してください", groups = { First.class })
	private String password;
}
