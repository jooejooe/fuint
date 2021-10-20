package com.fuint.application.service.usercoupon;

import com.fuint.application.config.Message;
import com.fuint.application.enums.CouponTypeEnum;
import com.fuint.application.enums.UserCouponStatusEnum;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.coupongroup.CouponGroupService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.point.PointService;
import com.fuint.application.util.SeqUtil;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dao.repositories.MtUserCouponRepository;
import com.fuint.application.BaseService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 会员卡券业务实现类
 * Created by FSQ
 * Contact wx fsq_better
 */
@Service
public class UserCouponServiceImpl extends BaseService implements UserCouponService {

    private static final Logger log = LoggerFactory.getLogger(UserCouponServiceImpl.class);

    @Autowired
    private MtUserCouponRepository userCouponRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponGroupService couponGroupService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PointService pointService;

    /**
     * 分页查询券列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtUserCoupon> queryUserCouponListByPagination(PaginationRequest paginationRequest) {
        paginationRequest.setSortColumn(new String[]{"status asc", "id desc"});
        PaginationResponse<MtUserCoupon> paginationResponse = userCouponRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 领取卡券(优惠券、集次卡)
     * @param paramMap
     * @return
     * */
    @Override
    @Transactional
    public boolean receiveCoupon(Map<String, Object> paramMap) throws BusinessCheckException {
        Integer couponId = paramMap.get("couponId") == null ? 0 : Integer.parseInt(paramMap.get("couponId").toString());
        Integer userId = paramMap.get("userId") == null ? 0 : Integer.parseInt(paramMap.get("userId").toString());
        Integer num = paramMap.get("num") == null ? 1 : Integer.parseInt(paramMap.get("num").toString());

        MtCoupon couponInfo = couponService.queryCouponById(couponId);
        if (null == couponInfo) {
            throw new BusinessCheckException(Message.COUPON_NOT_EXIST);
        }

        // 卡券类型检查
        if (couponInfo.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
            throw new BusinessCheckException(Message.COUPON_TYPE_ERROR);
        }

        // 判断卡券是否有效
        boolean isCouponEffective = couponService.isCouponEffective(couponInfo);
        if (!isCouponEffective) {
            throw new BusinessCheckException(Message.COUPON_IS_EXPIRE);
        }

        MtCouponGroup groupInfo = couponGroupService.queryCouponGroupById(couponInfo.getGroupId());
        MtUser userInfo = memberService.queryMemberById(userId);
        if (null == userInfo) {
            throw new BusinessCheckException(Message.USER_NOT_EXIST);
        }

        // 是否已经领取
        List<String> statusList = Arrays.asList(UserCouponStatusEnum.UNUSED.getKey(), UserCouponStatusEnum.USED.getKey(), UserCouponStatusEnum.EXPIRE.getKey());
        List<MtUserCoupon> userCouponData = userCouponRepository.getUserCouponListByCouponId(userId, couponId, statusList);
        if ((userCouponData.size() >= couponInfo.getLimitNum()) && (couponInfo.getLimitNum() > 0)) {
            throw new BusinessCheckException(Message.MAX_COUPON_LIMIT);
        }

        // 积分不足以领取
        if (couponInfo.getPoint() > 0) {
            if (userInfo.getPoint() < couponInfo.getPoint()) {
                throw new BusinessCheckException(Message.POINT_LIMIT);
            }
        }

        // 可领取多张，领取序列号
        StringBuffer uuid = new StringBuffer();
        uuid.append(SeqUtil.getRandomNumber(4));
        uuid.append(SeqUtil.getRandomNumber(4));
        uuid.append(SeqUtil.getRandomNumber(4));
        uuid.append(SeqUtil.getRandomNumber(4));

        for (int i = 1; i <= num; i++) {
            MtUserCoupon userCoupon = new MtUserCoupon();

            userCoupon.setCouponId(couponInfo.getId());
            userCoupon.setType(couponInfo.getType());
            userCoupon.setAmount(couponInfo.getAmount());
            userCoupon.setGroupId(groupInfo.getId());
            userCoupon.setMobile(userInfo.getMobile());
            userCoupon.setUserId(userInfo.getId());
            userCoupon.setStatus(UserCouponStatusEnum.UNUSED.getKey());
            userCoupon.setCreateTime(new Date());
            userCoupon.setUpdateTime(new Date());

            // 12位随机数
            StringBuffer code = new StringBuffer();
            code.append(SeqUtil.getRandomNumber(4));
            code.append(SeqUtil.getRandomNumber(4));
            code.append(SeqUtil.getRandomNumber(4));
            code.append(SeqUtil.getRandomNumber(4));
            userCoupon.setCode(code.toString());
            userCoupon.setUuid(uuid.toString());

            userCouponRepository.save(userCoupon);
        }

        // 是否需要扣除相应积分
        if (couponInfo.getPoint() > 0) {
            MtPoint reqPointDto = new MtPoint();
            reqPointDto.setUserId(userId);
            reqPointDto.setAmount(-couponInfo.getPoint());
            reqPointDto.setDescription("领取"+ couponInfo.getName() + "扣除" +couponInfo.getPoint() +"积分");
            pointService.addPoint(reqPointDto);
        }

        return true;
    }

    /**
     * 预存
     * @param paramMap
     * @return
     * */
    public boolean preStore(Map<String, Object> paramMap) throws BusinessCheckException {
        Integer couponId = paramMap.get("couponId") == null ? 0 : Integer.parseInt(paramMap.get("couponId").toString());
        Integer userId = paramMap.get("userId") == null ? 0 : Integer.parseInt(paramMap.get("userId").toString());
        String param = paramMap.get("param") == null ? "" : paramMap.get("param").toString();
        Integer orderId = paramMap.get("orderId") == null ? 0 : Integer.parseInt(paramMap.get("orderId").toString());

        if (StringUtils.isEmpty(param) || couponId <= 0 || userId <= 0) {
            throw new BusinessCheckException(Message.PARAM_ERROR);
        }

        MtCoupon couponInfo = couponService.queryCouponById(couponId);
        if (null == couponInfo) {
            throw new BusinessCheckException(Message.COUPON_NOT_EXIST);
        }

        MtUser userInfo = memberService.queryMemberById(userId);
        if (null == userInfo) {
            throw new BusinessCheckException(Message.USER_NOT_EXIST);
        }

        String[] paramArr = param.split(",");

        for (int i = 0; i < paramArr.length; i++) {
            String item = paramArr[i];
            if (StringUtils.isNotEmpty(item)) {
                String buyItem = paramArr[i]; // 100_200_1
                String[] buyItemArr = buyItem.split("_");
                if (StringUtils.isNotEmpty(buyItemArr[2])) {
                    Integer numInt = Integer.parseInt(buyItemArr[2]);
                    for (int j = 1; j <= numInt; j++) {
                        if (StringUtils.isNotEmpty(buyItemArr[1])) {
                            this.preStoreItem(couponInfo, userInfo, orderId, new BigDecimal(buyItemArr[1]));
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * 获取会员卡券列表
     * @param userId
     * @param status
     * @return
     * */
    @Override
    public List<MtUserCoupon> getUserCouponList(Integer userId, List<String> status) {
        return userCouponRepository.getUserCouponList(userId, status);
    }

    /**
     * 获取会员卡券详情
     * @param userId
     * @param couponId
     * @return
     * */
    @Override
    public  List<MtUserCoupon> getUserCouponDetail(Integer userId, Integer couponId) {
        return userCouponRepository.findUserCouponDetail(couponId, userId);
    }

    /**
     * 预存单张
     * @param couponInfo
     * @param userInfo
     * @return
     * */
    private boolean preStoreItem(MtCoupon couponInfo, MtUser userInfo, Integer orderId, BigDecimal amount) {
        MtUserCoupon userCoupon = new MtUserCoupon();
        userCoupon.setCouponId(couponInfo.getId());
        userCoupon.setType(couponInfo.getType());
        userCoupon.setGroupId(couponInfo.getGroupId());
        userCoupon.setMobile(userInfo.getMobile());
        userCoupon.setUserId(userInfo.getId());
        userCoupon.setStatus(UserCouponStatusEnum.UNUSED.getKey());
        userCoupon.setCreateTime(new Date());
        userCoupon.setUpdateTime(new Date());

        userCoupon.setOrderId(orderId);
        userCoupon.setAmount(amount);
        userCoupon.setBalance(amount);

        // 12位随机数
        StringBuffer code = new StringBuffer();
        code.append(SeqUtil.getRandomNumber(4));
        code.append(SeqUtil.getRandomNumber(4));
        code.append(SeqUtil.getRandomNumber(4));
        code.append(SeqUtil.getRandomNumber(4));
        userCoupon.setCode(code.toString());
        userCoupon.setUuid(code.toString());

        userCouponRepository.save(userCoupon);
        return true;
    }
}
