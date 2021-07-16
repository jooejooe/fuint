package com.fuint.application.service.confirmer;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtConfirmer;
import java.util.List;
import java.util.Map;

/**
 * 核销人员管理接口
 * Created by FSQ
 * Contact wx fsq_better
 */
public interface ConfirmerService {

    /**
     * 核销人员查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtConfirmer> queryConfirmerListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加核销人员记录
     *
     * @param reqConfirmerDto
     * @throws BusinessCheckException
     */
    MtConfirmer addConfirmer(MtConfirmer reqConfirmerDto) throws BusinessCheckException;

    /**
     * 修改核销人员信息
     *
     * @param reqConfirmerDto
     * @throws BusinessCheckException
     */
    MtConfirmer updateStore(MtConfirmer reqConfirmerDto) throws BusinessCheckException;

    /**
     * 根据ID获取店铺信息
     *
     * @param id 核销人员id
     * @throws BusinessCheckException
     */
    MtConfirmer queryConfirmerById(Integer id) throws BusinessCheckException;

    /**
     * 审核更改状态(禁用，审核通过)
     *
     * @param ids
     * @throws BusinessCheckException
     */
    Integer updateAuditedStatus(List<Integer> ids, String statusEnum) throws BusinessCheckException;

    /**
     * 根据条件搜索审核人员
     * */
    List<MtConfirmer> queryConfirmerByParams(Map<String, Object> params) throws BusinessCheckException;

    /**
     * 根据手机号获取核销员信息
     *
     * @param mobile 手机
     * @throws BusinessCheckException
     */
    MtConfirmer queryConfirmerByMobile(String mobile) throws BusinessCheckException;

    /**
     * 根据用户ID获取核销员信息
     *
     * @param userId 用户ID
     * @throws BusinessCheckException
     */
    MtConfirmer queryConfirmerByUserId(Integer userId) throws BusinessCheckException;
}
