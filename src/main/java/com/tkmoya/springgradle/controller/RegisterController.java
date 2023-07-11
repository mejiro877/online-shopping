package com.tkmoya.springgradle.controller;

import javax.validation.Valid;

import com.tkmoya.springgradle.model.LoginFormModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.tkmoya.springgradle.model.RegisterFormModel;
import com.tkmoya.springgradle.service.UserService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class RegisterController {

	private final UserService userService;

	@ModelAttribute("registerFormModel")
	public RegisterFormModel getRegisterForm() {
		return new RegisterFormModel();
	}

	/**
	 * ユーザ登録画面遷移
	 */
	@GetMapping("/register_user")
	public String showRegisterUserForm() {
		return "register_user";
	}

	/**
	 * ユーザ登録確認画面遷移
	 */
//	@GetMapping("/register_check")
//	public String showRegisterUserCheckForm() {
//		return "register_check";
//	}

	/**
	 * ユーザ登録確認画面遷移
	 */
	@GetMapping( "/register_end")
	public String showRegisterUserEndForm() {
		return "register_end";
	}

	/**
	 * ユーザー登録処理開始
	 */
	@PostMapping( "/register_check")
	public String register(
			@Validated @ModelAttribute("registerFormModel") RegisterFormModel registerFormModel,
			BindingResult result,
			RedirectAttributes redirectAttr,
			Model model
	) {

		// サーバ側バリデーション
		if (result.hasErrors()) {
			redirectAttr.addFlashAttribute("registerFormModel", result);
			redirectAttr.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "registerFormModel",result);
			return "redirect:register_user";
		}

		model.addAttribute("registerFormModel", registerFormModel);
		return "register_check";
	}

	@PostMapping( "/register_end")
	public String register_check(@ModelAttribute RegisterFormModel registerFormModel, Model model) {

		try {
			// 登録処理
			userService.register(registerFormModel);
			// 登録成功
			model.addAttribute("registerFormModel", registerFormModel);
			return "register_end";

		} catch (Exception e) {
			// 登録失敗
			return "/WEB-INF/rg/rg_failure";
		}
	}
}
