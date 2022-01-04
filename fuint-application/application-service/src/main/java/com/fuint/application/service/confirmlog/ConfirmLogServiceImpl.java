package com.fuint.application.service.confirmlog;

import com.fuint.application.dao.entities.*;
import com.fuint.application.dao.repositories.MtConfirmLogRepository;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.store.StoreService;
import com.fuint.application.service.member.MemberService;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.application.dto.ConfirmLogDto;
import com.fuint.exception.BusinessCheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * 核销卡券服务
 * Created by FSQ
 * Contact wx fsq_better
 */
@Service
public class ConfirmLogServiceImpl implements ConfirmLogService {

    private static final Logger log = LoggerFactory.getLogger(ConfirmLogServiceImpl.class);

    @Autowired
    private MtConfirmLogRepository confirmLogRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private StoreService storeService;

    /**
     * 分页查询会员卡券核销列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<ConfirmLogDto> queryConfirmLogListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        paginationRequest.setSortColumn(new String[]{"updateTime desc", "createTime desc"});
        PaginationResponse<MtConfirmLog> paginationResponse = confirmLogRepository.findResultsByPagination(paginationRequest);

        List<ConfirmLogDto> content = new ArrayList<>();

        List<MtConfirmLog> dataList = paginationResponse.getContent();
        for (MtConfirmLog log : dataList) {
            MtUser userInfo = memberService.queryMemberById(log.getUserId());
            MtStore storeInfo = storeService.queryStoreById(log.getStoreId());
            MtCoupon couponInfo = couponService.queryCouponById(log.getCouponId());
            ConfirmLogDto item = new ConfirmLogDto();
            item.setId(log.getId());
            item.setCode(log.getCode());
            item.setUserInfo(userInfo);
            item.setStoreInfo(storeInfo);
            item.setCouponInfo(couponInfo);
            item.setUserCouponId(log.getUserCouponId());
            item.setAmount(log.getAmount());
            item.setCreateTime(log.getCreateTime());
            item.setUpdateTime(log.getUpdateTime());
            item.setStatus(log.getStatus());
            item.setRemark(log.getRemark());
            item.setOperator(log.getOperator());
            content.add(item);
        }

        PageRequest pageRequest = new PageRequest((paginationRequest.getCurrentPage() +1), paginationRequest.getPageSize());
        Page page = new PageImpl(content, pageRequest, paginationResponse.getTotalElements());
        PaginationResponse<ConfirmLogDto> result = new PaginationResponse(page, ConfirmLogDto.class);
        result.setTotalPages(paginationResponse.getTotalPages());
        result.setContent(content);

        return result;
    }

    /**
     * 获取卡券核销次数
     * @param userCouponId
     * @return
     * */
    @Override
    public Integer getConfirmNum(Integer userCouponId) {
        Map<String, Object> params = new HashMap<>();
        params.put("EQ_UserCouponId", userCouponId);
        return 0;
    }

    /**
     * 获取核销数量
     * */
    @Override
    public Long getConfirmCount() {
        return confirmLogRepository.getConfirmLogCount();
    }
}
