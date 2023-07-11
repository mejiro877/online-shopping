package com.tkmoya.springgradle.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.tkmoya.springgradle.model.RegisterFormModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.tkmoya.springgradle.model.EditFormModel;
import com.tkmoya.springgradle.service.UserService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class EditController {

  private final UserService userService;

  @ModelAttribute("editFormModel")
  public EditFormModel getEditFormModel() {
    return new EditFormModel();
  }

  /**
   * ユーザ情報表示画面遷移
   */
  @GetMapping("edit")
  public String menuForm(@ModelAttribute EditFormModel editFormModel, Model model, HttpServletRequest request, HttpSession session) {
    // ログイン画面の遷移前画面の情報をセッションに持たせる
    session.setAttribute("beforeEdit", request.getRequestURI());
    if (checkIsLogin(session)) {
      Integer memberNo = (Integer) session.getAttribute(MenuController.USER_ID_KEY);
      editFormModel = (EditFormModel) userService.findUserAnsByMemberNo(memberNo);
      model.addAttribute("editFormModel", editFormModel);
      session.setAttribute("editFormModel", editFormModel);
      return "edit";
    } else {
      return "redirect:login";
    }
  }

  /**
   * ユーザ情報更新画面遷移
   */
  @GetMapping ( "edit_update")
  public String showUserInfoUpdateForm(Model model, HttpSession session) {
    EditFormModel editFormModel = (EditFormModel) session.getAttribute("editFormModel");
    model.addAttribute("editFormModel", editFormModel);
    return "edit_update";
  }

  /**
   * ユーザ情報更新完了画面遷移
   */
  @GetMapping("edit_end")
  public String showEditUserEndForm() {
    return "edit_end";
  }

  /**
   * ユーザ削除確認画面遷移
   */
  @GetMapping("edit_delete")
  public String showUserDeleteForm() {
    return "edit_delete";
  }

  /**
   * ユーザ情報変更処理開始
   */
  @PostMapping( "edit_check")
  public String updateUser(
          @Validated @ModelAttribute EditFormModel editFormModel,
          BindingResult result,
          RedirectAttributes redirectAttr,
          Model model,
          HttpSession session
  ) {

    // サーバ側バリデーション
    if (result.hasErrors()) {
      redirectAttr.addFlashAttribute("editFormModel", result);
      redirectAttr.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "editFormModel",result);
      return "redirect:edit_update";
    }

    Integer memberNo = (Integer) session.getAttribute("userId");
    if (!userService.nowPassCk(memberNo, editFormModel.getNowPassword())) {
      // 現在のパスワード確認
      return "redirect:edit_update";
    }

    model.addAttribute("editFormModel", editFormModel);
    return "edit_check";
  }

  @PostMapping("edit_end")
  public String edit_check(
          @ModelAttribute EditFormModel editFormModel,
          RedirectAttributes redirectAttributes,
          HttpSession session) {

    Integer memberNo = (Integer) session.getAttribute("userId");

    try {
      userService.updateUser(memberNo, editFormModel);
      // 更新成功
      return "edit_end";

    } catch (Exception e) {
      // 登録失敗
      redirectAttributes.addFlashAttribute("errorMess", "変更処理時にエラーが発生し、変更に失敗しました。");
      return "redirect:menu";
    }
  }

  @PostMapping("edit_update")
  public String showUserInfoForm(@ModelAttribute EditFormModel editFormModel, Model model, HttpSession session) {
    editFormModel = (EditFormModel) session.getAttribute("editFormModel");
    model.addAttribute("editFormModel", editFormModel);
    return "edit_update";
  }

  @PostMapping("edit_delete_check")
  public String showUserDeleteForm(@ModelAttribute EditFormModel editFormModel, Model model, HttpSession session) {
    editFormModel = (EditFormModel) session.getAttribute("editFormModel");
    model.addAttribute("editFormModel", editFormModel);
    return "edit_delete_check";
  }

  @PostMapping("edit_delete_end")
  public String delUser(RedirectAttributes redirectAttributes, Model model, HttpSession session) {

    Integer memberNo = (Integer) session.getAttribute("userId");
    try {
      userService.delUser(memberNo);
      session.invalidate();
      // 論理削除成功
      return "edit_delete_end";

    } catch (Exception e) {
      // 論理削除失敗
      redirectAttributes.addFlashAttribute("errorMess", "削除処理時にエラーが発生し、削除に失敗しました。");
      return "redirect:edit_delete_check";
    }
  }

  private boolean checkIsLogin(HttpSession session) {
    // ログイン時にセッションに登録したログイン情報があるか確認
    Integer count = (Integer) session.getAttribute(MenuController.USER_ID_KEY);
    if (count != null) {
      return true;
    } else {
      return false;
    }

  }
}
