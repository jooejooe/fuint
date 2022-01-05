package com.fuint.application.service.refund;

import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.MtRefund;
import com.fuint.application.dto.RefundDto;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 售后业务接口
 * Created by FSQ
 * Contact wx fsq_better
 */
public interface RefundService {
    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtRefund> getRefundListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 获取用户的售后订单
     * @param paramMap 查询参数
     * @throws BusinessCheckException
     * */
    ResponseObject getUserRefundList(Map<String, Object> paramMap) throws BusinessCheckException;

    /**
     * 创建售后订单
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    MtRefund createRefund(RefundDto reqDto) throws BusinessCheckException;

    /**
     * 根据ID获取信息
     *
     * @param id Banner ID
     * @throws BusinessCheckException
     */
    RefundDto getRefundById(Integer id) throws BusinessCheckException;

    /**
     * 更新订单
     * @param reqDto
     * @throws BusinessCheckException
     * */
    MtRefund updateRefund(RefundDto reqDto) throws BusinessCheckException;

    /**
     * 获取订单总数
     * */
    Long getRefundCount(Date beginTime, Date endTime) throws BusinessCheckException;
}
