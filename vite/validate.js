// -----------------------------------------
// ログインチェック処理例
// -----------------------------------------
function validate(formObj) {

	var errorCount = 0;

	var idCheck = document.getElementById('idCheck');
	var pwCheck = document.getElementById('pwCheck');
	idCheck.innerText = "";
	pwCheck.innerText = "";

	// ID必須入力チェック
	if (formObj.userId.value == "") {
		idCheck.innerText = 'IDを入力してください';
		errorCount++;
	}

	// パスワード必須入力チェック
	if (formObj.password.value == "") {
		pwCheck.innerText = 'パスワードを入力してください';
		errorCount++;
	}

	if (errorCount > 0) {
		return false;
	}

	// ID「32文字まで」チェック
	if (formObj.userId.value.length > 32) {
		idCheck.innerText = 'IDは32文字以内で入力してください';
		errorCount++;
	}

	// パスワード「32文字まで」チェック
	if (formObj.password.value.length > 32) {
		pwCheck.innerText = 'パスワードは32文字以内にしてください';
		errorCount++;
	}

	if (errorCount > 0) {
		return false;
	}

	// ID半角英数字チェック
	if (!formObj.userId.value.match(/^[0-9a-zA-Z]+$/)) {
		idCheck.innerText = 'IDは半角英数字で入力してください';
		errorCount++;
	}

	// パスワード半角英数字チェック
	if (!formObj.password.value.match(/^[0-9a-zA-Z]+$/)) {
		pwCheck.innerText = 'パスワードは半角英数字で入力してください';
		errorCount++;
	}

	if (errorCount > 0) {
		return false;
	}

}

// -----------------------------------------
// 新規登録チェック処理
// -----------------------------------------
function registrationValidate(formObj) {

	var formatCheck = validate(formObj);

	if(formatCheck != null){
		return false;
	}

	var cpwCheck = document.getElementById('cpwCheck');
	cpwCheck.innerText = "";

	if (formObj.confPassword.value != formObj.password.value) {

		cpwCheck.innerText = 'パスワードが一致しません';
		return false;

	}

}

