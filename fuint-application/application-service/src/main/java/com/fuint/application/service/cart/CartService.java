package com.fuint.application.service.cart;

import com.fuint.application.dao.entities.MtCart;
import com.fuint.exception.BusinessCheckException;
import java.util.List;
import java.util.Map;

/**
 * 购物车业务接口
 * Created by FSQ
 * Contact wx fsq_better
 */
public interface CartService {

    /**
     * 保存购物车
     *
     * @param reqDto
     * @param action + or -
     * @throws BusinessCheckException
     */
    boolean saveCart(MtCart reqDto, String action) throws BusinessCheckException;

    /**
     * 删除购物车
     *
     * @param goodsId  商品ID
     * @param userId   操作人
     * @throws BusinessCheckException
     */
    void removeCart(Integer goodsId, Integer userId) throws BusinessCheckException;

    /**
     * 根据条件搜索分类
     * */
    List<MtCart> queryCartListByParams(Map<String, Object> params) throws BusinessCheckException;
}
