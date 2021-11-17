package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.MtGoods;
import com.fuint.application.dao.entities.MtCart;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dto.ResCartDto;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.goods.GoodsService;
import com.fuint.application.service.cart.CartService;
import com.fuint.application.service.token.TokenService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车类controller
 * Created by FSQ
 * Contact wx fsq_better
 */
@RestController
@RequestMapping(value = "/rest/cart")
public class CartController extends BaseController {

    /**
     * Token服务接口
     */
    @Autowired
    private TokenService tokenService;

    /**
     * 购物车服务接口
     * */
    @Autowired
    private CartService cartService;

    /**
     * 商品服务接口
     * */
    @Autowired
    private GoodsService goodsService;

    @Autowired
    private Environment env;

    /**
     * 保存购物车
     */
    @RequestMapping(value = "/save")
    @CrossOrigin
    public ResponseObject save(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer goodsId = param.get("goodsId") == null ? 0 : Integer.parseInt(param.get("goodsId").toString());
        Integer skuId = param.get("skuId") == null ? 0 : Integer.parseInt(param.get("skuId").toString());
        Integer buyNum = param.get("buyNum") == null ? 1 : Integer.parseInt(param.get("buyNum").toString());
        String action = param.get("action") == null ? "+" : param.get("action").toString();

        if (StringUtils.isEmpty(token)) {
            return getFailureResult(1001);
        }

        MtUser mtUser = tokenService.getUserInfoByToken(token);
        if (null == mtUser) {
            return getFailureResult(1001);
        }

        MtCart mtCart = new MtCart();
        mtCart.setGoodsId(goodsId);
        mtCart.setUserId(mtUser.getId());
        mtCart.setNum(buyNum);
        mtCart.setSkuId(skuId);

        cartService.saveCart(mtCart, action);

        return getSuccessResult(true);
    }

    /**
     * 获取购物车列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        Map<String, Object> result = new HashMap<>();
        result.put("list", new ArrayList<>());
        result.put("totalNum", 0);
        result.put("totalPrice", 0);

        Map<String, Object> param = new HashMap<>();

        MtUser mtUser = tokenService.getUserInfoByToken(token);
        if (null == mtUser) {
            return getSuccessResult(result);
        } else {
            param.put("EQ_userId", mtUser.getId().toString());
        }

        param.put("EQ_status", StatusEnum.ENABLED.getKey());
        List<MtCart> cartList = cartService.queryCartListByParams(param);
        List<MtGoods> goodsList = goodsService.queryGoodsListByParams(param);

        String baseImage = env.getProperty("images.upload.url");
        if (goodsList.size() > 0) {
            for (MtGoods goods : goodsList) {
                goods.setLogo(baseImage + goods.getLogo());
            }
        }

        List<ResCartDto> cartDtoList = new ArrayList<>();

        Integer totalNum = 0;
        BigDecimal totalPrice = new BigDecimal("0");
        for (MtCart cart : cartList) {
            totalNum = totalNum + cart.getNum();
            ResCartDto cartDto = new ResCartDto();
            cartDto.setId(cart.getId());
            cartDto.setGoodsId(cart.getGoodsId());
            cartDto.setNum(cart.getNum());
            cartDto.setSkuId(cart.getSkuId());
            cartDto.setUserId(cart.getUserId());

            for (MtGoods goods : goodsList) {
               if (cart.getGoodsId() == goods.getId()) {
                   cartDto.setGoodsInfo(goods);
               }
            }

            totalPrice = totalPrice.add(cartDto.getGoodsInfo().getPrice().multiply(new BigDecimal(cart.getNum())));

            cartDtoList.add(cartDto);
        }

        result.put("list", cartDtoList);
        result.put("totalNum", totalNum);
        result.put("totalPrice", totalPrice);

        return getSuccessResult(result);
    }
}
