package com.fuint.application.service.refund;

import com.fuint.application.BaseService;
import com.fuint.application.ResponseObject;
import com.fuint.application.config.Constants;
import com.fuint.application.dao.entities.MtRefund;
import com.fuint.application.dao.repositories.MtRefundRepository;
import com.fuint.application.dto.*;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.enums.RefundStatusEnum;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 售后接口实现类
 * Created by FSQ
 * Contact wx fsq_better
 */
@Service
public class RefundServiceImpl extends BaseService implements RefundService {

    private static final Logger log = LoggerFactory.getLogger(RefundServiceImpl.class);

    @Autowired
    private MtRefundRepository refundRepository;

    @Autowired
    private Environment env;

    /**
     * 分页查询售后订单列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtRefund> getRefundListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        PaginationResponse<MtRefund> paginationResponse = refundRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 获取用户售后订单列表
     * @param paramMap
     * @throws BusinessCheckException
     * */
    @Override
    @Transactional
    public ResponseObject getUserRefundList(Map<String, Object> paramMap) throws BusinessCheckException {
        Integer pageNumber = paramMap.get("pageNumber") == null ? Constants.PAGE_NUMBER : Integer.parseInt(paramMap.get("pageNumber").toString());
        Integer pageSize = paramMap.get("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(paramMap.get("pageSize").toString());
        String userId = paramMap.get("userId") == null ? "0" : paramMap.get("userId").toString();
        String status =  paramMap.get("status") == null ? "": paramMap.get("status").toString();

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(pageNumber);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashedMap();
        searchParams.put("EQ_status", status);
        searchParams.put("EQ_userId", userId);

        paginationRequest.setSearchParams(searchParams);
        paginationRequest.setSortColumn(new String[]{"updateTime desc", "status asc"});
        PaginationResponse<MtRefund> paginationResponse = refundRepository.findResultsByPagination(paginationRequest);

        return getSuccessResult(paginationResponse);
    }

    /**
     * 创建售后订单
     *
     * @param refundDto
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "创建售后订单")
    public MtRefund createRefund(RefundDto refundDto) throws BusinessCheckException {
        MtRefund refund = new MtRefund();
        if (null != refundDto.getId()) {
            refund.setId(refund.getId());
        }

        refund.setOrderSn(refundDto.getOrderSn());
        refund.setUserId(refundDto.getUserId());
        refund.setRemark(refundDto.getRemark());
        refund.setStatus(RefundStatusEnum.CREATED.getKey());

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dt = format.format(new Date());
            Date addTime = format.parse(dt);
            refund.setUpdateTime(addTime);
            refund.setCreateTime(addTime);
        } catch (ParseException e) {
            throw new BusinessCheckException("日期转换异常 " + e.getMessage());
        }

        return refundRepository.save(refund);
    }

    /**
     * 根据ID获取订单详情
     *
     * @param id 订单ID
     * @throws BusinessCheckException
     */
    @Override
    public RefundDto getRefundById(Integer id) throws BusinessCheckException {
        MtRefund refundInfo = refundRepository.findOne(id);
        return null;
    }

    /**
     * 修改订单
     *
     * @param refundDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "修改售后订单")
    public MtRefund updateRefund(RefundDto refundDto) throws BusinessCheckException {
        MtRefund refund = refundRepository.findOne(refundDto.getId());
        if (null == refund || StatusEnum.DISABLE.getKey().equals(refund.getStatus())) {
            log.error("该售后订单状态异常");
            throw new BusinessCheckException("该售后订单状态异常");
        }

        refund.setId(refundDto.getId());
        refund.setUpdateTime(new Date());

        if (null != refundDto.getOperator()) {
            refund.setOperator(refundDto.getOperator());
        }

        if (null != refundDto.getStatus()) {
            refund.setStatus(refundDto.getStatus());
        }

        return refundRepository.save(refund);
    }

    /**
     * 获取售后订单数量
     * */
    @Override
    public Long getRefundCount(Date beginTime, Date endTime) {
        return refundRepository.getRefundCount(beginTime, endTime);
    }
}
