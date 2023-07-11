package com.tkmoya.springgradle.controller;

import java.util.List;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import com.tkmoya.springgradle.model.CategoryModel;
import com.tkmoya.springgradle.model.SearchProductModel;
import com.tkmoya.springgradle.model.SearchProductResultModel;
import com.tkmoya.springgradle.service.FormInitService;
import com.tkmoya.springgradle.service.ProductService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class SearchProductController {

	/**
	 * フォーム画面初期化
	 */
	private final FormInitService formInitService;

	   @InitBinder
	    public void initBinder(WebDataBinder binder) {
	        // 未入力のStringをnullに設定する
	        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	    }

	/**
	 * 商品検索/選択のためのサービスクラス
	 */
	private final ProductService productService;

	/**
	 * アプリケーションスコープ
	 */
	private final ServletContext application;

	// アプリケーションスコープキー
	public static final String CATEGORY_LIST = "categoryList";

	// セッションスコープキー
	public static final String SEARCH_PRODUCT_MODEL = "searchProductModel";
	public static final String PRODUCT_PAGE_LIST = "productPageList";
	public static final String PRODUCT = "selectProductMap";

	// メニュー画面から遷移した時の初期画面
	@GetMapping("/order_start")
	public String Login(Model model, HttpSession session) {
		// アプリケーションスコープに商品検索フォーム表示のための値が
		// 保存されていなかったらDBから取得・保存。
		if (application.getAttribute("categoryList") == null) {
			// カテゴリの全リストを取得する
			List<CategoryModel> categoryList = formInitService.getCategoryChoices();
			application.setAttribute(CATEGORY_LIST, categoryList);
			model.addAttribute("categoryList", categoryList);
		}
		model.addAttribute(SEARCH_PRODUCT_MODEL, new SearchProductModel());
		return "/search_product";
	}

	/**
	 * フォームからの入力値を受け取り検索結果表示画面に遷移するメソッド
	 */

	@PostMapping("/search_product")
	public String SearchMember(
			@Validated @ModelAttribute SearchProductModel searchProduct,
			BindingResult result,
			RedirectAttributes redirectAttributes,
			HttpSession session
	) {
		if (result.hasErrors()) {
			return "/search_product";
		}

		redirectAttributes.addFlashAttribute(SEARCH_PRODUCT_MODEL, searchProduct);
		// 検索条件をセッションに保存
		session.setAttribute(SEARCH_PRODUCT_MODEL, searchProduct);

		// 商品検索結果を取得
		List<List<SearchProductResultModel>> productPageList = productService.searchProduct(searchProduct);

		// ページリストをセッションに保存
		session.setAttribute(PRODUCT_PAGE_LIST, productPageList);
		// 初期表示
		return "redirect:/search_product?pageCnt=1";
	}

	/**
	 * 指定されたページを表示する(ページング)
	 */

	@GetMapping("/search_product")
	public static String ProductDisplay(Model model, @RequestParam("pageCnt") int pageCnt, HttpSession session) {
	    session.removeAttribute("error");
		SearchProductModel searchProductModel = (SearchProductModel) session.getAttribute(SEARCH_PRODUCT_MODEL);
		model.addAttribute(SEARCH_PRODUCT_MODEL, searchProductModel);
		// セッションからページリストを取得
		List<List<SearchProductResultModel>> productPageList = (List<List<SearchProductResultModel>>) session
				.getAttribute(PRODUCT_PAGE_LIST);
		// 商品検索後でない場合、画面を初期表示にする
		if (productPageList == null) {
			return "redirect:/order_start";
		}
		if (pageCnt < 1) {
			pageCnt = 1;
		} else if (pageCnt > productPageList.size()) {
			pageCnt = productPageList.size();
		}
		// ページリストから指定ページを取得
		List<SearchProductResultModel> viewPage = productPageList.get(pageCnt - 1);
		if (viewPage.size() == 0) {
		    session.setAttribute("error", "条件に該当する商品は0件です。");
			return "redirect:/order_start";
		}
		// 指定ページとページ数をリクエストスコープに保存し画面表示
		model.addAttribute("viewPage", viewPage);
		model.addAttribute("pageCnt", pageCnt);
		model.addAttribute("totalPage", productPageList.size());

		return "search_product";
	}

	/**
	 * 商品情報を作成後、注文内容確認/個数入力画面へリダイレクト
	 */

	@PostMapping("/select_product")
	public String SelectProduct(
			@Valid @ModelAttribute SearchProductResultModel searchProductResultModel,
			BindingResult result,
			Model model,
			HttpServletRequest request,
			HttpSession session
	) {

		// チェックボックスに一つもチェックが入っていない場合、エラーを出力する。
		if (request.getParameterValues("selectProductCode") == null) {
			request.setAttribute("error", "注文商品を選択してください");
			// 1ページ目に戻して、エラーメッセージを表示させる
			return ProductDisplay(model,1, session);
		}

		// 登録済みのカート情報を取得
		Object cartsession = session.getAttribute(PRODUCT);

		// カート情報が空の場合のための新規TreeMapインスタンス生成
		TreeMap<String, SearchProductResultModel> selectProductMap = new TreeMap<>();

		// カート情報が空でない場合は、セッション情報をselectProductMapに格納
		if (cartsession != null) {
			selectProductMap = (TreeMap<String, SearchProductResultModel>) cartsession;
		}

		// チェックボックスにチェックが入っている商品をselectProductMapに格納
		for (String selectProductCode : request.getParameterValues("selectProductCode")) {
			SearchProductResultModel product = productService.searchProductByCode(selectProductCode);
			String stock = product.getStockCount();

			// もしカートに追加済みの商品が存在していたら、数量を追加する
			if (selectProductMap.containsKey(selectProductCode)) {
				// selectProductCodeを持つ商品情報を変数codeに格納する
				SearchProductResultModel code = selectProductMap.get(selectProductCode);
				// 対象商品の数量を変数countに格納する
				String count = code.getProductCnt();
				if (request.getParameterValues(selectProductCode)[0].equals("")) {
					request.setAttribute("error", "数量を選択してください");
					return ProductDisplay(model,1, session);
				}
				count = Integer.toString(
						Integer.parseInt(count) + Integer.parseInt(request.getParameterValues(selectProductCode)[0]));
				if (!count.matches("[0-9]+")) {
					request.setAttribute("error", "購入数は半角数字で入力してください。");
					return ProductDisplay(model,1, session);
				}
				if (Integer.parseInt(request.getParameterValues(selectProductCode)[0]) < 1 || Integer.parseInt(request.getParameterValues(selectProductCode)[0]) > 999) {
					request.setAttribute("error", "購入数は1～999の数値で入力してください。");
					return ProductDisplay(model,1, session);
				}
				if (Integer.parseInt(count) < 1 || Integer.parseInt(count) > 999) {
					request.setAttribute("error", "購入数は1～999の数値で入力してください。");
					return ProductDisplay(model,1, session);
				}
				if (Integer.parseInt(stock) < Integer.parseInt(count)) {
					String shortage = String.valueOf(Math.abs(Integer.parseInt(stock) - Integer.parseInt(count)));
					request.setAttribute("error", "在庫が足りません。購入数を変更してください。");
					return ProductDisplay(model,1, session);
				}
				product.setProductCnt(count);
			} else {
				product.setProductCnt(request.getParameterValues(selectProductCode)[0]);
				if (!product.getProductCnt().matches("[0-9]+")) {
					request.setAttribute("error", "購入数は半角数字で入力してください。");
					return ProductDisplay(model,1, session);
				}
				if (product.getProductCnt().equals("")) {
					request.setAttribute("error", "数量を選択してください");
					return ProductDisplay(model,1, session);
				}
				if (Integer.parseInt(product.getProductCnt()) < 1 || Integer.parseInt(product.getProductCnt()) > 999) {
					request.setAttribute("error", "購入数は1～999の数値で入力してください。");
					return ProductDisplay(model,1, session);
				}
				if (Integer.parseInt(stock) < Integer.parseInt(product.getProductCnt())) {
					String shortage = String.valueOf(Math.abs(Integer.parseInt(stock) - Integer.parseInt(product.getProductCnt())));
					request.setAttribute("error", "在庫が足りません。購入数を変更してください。");
					return ProductDisplay(model,1, session);
				}
			}
			selectProductMap.put(product.getProductCode(), product);
		}

		// 注文商品をセッションに保存

		selectProductMap.entrySet().stream()
		  .sorted(java.util.Map.Entry.comparingByKey());

		session.setAttribute(PRODUCT, selectProductMap);

		return "add_cart";
	}

	@GetMapping("/add_cart")
	public String AddCart(HttpSession session) {
		return "add_cart";
	}

	@PostMapping("/add_cart")
	public String AddCarttoCartlist(HttpSession session) {
		return "cart_list";
	}

	@GetMapping("/product_detail")
	public String ProductDetail(Model model, HttpServletRequest request, HttpSession session) {
		String val = request.getParameter("productCode");
		SearchProductResultModel product = productService.searchProductByCode(val);
		model.addAttribute("code", product);
		return "product_detail";
	}

	@PostMapping("/product_detail")
	public String ProductDetailtoCartlist(Model model, HttpServletRequest request, HttpSession session) {
		Object cartsession = session.getAttribute(PRODUCT);

		TreeMap<String, SearchProductResultModel> selectProductMap = new TreeMap<>();

		if (cartsession != null) {
			selectProductMap = (TreeMap<String, SearchProductResultModel>) cartsession;
		}
		for (String detailCode : request.getParameterValues("detailCode")) {
			SearchProductResultModel product = productService.searchProductByCode(detailCode);
			String stock = product.getStockCount();
			if (selectProductMap.containsKey(detailCode)) {
				SearchProductResultModel code = selectProductMap.get(detailCode);
				String count = code.getProductCnt();
				if (!request.getParameterValues(detailCode)[0].matches("[0-9]+")) {
					request.setAttribute("error", "購入数は半角数字で入力してください。");
					SearchProductResultModel detailproduct = productService.searchProductByCode(product.getProductCode());
					model.addAttribute("code", detailproduct);
					return "product_detail";
				}
				if (request.getParameterValues(detailCode)[0] == "") {
					request.setAttribute("error", "数量を選択してください");
					SearchProductResultModel detailproduct = productService.searchProductByCode(product.getProductCode());
					model.addAttribute("code", detailproduct);
					return "product_detail";
				}
				if (Integer.parseInt(request.getParameterValues(detailCode)[0]) < 1 || Integer.parseInt(request.getParameterValues(detailCode)[0]) > 999) {
					request.setAttribute("error", "購入数は1～999の数値で入力してください。");
					SearchProductResultModel detailproduct = productService.searchProductByCode(product.getProductCode());
					model.addAttribute("code", detailproduct);
					return "product_detail";
				}
				count = Integer.toString(
						Integer.parseInt(count) + Integer.parseInt(request.getParameterValues(detailCode)[0]));
				if (Integer.parseInt(stock) < Integer.parseInt(count)) {
					String shortage = String.valueOf(Math.abs(Integer.parseInt(stock) - Integer.parseInt(count)));
					request.setAttribute("error", "在庫が"+shortage+"不足しています");
					SearchProductResultModel detailproduct = productService.searchProductByCode(product.getProductCode());
					model.addAttribute("code", detailproduct);
					return "product_detail";
				}
				product.setProductCnt(count);
			} else {
				product.setProductCnt(request.getParameterValues(detailCode)[0]);
				if (!request.getParameterValues(detailCode)[0].matches("[0-9]+")) {
					request.setAttribute("error", "購入数は半角数字で入力してください。");
					SearchProductResultModel detailproduct = productService.searchProductByCode(product.getProductCode());
					model.addAttribute("code", detailproduct);
					return "product_detail";
				}
				if (request.getParameterValues(detailCode)[0].equals("")) {
					request.setAttribute("error", "数量を選択してください");
					SearchProductResultModel detailproduct = productService.searchProductByCode(product.getProductCode());
					model.addAttribute("code", detailproduct);
					return "product_detail";
				}
				if (Integer.parseInt(request.getParameterValues(detailCode)[0]) < 1 || Integer.parseInt(request.getParameterValues(detailCode)[0]) > 999) {
					request.setAttribute("error", "購入数は1～999の数値で入力してください。");
					SearchProductResultModel detailproduct = productService.searchProductByCode(product.getProductCode());
					model.addAttribute("code", detailproduct);
					return "product_detail";
				}
				if (Integer.parseInt(stock) < Integer.parseInt(request.getParameterValues(detailCode)[0])) {
					String shortage = String.valueOf(Math.abs(Integer.parseInt(stock) - Integer.parseInt(request.getParameterValues(detailCode)[0])));
					request.setAttribute("error", "在庫が"+shortage+"不足しています");
					SearchProductResultModel detailproduct = productService.searchProductByCode(product.getProductCode());
					model.addAttribute("code", detailproduct);
					return "product_detail";
				}
			}
			selectProductMap.put(product.getProductCode(), product);
		}

		// 注文商品をセッションに保存
		selectProductMap.entrySet().stream()
		  .sorted(java.util.Map.Entry.comparingByKey());
		session.setAttribute(PRODUCT, selectProductMap);
		return "redirect:/cart_list";
	}
}
