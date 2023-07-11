package com.tkmoya.springgradle.controller;

import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.tkmoya.springgradle.model.OrderModel;
import com.tkmoya.springgradle.model.SearchProductResultModel;
import com.tkmoya.springgradle.service.OrderService;
import com.tkmoya.springgradle.service.ProductService;
import com.tkmoya.springgradle.service.UserService;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;

    @GetMapping("/cart_list")
    public String ProductDetail(HttpSession session) {
        Object cartsession = session.getAttribute(SearchProductController.PRODUCT);
        TreeMap<String, SearchProductResultModel> selectProductMap = (TreeMap<String, SearchProductResultModel>) cartsession;
        session.setAttribute("cart", cartsession);
        return "cart_list";
    }

    @PostMapping(path = "/cart_list", params = "remove")
    public String ProductDetail(HttpServletRequest request, HttpSession session) {
        // 登録済みのカート情報を取得
        Object cartsession = session.getAttribute(SearchProductController.PRODUCT);
        TreeMap<String, SearchProductResultModel> selectProductMap = (TreeMap<String, SearchProductResultModel>) cartsession;

        if (request.getParameterValues("selectProductCode") == null) {
            request.setAttribute("error", "取り消し対象の商品を選択してください。");
            // 1ページ目に戻して、エラーメッセージを表示させる
            return "cart_list";
        }

        // チェックボックスにチェックが入っている商品だけ繰り返す
        for (String selectProductCode : request.getParameterValues("selectProductCode")) {
            selectProductMap.remove(selectProductCode);
        }
        session.setAttribute(SearchProductController.PRODUCT, selectProductMap);

        return "redirect:/cart_list";
    }

    @PostMapping(path = "/cart_list", params = "delete")
    public String CartDelete(HttpSession session) {
        session.removeAttribute(SearchProductController.PRODUCT);
        return "menu";
    }

    @PostMapping(path = "/shopping_check")
    public String CarttoCheck(
            @Validated @ModelAttribute OrderModel orderModel,
            BindingResult result,
            Model model,
            HttpServletRequest request,
            HttpSession session
    ) {
        Object cartsession = session.getAttribute(SearchProductController.PRODUCT);
        //		SearchProductResultModel test = (SearchProductResultModel) cartsession;
        TreeMap<String, SearchProductResultModel> selectProductMap = (TreeMap<String, SearchProductResultModel>) cartsession;
        int sum = 0;
        double tax = 0;

        for (Map.Entry<String, SearchProductResultModel> totalPrice : selectProductMap.entrySet()) {
            SearchProductResultModel element = totalPrice.getValue();
            element.setProductCnt(request.getParameterValues(totalPrice.getKey())[0]);
            //追記 2019/04/26
            String stock = element.getStockCount();
            if (!element.getProductCnt().matches("[0-9]+")) {
                request.setAttribute("error", "購入数は半角数字で入力してください。");
                return "cart_list";
            }
            if (element.getProductCnt().equals("")) {
                request.setAttribute("error", "購入数は1～999の数値で入力してください。");
                return "cart_list";
            }
            if (Integer.parseInt(element.getProductCnt()) < 1 || Integer.parseInt(element.getProductCnt()) > 999 || element.getProductCnt().equals("")) {
                request.setAttribute("error", "購入数は1～999の数値で入力してください。");
                return "cart_list";
            }
            if (Integer.parseInt(stock) < Integer.parseInt(element.getProductCnt())) {
                String shortage = String.valueOf(Math.abs(Integer.parseInt(stock) - Integer.parseInt(element.getProductCnt())));
                request.setAttribute("error", "在庫が足りません。購入数を変更してください。");
                return "cart_list";
            }

            int price = Integer.parseInt(element.getUnitPrice());
            int count = Integer.parseInt(element.getProductCnt());
            sum += price * count;
        }
        tax = (double) sum * 0.08;
        tax = Math.floor(tax);
        int t = (int) tax;
        int total = sum + t;
        model.addAttribute("sum", sum);
        model.addAttribute("tax", t);
        model.addAttribute("total", total);
        session.setAttribute("sum", sum);
        session.setAttribute("tax", t);
        session.setAttribute("total", total);

        selectProductMap.entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey());

        return "shopping_check";
    }


    @PostMapping(path = "/order_end", params = "delete")
    public String ShoppingEnd(HttpSession session) {
        session.removeAttribute(SearchProductController.PRODUCT);
        return "menu";
    }

    @PostMapping(path = "/order_end", params = "order")
    public String OrdertoEnd(
            @ModelAttribute OrderModel orderModel,
            BindingResult result,
            HttpServletRequest request,
            HttpSession session
    ) {
        session.setAttribute("beforeEdit", request.getRequestURI());
        if (checkIsLogin(session)) {
            Integer tax = (Integer) session.getAttribute("tax");
            Integer sum = (Integer) session.getAttribute("sum");
            Integer total = (Integer) session.getAttribute("total");

            orderModel.setTotalMoney(String.valueOf(sum));
            orderModel.setTotalTax(String.valueOf(tax));
            Integer memberNo = (Integer) session.getAttribute(MenuController.USER_ID_KEY);
            orderModel.setMemberNo(String.valueOf(memberNo));

            Object orderProduct = session.getAttribute(SearchProductController.PRODUCT);
            TreeMap<String, SearchProductResultModel> orderProductMap = new TreeMap<>();
            orderProductMap = (TreeMap<String, SearchProductResultModel>) orderProduct;

            for (Map.Entry<String, SearchProductResultModel> orderelement : orderProductMap.entrySet()) {
                SearchProductResultModel element = orderelement.getValue();
                String code = (String) element.getProductCode();
                String count = element.getProductCnt();
                String price = element.getUnitPrice();
                orderModel.setProductCode(code);
                orderModel.setOrderCount(count);
                orderModel.setOrderPrice(price);


                SearchProductResultModel product = productService.searchProductByCode(code);
                if (Integer.parseInt(product.getDeleteFlg()) != 0) {
                    return "error";
                }
            }
            try {
                orderService.OrderExecute(orderModel, orderProductMap);
                session.removeAttribute(SearchProductController.PRODUCT);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "order_end";
        } else {
            return "login2";
        }
    }


    private boolean checkIsLogin(HttpSession session) {
        //ログイン時にセッションに登録したログイン情報があるか確認
        Integer count = (Integer) session.getAttribute(MenuController.USER_ID_KEY);
        if (count != null) {
            return true;
        } else {
            return false;
        }

    }
}