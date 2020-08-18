package com.fuint.application.service.usercoupon;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtCoupon;
import com.fuint.application.dao.entities.MtUserCoupon;
import com.fuint.application.dto.ReqCouponDto;
import com.fuint.application.ResponseObject;
import java.util.List;
import java.util.Map;

/**
 * 用户优惠券业务接口
 * Created by zach on 2019/9/20
 */
public interface UserCouponService {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtUserCoupon> queryUserCouponListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;
}
