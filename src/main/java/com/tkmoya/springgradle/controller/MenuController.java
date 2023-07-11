package com.tkmoya.springgradle.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.tkmoya.springgradle.model.EditFormModel;
import com.tkmoya.springgradle.model.LoginFormModel;
import com.tkmoya.springgradle.service.UserService;

/**
 * 画面遷移コントローラー
 */
@Controller
@RequiredArgsConstructor
public class MenuController {

    public static final String USER_ID_KEY = "userId";

    private final UserService userService;

    /**
     * エラー画面遷移
     */
    @PostMapping("error")
    public String showError(HttpSession session) {
        return "menu";
    }

    /**
     * メニュー画面遷移
     */
    @GetMapping("/")
    public String showIndex(HttpServletRequest request, HttpSession session) {
        return "menu";
    }

    /**
     * メニュー画面遷移
     */
    @GetMapping("/menu")
    public String showMenuForm(HttpServletRequest request, HttpSession session) {
        return "menu";
    }

    /**
     * ログイン画面遷移
     */
    @GetMapping("/login")
    public String showLoginForm(LoginFormModel loginFormModel, Model model) {
        model.addAttribute("loginFormModel", new LoginFormModel());
        return "login";
    }

    /**
     * ログイン処理開始
     */
    @PostMapping("/login")
    public String login(
            @Validated LoginFormModel loginFormModel,
            BindingResult result,
            Model model,
            HttpServletRequest request,
            HttpSession session
    ) {

        // サーバ側バリデーション
        if (result.hasErrors()) {
            return "login";
        }

        try {
            userService.doLogin(loginFormModel);
            // ログインに成功した場合、セッションを保持する
            session.setAttribute(USER_ID_KEY, Integer.parseInt(loginFormModel.getMemberNo()));
            Integer memberNo = (Integer) session.getAttribute(USER_ID_KEY);
            EditFormModel editFormModel = userService.findUserAnsByMemberNo(memberNo);
            //ユーザー編集時に利用するユーザー情報をセッションとして保持する
            session.setAttribute("editFormModel", editFormModel);
        } catch (Exception e) {
            // ログイン失敗
            model.addAttribute("message", "ログインできませんでした。");
            return "login";
        }

        //beforeEditというセッション変数に遷移元のURL情報を持たせる。
        if (session.getAttribute("beforeEdit") == null) {
            session.setAttribute("beforeEdit", request.getRequestURI());
        }
        String requestURI = (String) session.getAttribute("beforeEdit");

        // 遷移元によりログイン認証後の画面遷移先を分ける

        switch (requestURI) {
            case "/ecsite_kanda/edit.html":
                return "redirect:edit";
            case "/ecsite_kanda/order_end.html":
                return "redirect:shopping_check";
            default:
                return "redirect:menu";
        }
    }

    /**
     * ログアウト画面遷移
     */
    @GetMapping(path = "/logout")
    public String showLogoutForm(HttpSession session) {
        // セッションに保持している情報を削除する
        session.invalidate();
        return "redirect:login";
    }
}
