package com.fuint.application.web.backend.goods;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.fuint.application.enums.OrderStatusEnum;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.goods.CateService;
import com.fuint.application.util.CommonUtil;
import com.fuint.exception.BusinessCheckException;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dto.*;
import com.fuint.application.service.goods.GoodsService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.util.RequestHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

/**
 * 商品管理controller
 * Created by FSQ
 * Contact wx fsq_better
 */
@Controller
@RequestMapping(value = "/backend/goods/goods")
public class goodsController {

    /**
     * 商品服务接口
     */
    @Autowired
    private GoodsService goodsService;

    /**
     * 商品分类服务接口
     */
    @Autowired
    private CateService cateService;

    @Autowired
    private Environment env;

    /**
     * 查询列表
     *
     * @param request
     * @param response
     * @param model
     * @return
     * @throws BusinessCheckException
     */
    @RequestMapping(value = "/list")
    @RequiresPermissions("/backend/goods/goods/list")
    public String queryList(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        PaginationResponse<MtGoods> paginationResponse = goodsService.queryGoodsListByPagination(paginationRequest);

        String imagePath = env.getProperty("images.upload.url");

        model.addAttribute("paginationResponse", paginationResponse);
        model.addAttribute("imagePath", imagePath);

        return "goods/goods/list";
    }

    /**
     * 删除分类
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/goods/goods/delete")
    @RequestMapping(value = "/delete/{id}")
    @ResponseBody
    public ReqResult delete(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("id") Integer id) throws BusinessCheckException {
        List<Integer> ids = new ArrayList<>();
        ids.add(id);

        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        goodsService.deleteGoods(id, operator);

        ReqResult reqResult = new ReqResult();
        reqResult.setResult(true);
        return reqResult;
    }

    /**
     * 添加商品页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequiresPermissions("backend/goods/goods/add")
    @RequestMapping(value = "/add")
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        Integer goodsId = request.getParameter("goodsId") == null ? 0 : Integer.parseInt(request.getParameter("goodsId"));

        MtGoods goods = goodsService.queryGoodsById(goodsId);
        model.addAttribute("goods", goods);

        List<String> images = new ArrayList<>();
        if (goods != null) {
            images = JSONArray.parseArray(goods.getImages(), String.class);
        }
        model.addAttribute("images", images);

        Map<String, Object> param = new HashMap<>();
        param.put("EQ_status", StatusEnum.ENABLED.getKey());
        List<MtGoodsCate> cateList = cateService.queryCateListByParams(param);
        model.addAttribute("cateList", cateList);

        String imagePath = env.getProperty("images.upload.url");
        model.addAttribute("imagePath", imagePath);

        return "goods/goods/add";
    }

    /**
     * 保存商品
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/goods/goods/save")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ReqResult saveHandler(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String goodsId = request.getParameter("goodsId") == null ? "0" : request.getParameter("goodsId");
        if (StringUtils.isEmpty(goodsId)) {
            goodsId = "0";
        }

        String name = CommonUtil.replaceXSS(request.getParameter("name"));
        String description = request.getParameter("editorValue") == null ? "" : request.getParameter("editorValue");
        String images[] = request.getParameterValues("image");
        String sort = request.getParameter("sort") == null ? "0" : request.getParameter("sort");
        String stock = request.getParameter("stock") == null ? "0" : request.getParameter("stock");
        String status = CommonUtil.replaceXSS(request.getParameter("status"));
        String goodsNo = CommonUtil.replaceXSS(request.getParameter("goodsNo"));
        String price = request.getParameter("price") == null ? "0" : request.getParameter("price");
        String linePrice = request.getParameter("linePrice") == null ? "0" : request.getParameter("linePrice");
        String weight = request.getParameter("weight") == null ? "0" : request.getParameter("weight");
        Integer initSale = request.getParameter("initSale") == null ? 0 : Integer.parseInt(request.getParameter("initSale"));
        String salePoint = CommonUtil.replaceXSS(request.getParameter("salePoint"));
        String canUsePoint = CommonUtil.replaceXSS(request.getParameter("canUsePoint"));
        String isMemberDiscount = CommonUtil.replaceXSS(request.getParameter("isMemberDiscount"));
        Integer cateId = request.getParameter("cateId") == null ? 0 : Integer.parseInt(request.getParameter("cateId"));

        MtGoods info = new MtGoods();

        info.setId(Integer.parseInt(goodsId));
        info.setCateId(cateId);
        info.setName(name);
        info.setGoodsNo(goodsNo);
        info.setStock(Integer.parseInt(stock));
        info.setDescription(description);
        if (images != null) {
            info.setLogo(images[0]);
        }
        if (StringUtils.isNotEmpty(sort)) {
            info.setSort(Integer.parseInt(sort));
        }
        info.setStatus(status);
        if (StringUtils.isNotEmpty(price)) {
            info.setPrice(new BigDecimal(price));
        }
        if (StringUtils.isNotEmpty(linePrice)) {
            info.setLinePrice(new BigDecimal(linePrice));
        }
        if (StringUtils.isNotEmpty(weight)) {
            info.setWeight(new BigDecimal(weight));
        }
        info.setInitSale(initSale);
        info.setSalePoint(salePoint);
        info.setCanUsePoint(canUsePoint);
        info.setIsMemberDiscount(isMemberDiscount);

        if (images != null) {
            String imagesJson = JSONObject.toJSONString(images);
            info.setImages(imagesJson);
        }

        String operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
        info.setOperator(operator);

        MtGoods goods = goodsService.saveGoods(info);

        ReqResult reqResult = new ReqResult();
        reqResult.setResult(true);

        Map<String, Object> outParams = new HashMap();
        outParams.put("goods", goods);

        reqResult.setData(outParams);

        return reqResult;
    }
}