package com.fuint.application.service.goods;

import com.fuint.application.dao.entities.MtGoods;
import com.fuint.application.dto.GoodsDto;
import com.fuint.application.dto.GoodsSpecValueDto;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 商品业务接口
 * Created by FSQ
 * Contact wx fsq_better
 */
public interface GoodsService {
    /**
     * 分页查询商品列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtGoods> queryGoodsListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 保存商品
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    MtGoods saveGoods(MtGoods reqDto) throws BusinessCheckException;

    /**
     * 根据ID获取商品信息
     *
     * @param id ID
     * @throws BusinessCheckException
     */
    MtGoods queryGoodsById(Integer id) throws BusinessCheckException;

    /**
     * 根据ID获取商品详情
     *
     * @param id ID
     * @throws BusinessCheckException
     */
    GoodsDto getGoodsDetail(Integer id) throws InvocationTargetException, IllegalAccessException;

    /**
     * 根据ID 删除
     *
     * @param id       ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void deleteGoods(Integer id, String operator) throws BusinessCheckException;

    /**
     * 根据条件搜索分类
     * */
    List<MtGoods> queryGoodsListByParams(Map<String, Object> params) throws BusinessCheckException;

    /**
     * 根据skuId获取规格列表
     * */
    List<GoodsSpecValueDto> getSpecListBySkuId(Integer skuId) throws BusinessCheckException;
}
