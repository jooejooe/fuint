package com.fuint.application.web.rest;

import com.alibaba.fastjson.JSONObject;
import com.fuint.application.dao.entities.MtGoodsCate;
import com.fuint.application.dao.entities.MtGoods;
import com.fuint.application.dao.entities.MtGoodsSku;
import com.fuint.application.dao.entities.MtGoodsSpec;
import com.fuint.application.dto.*;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.goods.GoodsService;
import com.fuint.application.service.goods.CateService;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品类controller
 * Created by FSQ
 * Contact wx fsq_better
 */
@RestController
@RequestMapping(value = "/rest/goodsApi")
public class GoodsApiController extends BaseController {

    /**
     * 商品服务接口
     * */
    @Autowired
    private GoodsService goodsService;

    /**
     * 商品类别服务接口
     * */
    @Autowired
    private CateService cateService;

    @Autowired
    private Environment env;

    /**
     * 获取商品分类列表
     */
    @RequestMapping(value = "/cateList", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject cateList(HttpServletRequest request) throws BusinessCheckException {
        Map<String, Object> param = new HashMap<>();
        param.put("EQ_status", StatusEnum.ENABLED.getKey());

        List<MtGoodsCate> cateList = cateService.queryCateListByParams(param);
        List<MtGoods> goodsList = goodsService.queryGoodsListByParams(param);

        String baseImage = env.getProperty("images.upload.url");
        if (goodsList.size() > 0) {
            for (MtGoods goods : goodsList) {
                goods.setLogo(baseImage + goods.getLogo());
            }
        }

        List<ResCateDto> result = new ArrayList<>();
        for (MtGoodsCate cate : cateList) {
            ResCateDto dto = new ResCateDto();
            dto.setCateId(cate.getId());
            dto.setName(cate.getName());
            dto.setLogo(cate.getLogo());
            List<MtGoods> goodsArr = new ArrayList<>();
            for (MtGoods goods : goodsList) {
                if (goods.getCateId() == cate.getId()) {
                    goodsArr.add(goods);
                }
            }
            dto.setGoodsList(goodsArr);
            result.add(dto);
        }

        return getSuccessResult(result);
    }

    /**
     * 获取商品列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        Map<String, Object> param = new HashMap<>();
        param.put("EQ_status", StatusEnum.ENABLED.getKey());
        List<MtGoods> goodsList = goodsService.queryGoodsListByParams(param);

        return getSuccessResult(goodsList);
    }

    /**
     * 搜索商品
     * */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject search(HttpServletRequest request, Model model) throws BusinessCheckException {
        PaginationRequest paginationRequest = RequestHandler.buildPaginationRequest(request, model);
        PaginationResponse<GoodsDto> paginationResponse = goodsService.queryGoodsListByPagination(paginationRequest);

        return getSuccessResult(paginationResponse);
    }

    /**
     * 获取商品详情
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        String goodsId = request.getParameter("goodsId");
        if (StringUtil.isEmpty(goodsId)) {
            return getFailureResult(2000, "商品ID不能为空");
        }

        GoodsDto goodsDto = goodsService.getGoodsDetail(Integer.parseInt(goodsId));

        GoodsDetailDto goodsDetailDto = new GoodsDetailDto();
        goodsDetailDto.setGoodsNo(goodsDto.getGoodsNo());
        goodsDetailDto.setGoodsId(goodsDto.getId());
        goodsDetailDto.setName(goodsDto.getName());
        goodsDetailDto.setCateId(goodsDto.getCateId());
        goodsDetailDto.setPrice(goodsDto.getPrice());
        goodsDetailDto.setLinePrice(goodsDto.getLinePrice());
        goodsDetailDto.setSalePoint(goodsDto.getSalePoint());
        goodsDetailDto.setSort(goodsDto.getSort());
        goodsDetailDto.setCanUsePoint(goodsDto.getCanUsePoint());
        goodsDetailDto.setIsMemberDiscount(goodsDto.getIsMemberDiscount());

        List<String> images = JSONObject.parseArray(goodsDto.getImages(), String.class);
        List<String> imageList = new ArrayList<>();
        String baseImage = env.getProperty("images.upload.url");
        for (String image : images) {
            imageList.add((baseImage + image));
        }
        goodsDetailDto.setImages(imageList);

        goodsDetailDto.setIsSingleSpec(goodsDto.getIsSingleSpec());
        goodsDetailDto.setLogo(goodsDto.getLogo());
        goodsDetailDto.setStock(goodsDto.getStock());
        goodsDetailDto.setWeight(goodsDto.getWeight());
        goodsDetailDto.setDescription(goodsDto.getDescription());
        goodsDetailDto.setInitSale(goodsDto.getInitSale());
        goodsDetailDto.setStatus(goodsDto.getStatus());

        // 商品规格列表
        List<MtGoodsSpec> goodsSpecList = goodsDto.getSpecList();
        List<String> specNameArr = new ArrayList<>();
        List<MtGoodsSpec> specArr = new ArrayList<>();
        for (MtGoodsSpec mtGoodsSpec : goodsSpecList) {
            if (!specNameArr.contains(mtGoodsSpec.getName())) {
                MtGoodsSpec spec = new MtGoodsSpec();
                spec.setId(mtGoodsSpec.getId());
                spec.setName(mtGoodsSpec.getName());
                specArr.add(spec);
                specNameArr.add(mtGoodsSpec.getName());
            }
        }
        List<GoodsSpecDto> specDtoList = new ArrayList<>();
        for (MtGoodsSpec mtSpec : specArr) {
            GoodsSpecDto dto = new GoodsSpecDto();
            dto.setSpecId(mtSpec.getId());
            dto.setName(mtSpec.getName());
            List<GoodsSpecValueDto> valueList = new ArrayList<>();
            for (MtGoodsSpec spec : goodsSpecList) {
                 if (spec.getName().equals(mtSpec.getName())) {
                     GoodsSpecValueDto valueDto = new GoodsSpecValueDto();
                     valueDto.setSpecValue(spec.getValue());
                     valueDto.setSpecValueId(spec.getId());
                     valueList.add(valueDto);
                 }
            }
            dto.setValueList(valueList);
            specDtoList.add(dto);
        }

        // sku列表
        List<MtGoodsSku> goodsSkuList = goodsDto.getSkuList();
        List<GoodsSkuDto> skuDtoList = new ArrayList<>();
        for (MtGoodsSku sku : goodsSkuList) {
             GoodsSkuDto dto = new GoodsSkuDto();
             dto.setId(sku.getId());
             dto.setLogo(sku.getLogo());
             dto.setGoodsId(sku.getGoodsId());
             dto.setSkuNo(sku.getSkuNo());
             dto.setPrice(sku.getPrice());
             dto.setLinePrice(sku.getLinePrice());
             dto.setStock(sku.getStock());
             dto.setWeight(sku.getWeight());
             dto.setSpecIds(sku.getSpecIds());
             skuDtoList.add(dto);
        }

        if (goodsDetailDto.getIsSingleSpec().equals("Y") && skuDtoList.size() < 1) {
            GoodsSkuDto dto = new GoodsSkuDto();
            dto.setId(goodsDetailDto.getGoodsId());
            dto.setLogo(goodsDetailDto.getLogo());
            dto.setGoodsId(goodsDetailDto.getGoodsId());
            dto.setSkuNo(goodsDetailDto.getGoodsId()+"");
            dto.setPrice(goodsDetailDto.getPrice());
            dto.setLinePrice(goodsDetailDto.getLinePrice());
            dto.setStock(goodsDetailDto.getStock());
            dto.setWeight(goodsDetailDto.getWeight());
            dto.setSpecIds("");
            skuDtoList.add(dto);
        }

        goodsDetailDto.setSpecList(specDtoList);
        goodsDetailDto.setSkuList(skuDtoList);

        return getSuccessResult(goodsDetailDto);
    }
}
