package com.tkmoya.springgradle.model;

import javax.validation.GroupSequence;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.tkmoya.springgradle.model.LoginFormModel.First;
import com.tkmoya.springgradle.model.LoginFormModel.Second;
import lombok.Getter;
import lombok.Setter;

@GroupSequence({ First.class, Second.class, RegisterFormModel.class })
@Getter
@Setter
public class RegisterFormModel {

    private String memberNo;

    @NotEmpty(message = "氏名を入力してください", groups = { First.class })
    @Size(max = 20, message = "氏名は{max}文字以内で入力してください", groups = { Second.class })
    private String name;

    @NotEmpty(message = "パスワードを入力してください", groups = { First.class })
    @Size(max = 8, message = "パスワードは{max}文字以内で入力してください", groups = { Second.class })
    private String password;

    @NotEmpty(message = "確認用パスワードを入力してください", groups = { First.class })
    @Size(max = 8, message = "確認用パスワードは{max}文字以内で入力してください", groups = { Second.class })
    private String confPassword;

    @NotEmpty(message = "年齢を入力してください", groups = { First.class })
    @Pattern(regexp = "^[0-9]+$", message = "年齢は半角数字で入力してください", groups = { Second.class })
    @Pattern(regexp = "^[1-9][0-9]+$", message = "年齢は正の数で入力してください")
    private String age;

    @NotEmpty(message = "性別を選択してください", groups = { First.class })
    private String sex;

    @NotEmpty(message = "郵便番号を入力してください", groups = { First.class })
    @Pattern(regexp = "\\d{3}-\\d{4}", message = "正しいフォーマットで入力してください", groups = { Second.class })
    private String zip;

    @NotEmpty(message = "住所を入力してください", groups = { First.class })
    @Size(max = 50, message = "住所は{max}文字以内で入力してください", groups = { Second.class })
    private String address;

    @NotEmpty(message = "電話番号を入力してください", groups = { First.class })
    @Pattern(regexp = "0\\d{1,4}-\\d{1,4}-\\d{4}", message = "正しいフォーマットで入力してください", groups = { Second.class })
    @Size(max = 20, message = "電話番号は{max}文字以内で入力してください")
    private String tel;

    // @AssertTrue(TRUEであることを確認)、@AssertFalse(FALSEであることを確認)を使用してパスワード一致確認を実装
    @AssertTrue(message = "パスワードが一致しません")
    public boolean isEqualPass() {
        if (password.equals(confPassword)) {
            return true;
        } else {
            return false;
        }
    }
}
