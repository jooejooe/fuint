package com.fuint.application.web.backend.goods;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.fuint.application.dao.repositories.MtGoodsSpecRepository;
import com.fuint.application.dao.repositories.MtGoodsSkuRepository;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.goods.CateService;
import com.fuint.application.util.CommonUtil;
import com.fuint.exception.BusinessCheckException;
import java.lang.reflect.InvocationTargetException;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dto.*;
import com.fuint.application.service.goods.GoodsService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    private MtGoodsSpecRepository specRepository;

    @Autowired
    private MtGoodsSkuRepository goodskuRepository;

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
     * 删除商品
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
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException,InvocationTargetException,IllegalAccessException {
        Integer goodsId = request.getParameter("goodsId") == null ? 0 : Integer.parseInt(request.getParameter("goodsId"));

        GoodsDto goods = goodsService.getGoodsDetail(goodsId);
        model.addAttribute("goods", goods);

        List<String> images = new ArrayList<>();
        if (goods != null) {
            images = JSONArray.parseArray(goods.getImages(), String.class);
        }
        model.addAttribute("images", images);

        // 商品规格列表
        List<String> specKeyArr = new ArrayList<>();
        List<GoodsSpecItemDto> specArr = new ArrayList<>();
        for (MtGoodsSpec mtGoodsSpec : goods.getSpecList()) {
            if (!specKeyArr.contains(mtGoodsSpec.getName())) {
                specKeyArr.add(mtGoodsSpec.getName());
            }
        }

        int id = 1;
        for (String name : specKeyArr) {
            GoodsSpecItemDto item = new GoodsSpecItemDto();
            List<GoodsSpecChildDto> child = new ArrayList<>();
            for (MtGoodsSpec mtGoodsSpec : goods.getSpecList()) {
                if (mtGoodsSpec.getName().equals(name)) {
                    GoodsSpecChildDto e = new GoodsSpecChildDto();
                    e.setId(mtGoodsSpec.getId());
                    e.setName(mtGoodsSpec.getValue());
                    e.setChecked(true);
                    child.add(e);
                }
            }
            item.setId(id);
            item.setName(name);
            item.setChild(child);
            specArr.add(item);
            id++;
        }

        Map<String, Object> param = new HashMap<>();
        param.put("EQ_status", StatusEnum.ENABLED.getKey());
        List<MtGoodsCate> cateList = cateService.queryCateListByParams(param);
        model.addAttribute("cateList", cateList);

        String imagePath = env.getProperty("images.upload.url");
        model.addAttribute("imagePath", imagePath);

        String specData = JSONObject.toJSONString(specArr);
        model.addAttribute("specData", specData);

        Map<String, Object> skuData = new HashMap<>();
        for (MtGoodsSku sku : goods.getSkuList()) {
            skuData.put("skus["+sku.getSpecIds()+"][skuNo]", sku.getSkuNo());
            skuData.put("skus["+sku.getSpecIds()+"][logo]", (sku.getLogo().length() > 1 ? (imagePath + sku.getLogo()) : ""));
            skuData.put("skus["+sku.getSpecIds()+"][goodsId]", sku.getGoodsId());
            skuData.put("skus["+sku.getSpecIds()+"][stock]", sku.getStock());
            skuData.put("skus["+sku.getSpecIds()+"][price]", sku.getPrice());
            skuData.put("skus["+sku.getSpecIds()+"][linePrice]", sku.getLinePrice());
            skuData.put("skus["+sku.getSpecIds()+"][weight]", sku.getWeight());
        }
        model.addAttribute("skuData", JSONObject.toJSONString(skuData));

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
        String isSingleSpec = CommonUtil.replaceXSS(request.getParameter("isSingleSpec"));
        Integer cateId = request.getParameter("cateId") == null ? 0 : Integer.parseInt(request.getParameter("cateId"));

        Enumeration skuMap = request.getParameterNames();
        List<String> dataArr = new ArrayList<>();
        List<String> item = new ArrayList<>();

        String imagePath = env.getProperty("images.upload.url");

        while (skuMap.hasMoreElements()) {
            String paramName = (String)skuMap.nextElement();
            if (paramName.contains("skus")) {
                String paramValue = request.getParameter(paramName);
                paramName = paramName.replace("[", "_");
                paramName = paramName.replace("]", "");
                String[] s1 = paramName.split("_"); // skus[5-7][image]  skus_5-7_image
                dataArr.add(s1[1] + '_' + s1[2] + '_' + paramValue);
                if (!item.contains(s1[1])) {
                    item.add(s1[1]);
                }
            }
        }

        for (String key : item) {
            Map<String, Object> param = new HashMap<>();
            param.put("EQ_goodsId", goodsId);
            param.put("EQ_specIds", key);

            // 是否已存在
            Specification<MtGoodsSku> specification2 = goodskuRepository.buildSpecification(param);
            Sort sort2 = new Sort(Sort.Direction.ASC, "id");
            List<MtGoodsSku> goodsSkuList = goodskuRepository.findAll(specification2, sort2);
            MtGoodsSku sku = new MtGoodsSku();
            if (goodsSkuList.size() > 0) {
                sku = goodsSkuList.get(0);
            }

            sku.setGoodsId(Integer.parseInt(goodsId));
            sku.setSpecIds(key);
            for (String str :dataArr) {
                String[] ss = str.split("_");
                if (ss[0].equals(key)) {
                   if (ss[1].equals("skuNo")) {
                       String skuNo = ss.length > 2 ? ss[2] : "";
                       sku.setSkuNo(skuNo);
                   } else if (ss[1].equals("logo")) {
                       String logo = ss.length > 2 ? ss[2] : "";
                       logo = logo.replace(imagePath, "");
                       sku.setLogo(logo);
                   } else if (ss[1].equals("stock")) {
                       String skuStock = ss.length > 2 ? ss[2] : "0";
                       sku.setStock(Integer.parseInt(skuStock));
                   } else if (ss[1].equals("price")) {
                       String skuPrice = ss.length > 2 ? ss[2] : "0";
                       sku.setPrice(new BigDecimal(skuPrice));
                   } else if (ss[1].equals("linePrice")) {
                       String skuLinePrice = ss.length > 2 ? ss[2] : "0";
                       sku.setLinePrice(new BigDecimal(skuLinePrice));
                   } else if (ss[1].equals("weight")) {
                       String skuWeight = ss.length > 2 ? ss[2] : "0";
                       sku.setWeight(new BigDecimal(skuWeight));
                   }
                }
            }

            goodskuRepository.save(sku);
        }

        MtGoods info = new MtGoods();
        info.setId(Integer.parseInt(goodsId));
        info.setCateId(cateId);
        info.setName(name);
        info.setGoodsNo(goodsNo);
        info.setIsSingleSpec(isSingleSpec);
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

    /**
     * 保存商品规格
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/goods/goods/saveSpecName")
    @RequestMapping(value = "/saveSpecName", method = RequestMethod.POST)
    @ResponseBody
    public ReqResult saveSpecName(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String goodsId = request.getParameter("goodsId") == null ? "0" : request.getParameter("goodsId");
        String name = request.getParameter("name") == null ? "" : request.getParameter("name");

        if (StringUtils.isEmpty(goodsId)) {
            return null;
        }

        Map<String, Object> param = new HashMap<>();

        param.put("EQ_goodsId", goodsId);
        param.put("EQ_name", name);

        Specification<MtGoodsSpec> specification = specRepository.buildSpecification(param);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        List<MtGoodsSpec> dataList = specRepository.findAll(specification, sort);
        Integer targetId = 0;
        if (dataList.size() < 1) {
            MtGoodsSpec mtGoodsSpec = new MtGoodsSpec();
            mtGoodsSpec.setGoodsId(Integer.parseInt(goodsId));
            mtGoodsSpec.setName(name);
            mtGoodsSpec.setValue("");
            MtGoodsSpec data = specRepository.save(mtGoodsSpec);
            targetId = data.getId();
        } else {
            targetId = dataList.get(0).getId();
        }

        ReqResult reqResult = new ReqResult();
        reqResult.setResult(true);
        reqResult.setCode("200");
        reqResult.setMsg("请求成功");

        Map<String, Object> outParams = new HashMap();
        outParams.put("id", targetId);

        reqResult.setData(outParams);

        return reqResult;
    }

    /**
     * 保存商品规格
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param model    SpringFramework Model对象
     */
    @RequiresPermissions("backend/goods/goods/saveSpecValue")
    @RequestMapping(value = "/saveSpecValue", method = RequestMethod.POST)
    @ResponseBody
    public ReqResult saveSpecValue(HttpServletRequest request, HttpServletResponse response, Model model) {
        String specId = request.getParameter("specId") == null ? "0" : request.getParameter("specId");
        String value = request.getParameter("value") == null ? "" : request.getParameter("value");

        if (StringUtils.isEmpty(specId)) {
            return null;
        }

        MtGoodsSpec goodsSpec = specRepository.findOne(Integer.parseInt(specId));
        Integer targetId = goodsSpec.getId();
        if (goodsSpec.getValue().equals("")) {
            goodsSpec.setValue(value);
            specRepository.save(goodsSpec);
        } else {
            MtGoodsSpec mtGoodsSpec = new MtGoodsSpec();
            mtGoodsSpec.setGoodsId(goodsSpec.getGoodsId());
            mtGoodsSpec.setName(goodsSpec.getName());
            mtGoodsSpec.setValue(value);
            MtGoodsSpec data = specRepository.save(mtGoodsSpec);
            targetId = data.getId();
        }

        ReqResult reqResult = new ReqResult();
        reqResult.setResult(true);
        reqResult.setCode("200");
        reqResult.setMsg("请求成功");

        Map<String, Object> outParams = new HashMap();
        outParams.put("id", targetId);

        reqResult.setData(outParams);

        return reqResult;
    }
}
