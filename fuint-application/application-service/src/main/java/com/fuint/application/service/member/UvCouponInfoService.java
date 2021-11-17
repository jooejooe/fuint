package com.fuint.application.service.member;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.UvCouponInfo;
import com.fuint.application.dto.CouponTotalDto;
import java.util.List;
import java.util.Map;

/**
 * 会员用户业务接口
 * Created by FSQ
 * Contact wx fsq_better
 */
public interface UvCouponInfoService {

    /**
     * 分页查询会员卡券消费列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<UvCouponInfo> queryCouponInfoListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 分页查询会员卡券消费列表
     * */
    List<UvCouponInfo> queryCouponInfoByParams(Map<String, Object> params) throws BusinessCheckException;

    /**
     * 查询会员卡券总计
     * */
    CouponTotalDto queryCouponInfoTotalByParams(Map<String, Object> params) throws BusinessCheckException;
}
