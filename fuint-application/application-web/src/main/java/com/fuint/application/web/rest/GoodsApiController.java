package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.MtGoodsCate;
import com.fuint.application.dao.entities.MtGoods;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.goods.GoodsService;
import com.fuint.application.service.goods.CateService;
import com.fuint.application.dto.ResCateDto;
import com.fuint.application.service.token.TokenService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
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
    private TokenService tokenService;

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
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException{

        Map<String, Object> param = new HashMap<>();
        param.put("EQ_status", StatusEnum.ENABLED.getKey());
        List<MtGoods> goodsList = goodsService.queryGoodsListByParams(param);

        return getSuccessResult(goodsList);
    }

    /**
     * 获取商品详情
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request) throws BusinessCheckException{
        String goodsId = request.getParameter("goodsId");
        if (StringUtil.isEmpty(goodsId)) {
            return getFailureResult(2000, "商品ID不能为空");
        }

        MtGoods goodsInfo = goodsService.queryGoodsById(Integer.parseInt(goodsId));

        return getSuccessResult(goodsInfo);
    }
}
