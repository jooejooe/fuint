package com.fuint.application.service.coupon;

import com.fuint.application.dto.CouponDto;
import com.fuint.application.service.confirmlog.ConfirmLogService;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.application.enums.CouponTypeEnum;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dao.repositories.*;
import com.fuint.application.dto.MyCouponDto;
import com.fuint.application.dto.ReqCouponDto;
import com.fuint.application.config.Constants;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.enums.UserCouponStatusEnum;
import com.fuint.application.enums.SendWayEnum;
import com.fuint.application.service.coupongroup.CouponGroupService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.ResponseObject;
import com.fuint.application.service.sendlog.SendLogService;
import com.fuint.application.service.sms.SendSmsInterface;
import com.fuint.application.util.CommonUtil;
import com.fuint.application.util.DateUtil;
import com.fuint.application.util.SeqUtil;
import org.apache.commons.collections.map.HashedMap;
import com.fuint.application.BaseService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fuint.application.dto.ResMyCouponDto;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ?????????????????????
 * Created by FSQ
 * Contact wx fsq_better
 */
@Service
public class CouponServiceImpl extends BaseService implements CouponService {
    private static final Logger log = LoggerFactory.getLogger(CouponServiceImpl.class);

    @Autowired
    private MtCouponRepository couponRepository;

    @Autowired
    private CouponGroupService couponGroupService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private SendLogService sendLogService;

    @Autowired
    private ConfirmLogService confirmLogService;

    @Autowired
    private SendSmsInterface sendSmsService;

    @Autowired
    private MtUserCouponRepository userCouponRepository;

    @Autowired
    private MtConfirmLogRepository confirmLogRepository;

    @Autowired
    private MtSendLogRepository sendLogRepository;

    @Autowired
    private MtStoreRepository mtStoreRepository;

    @Autowired
    private Environment env;

    /**
     * ?????????????????????
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtCoupon> queryCouponListByPagination(PaginationRequest paginationRequest) {
        paginationRequest.setSortColumn(new String[]{"status asc", "id desc"});
        return couponRepository.findResultsByPagination(paginationRequest);
    }

    /**
     * ????????????
     *
     * @param reqCouponDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "????????????")
    public MtCoupon addCoupon(ReqCouponDto reqCouponDto) throws BusinessCheckException {
        MtCoupon coupon = new MtCoupon();

        Date startTime = reqCouponDto.getBeginTime();
        Date endTime = reqCouponDto.getEndTime();
        if (endTime.before(startTime)) {
            throw new BusinessCheckException("?????????????????????????????????????????????");
        }

        coupon.setGroupId(reqCouponDto.getGroupId());
        coupon.setType(reqCouponDto.getType());
        coupon.setName(CommonUtil.replaceXSS(reqCouponDto.getName()));
        coupon.setIsGive(reqCouponDto.getIsGive());
        if (null == reqCouponDto.getPoint()) {
            reqCouponDto.setPoint(0);
        }
        coupon.setPoint(reqCouponDto.getPoint());
        if (null == reqCouponDto.getLimitNum()) {
            reqCouponDto.setLimitNum(0);
        }
        coupon.setLimitNum(reqCouponDto.getLimitNum());
        coupon.setReceiveCode(reqCouponDto.getReceiveCode());

        if (coupon.getType().equals(CouponTypeEnum.TIMER.getKey())) {
            coupon.setPoint(reqCouponDto.getTimerPoint());
            coupon.setReceiveCode(reqCouponDto.getTimerReceiveCode());
        }

        coupon.setStoreIds(CommonUtil.replaceXSS(reqCouponDto.getStoreIds()));

        if (null == reqCouponDto.getSendNum()) {
            reqCouponDto.setSendNum(1);
        }
        coupon.setSendWay(reqCouponDto.getSendWay());

        coupon.setSendNum(reqCouponDto.getSendNum());

        if (null == reqCouponDto.getTotal()) {
            reqCouponDto.setTotal(0);
        }
        coupon.setTotal(reqCouponDto.getTotal());

        coupon.setBeginTime(reqCouponDto.getBeginTime());
        coupon.setEndTime(reqCouponDto.getEndTime());
        coupon.setExceptTime(CommonUtil.replaceXSS(reqCouponDto.getExceptTime()));
        coupon.setDescription(CommonUtil.replaceXSS(reqCouponDto.getDescription()));
        coupon.setRemarks(CommonUtil.replaceXSS(reqCouponDto.getRemarks()));
        coupon.setInRule(CommonUtil.replaceXSS(reqCouponDto.getInRule()));
        coupon.setOutRule(CommonUtil.replaceXSS(reqCouponDto.getOutRule()));
        if (null == reqCouponDto.getAmount()) {
            reqCouponDto.setAmount(new BigDecimal(0));
        }
        coupon.setAmount(reqCouponDto.getAmount());
        String image = reqCouponDto.getImage();
        if (null == image || image.equals("")) {
            image = "";
        }

        coupon.setImage(image);
        coupon.setRemarks(CommonUtil.replaceXSS(reqCouponDto.getRemarks()));

        coupon.setStatus(StatusEnum.ENABLED.getKey());
        //????????????
        coupon.setCreateTime(new Date());

        //????????????
        coupon.setUpdateTime(new Date());

        //?????????
        coupon.setOperator(reqCouponDto.getOperator());

        MtCoupon couponInfo = couponRepository.save(coupon);

        // ?????????????????????????????????????????????????????????
        if (coupon.getType().equals(CouponTypeEnum.COUPON.getKey()) && coupon.getSendWay().equals(SendWayEnum.OFFLINE.getKey())) {
            MtCouponGroup groupInfo = couponGroupService.queryCouponGroupById(coupon.getGroupId());

            Integer total = groupInfo.getTotal() * coupon.getSendNum();
            if (total > 0) {
                String uuid = UUID.randomUUID().toString().replaceAll("-", "");

                for (int i = 1; i <= total; i++) {
                    MtUserCoupon userCoupon = new MtUserCoupon();
                    userCoupon.setCouponId(couponInfo.getId());
                    userCoupon.setGroupId(coupon.getGroupId());
                    userCoupon.setMobile("");
                    userCoupon.setUserId(0);
                    userCoupon.setStatus("E");
                    userCoupon.setCreateTime(new Date());
                    userCoupon.setUpdateTime(new Date());
                    userCoupon.setUuid(uuid);

                    // 12????????????
                    StringBuffer code = new StringBuffer();
                    code.append(SeqUtil.getRandomNumber(4));
                    code.append(SeqUtil.getRandomNumber(4));
                    code.append(SeqUtil.getRandomNumber(4));
                    code.append(SeqUtil.getRandomNumber(4));
                    userCoupon.setCode(code.toString());

                    userCouponRepository.save(userCoupon);
                }
            }
        }

        return coupon;
    }

    /**
     * ??????ID???????????????
     *
     * @param id ???ID
     * @throws BusinessCheckException
     */
    @Override
    public MtCoupon queryCouponById(Integer id) throws BusinessCheckException {
        return couponRepository.findOne(id);
    }

    /**
     * ???????????????ID ?????????????????????
     *
     * @param id       ???ID
     * @param operator ?????????
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "????????????")
    public void deleteCoupon(Long id, String operator) throws BusinessCheckException {
        MtCoupon coupon = this.queryCouponById(id.intValue());
        if (null == coupon) {
            return;
        }
        coupon.setStatus("D");
        //????????????
        coupon.setUpdateTime(new Date());
        //?????????
        coupon.setOperator(operator);
        couponRepository.save(coupon);
    }

    /**
     * ????????????
     *
     * @param reqCouponDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "????????????")
    public MtCoupon updateCoupon(ReqCouponDto reqCouponDto) throws BusinessCheckException {
        MtCoupon coupon = this.queryCouponById(reqCouponDto.getId().intValue());

        if (null == coupon) {
            throw new BusinessCheckException("?????????????????????");
        }

        coupon.setGroupId(reqCouponDto.getGroupId());
        coupon.setName(CommonUtil.replaceXSS(reqCouponDto.getName()));
        coupon.setIsGive(reqCouponDto.getIsGive());
        coupon.setPoint(reqCouponDto.getPoint());
        coupon.setReceiveCode(reqCouponDto.getReceiveCode());
        coupon.setAmount(reqCouponDto.getAmount());
        coupon.setSendWay(reqCouponDto.getSendWay());
        coupon.setSendNum(reqCouponDto.getSendNum());
        coupon.setDescription(CommonUtil.replaceXSS(reqCouponDto.getDescription()));
        coupon.setImage(reqCouponDto.getImage());
        coupon.setRemarks(CommonUtil.replaceXSS(reqCouponDto.getRemarks()));

        coupon.setStatus(StatusEnum.ENABLED.getKey());

        //????????????
        coupon.setUpdateTime(new Date());

        //?????????
        coupon.setOperator(reqCouponDto.getOperator());

        couponRepository.save(coupon);

        return coupon;
    }

    /**
     * ????????????????????????
     * @param paramMap
     * @throws BusinessCheckException
     * */
    @Override
    @Transactional
    public ResponseObject findMyCouponList(Map<String, Object> paramMap) throws BusinessCheckException {
        Integer pageNumber = paramMap.get("pageNumber") == null ? Constants.PAGE_NUMBER : Integer.parseInt(paramMap.get("pageNumber").toString());
        Integer pageSize = paramMap.get("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(paramMap.get("pageSize").toString());
        String userId = paramMap.get("userId") == null ? "0" : paramMap.get("userId").toString();
        String status =  paramMap.get("status") == null ? UserCouponStatusEnum.UNUSED.getKey() : paramMap.get("status").toString();
        String type =  paramMap.get("type") == null ? "": paramMap.get("type").toString();

        // ??????????????????????????????
        if (pageNumber <= 1) {
            List<String> statusList = Arrays.asList(UserCouponStatusEnum.UNUSED.getKey());
            List<MtUserCoupon> data = userCouponRepository.getUserCouponList(Integer.parseInt(userId), statusList);
            for (MtUserCoupon uc : data) {
                MtCoupon coupon = this.queryCouponById(uc.getCouponId());
                if (coupon.getEndTime().before(new Date())) {
                    uc.setStatus(StatusEnum.EXPIRED.getKey());
                    uc.setUpdateTime(new Date());
                    userCouponRepository.save(uc);
                }
            }
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(pageNumber);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashedMap();
        searchParams.put("EQ_status", status);
        searchParams.put("EQ_userId", userId);
        if (StringUtils.isNotEmpty(type)) {
            searchParams.put("EQ_type", type);
        }

        paginationRequest.setSearchParams(searchParams);
        paginationRequest.setSortColumn(new String[]{"id desc", "groupId desc"});

        PaginationResponse<MtUserCoupon> paginationResponse = userCouponRepository.findResultsByPagination(paginationRequest);

        List<MyCouponDto> dataList = new ArrayList<>();

        if (paginationResponse.getContent().size() > 0) {
            for (MtUserCoupon userCouponDto : paginationResponse.getContent()) {
                 MtCoupon couponInfo = this.queryCouponById(userCouponDto.getCouponId());

                 MyCouponDto dto = new MyCouponDto();
                 dto.setId(userCouponDto.getId());
                 dto.setName(couponInfo.getName());
                 dto.setCouponId(couponInfo.getId());
                 dto.setUseRule(couponInfo.getDescription());

                 String image = couponInfo.getImage();
                 String baseImage = env.getProperty("images.upload.url");
                 dto.setImage(baseImage + image);
                 dto.setStatus(userCouponDto.getStatus());
                 dto.setAmount(couponInfo.getAmount());
                 dto.setType(couponInfo.getType());

                 boolean canUse = this.isCouponEffective(couponInfo);
                 if (!userCouponDto.getStatus().equals(UserCouponStatusEnum.UNUSED.getKey())) {
                     canUse = false;
                 }
                 dto.setCanUse(canUse);

                 String effectiveDate = DateUtil.formatDate(couponInfo.getBeginTime(), "yyyy.MM.dd") + "-" + DateUtil.formatDate(couponInfo.getEndTime(), "yyyy.MM.dd");
                 dto.setEffectiveDate(effectiveDate);

                 String tips = "";

                 // ?????????tips
                 if (couponInfo.getType().equals(CouponTypeEnum.COUPON.getKey())) {
                    if (Integer.parseInt(couponInfo.getOutRule()) > 0) {
                        tips = "???" + couponInfo.getOutRule() + "??????";
                    } else {
                        tips = "????????????";
                    }
                 }

                 // ?????????tips
                 if (couponInfo.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
                     tips = "???" + userCouponDto .getAmount() + "????????????" + userCouponDto.getBalance();
                 }

                  // ?????????tips
                  if (couponInfo.getType().equals(CouponTypeEnum.TIMER.getKey())) {
                      Integer confirmNum = confirmLogService.getConfirmNum(userCouponDto.getId());
                      tips = "??????"+ confirmNum +"???????????????" + couponInfo.getOutRule() + "???";
                  }

                  dto.setTips(tips);
                  dataList.add(dto);
            }
        }

        ResMyCouponDto myCouponResponse = new ResMyCouponDto();
        myCouponResponse.setPageNumber(pageNumber);
        myCouponResponse.setPageSize(pageSize);
        myCouponResponse.getTotalRow(paginationResponse.getTotalElements());
        myCouponResponse.setTotalPage(paginationResponse.getTotalPages());
        myCouponResponse.setContent(dataList);

        return getSuccessResult(myCouponResponse);
    }

    /**
     * ??????????????????
     * @param paramMap
     * */
    @Override
    @Transactional
    public ResponseObject findCouponList(Map<String, Object> paramMap) {
        Integer pageNumber = paramMap.get("pageNumber") == null ? Constants.PAGE_NUMBER : Integer.parseInt(paramMap.get("pageNumber").toString());
        Integer pageSize = paramMap.get("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(paramMap.get("pageSize").toString());
        String status =  paramMap.get("status") == null ? StatusEnum.ENABLED.getKey(): paramMap.get("status").toString();
        String type =  paramMap.get("type") == null ? "" : paramMap.get("type").toString();
        Integer userId =  paramMap.get("userId") == null ? 0 : Integer.parseInt(paramMap.get("userId").toString());
        String needPoint =  paramMap.get("needPoint") == null ? "0" : paramMap.get("needPoint").toString();
        String sendWay =  paramMap.get("sendWay") == null ? "front" : paramMap.get("sendWay").toString();

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(pageNumber);
        paginationRequest.setPageSize(pageSize);
        Map<String, Object> searchParams = new HashedMap();

        searchParams.put("EQ_status", status);
        if (StringUtils.isNotEmpty(sendWay)) {
            searchParams.put("EQ_sendWay", sendWay);
        }
        if (StringUtils.isNotEmpty(type)) {
            searchParams.put("EQ_type", type);
        }
        if (Integer.parseInt(needPoint) > 0) {
            searchParams.put("GT_point", needPoint);
        }

        paginationRequest.setSearchParams(searchParams);
        paginationRequest.setSortColumn(new String[]{"id desc", "groupId desc"});

        PaginationResponse<MtCoupon> paginationResponse = couponRepository.findResultsByPagination(paginationRequest);

        List<MtCoupon> dataList = paginationResponse.getContent();
        List<CouponDto> content = new ArrayList<>();
        String baseImage = env.getProperty("images.upload.url");
        for (int i = 0; i < dataList.size(); i++) {
            CouponDto item = new CouponDto();
            BeanUtils.copyProperties(dataList.get(i), item);

            item.setImage(baseImage + item.getImage());

            // ??????????????????????????????????????????
            List<String> statusList = Arrays.asList(UserCouponStatusEnum.UNUSED.getKey(), UserCouponStatusEnum.USED.getKey(), UserCouponStatusEnum.EXPIRE.getKey());
            List<MtUserCoupon> userCoupon = userCouponRepository.getUserCouponListByCouponId(userId, item.getId(), statusList);
            if ((userCoupon.size() >= dataList.get(i).getLimitNum()) && (dataList.get(i).getLimitNum() > 0)) {
                item.setIsReceive(true);
                item.setUserCouponId(userCoupon.get(0).getId());
            }

            // ?????????????????????
            List<Object[]> numData = userCouponRepository.getPeopleNumByCouponId(item.getId());
            Long num;
            if (null == numData || numData.size() < 1) {
                num = 0l;
            } else {
                Object[] obj = numData.get(0);
                num = (Long) obj[1];
            }
            item.setGotNum(num.intValue());

            // ????????????
            Integer leftNum = dataList.get(i).getTotal() - item.getGotNum();
            item.setLeftNum(leftNum >= 0 ? leftNum : 0);

            String sellingPoint = "";

            // ???????????????
            if (item.getType().equals(CouponTypeEnum.COUPON.getKey())) {
                if (Integer.parseInt(item.getOutRule()) > 0) {
                    sellingPoint = "???" + item.getOutRule() + "??????";
                } else {
                    sellingPoint = "????????????";
                }
            }

            // ???????????????
            if (item.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
                String inRuleArr[] = item.getInRule().split(",");
                if (inRuleArr.length > 0) {
                    for (int n = 0; n < inRuleArr.length; n++) {
                        String store[] = inRuleArr[n].split("_");
                        sellingPoint = "??????" + store[0] + "???" + store[1];
                    }
                }
            }

            // ???????????????
            if (item.getType().equals(CouponTypeEnum.TIMER.getKey())) {
                sellingPoint = "??????" + item.getOutRule() + "?????????";
            }

            item.setSellingPoint(sellingPoint);

            content.add(item);
        }

        PageRequest pageRequest = new PageRequest(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        Page page = new PageImpl(content, pageRequest, paginationResponse.getTotalElements());
        PaginationResponse<CouponDto> result = new PaginationResponse(page, CouponDto.class);
        result.setContent(content);
        result.setTotalPages(paginationResponse.getTotalPages());

        return getSuccessResult(result);
    }

    /**
     * ??????????????????????????????
     * @param groupId ????????????
     * @throws BusinessCheckException
     * */
    public List<MtCoupon> queryCouponListByGroupId(Integer groupId) throws BusinessCheckException {
        List<MtCoupon> couponList = couponRepository.queryByGroupId(groupId.intValue());
        return couponList;
    }

    /**
     * ????????????
     *
     * @param couponId ??????ID
     * @param mobile   ?????????
     * @param num      ????????????
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "????????????")
    public void sendCoupon(Integer couponId, String mobile, Integer num, String uuid) throws BusinessCheckException {
        MtUser mtUser = memberService.queryMemberByMobile(mobile);

        if (null == mtUser || !mtUser.getStatus().equals(StatusEnum.ENABLED.getKey())) {
            throw new BusinessCheckException("?????????????????????????????????????????????????????????");
        }

        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();

        // ??????num???
        for (int k = 1; k <= num; k++) {
            MtCoupon couponInfo = this.queryCouponById(couponId);

            // ???????????????
            if (!couponInfo.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                continue;
            }

            for (int j = 1; j <= couponInfo.getSendNum(); j++) {
                MtUserCoupon userCoupon = new MtUserCoupon();
                userCoupon.setCouponId(couponInfo.getId());
                userCoupon.setType(couponInfo.getType());
                userCoupon.setImage(couponInfo.getImage());
                userCoupon.setStoreId(0);
                userCoupon.setAmount(couponInfo.getAmount());
                userCoupon.setBalance(couponInfo.getAmount());
                if (shiroUser != null) {
                    userCoupon.setOperator(shiroUser.getAcctName());
                }
                userCoupon.setGroupId(couponInfo.getGroupId());
                userCoupon.setMobile(mobile);
                userCoupon.setUserId(mtUser.getId());
                userCoupon.setStatus(UserCouponStatusEnum.UNUSED.getKey());
                userCoupon.setCreateTime(new Date());
                userCoupon.setUpdateTime(new Date());

                // 12????????????
                StringBuffer code = new StringBuffer();
                code.append(SeqUtil.getRandomNumber(4));
                code.append(SeqUtil.getRandomNumber(4));
                code.append(SeqUtil.getRandomNumber(4));
                code.append(SeqUtil.getRandomNumber(4));
                userCoupon.setCode(code.toString());
                userCoupon.setUuid(uuid);
                userCouponRepository.save(userCoupon);
            }
        }
    }

    /**
     * ????????????
     *
     * @param userCouponId ????????????ID
     * @param userId  ???????????????ID
     * @param storeId ??????ID
     * @param amount ????????????
     * @param remark ????????????
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "????????????")
    public String useCoupon(Integer userCouponId, Integer userId, Integer storeId, BigDecimal amount, String remark) throws BusinessCheckException {
        MtUserCoupon userCoupon = userCouponRepository.findOne(userCouponId.intValue());

        if (null == userCoupon) {
            throw new BusinessCheckException("?????????????????????");
        } else if (!userCoupon.getStatus().equals("A")) {
            throw new BusinessCheckException("????????????????????????????????????????????????");
        }

        MtStore mtStore = null;
        if (storeId > 0) {
            mtStore = mtStoreRepository.findOne(storeId);
            if (null == mtStore) {
                throw new BusinessCheckException("?????????????????????");
            } else if (!mtStore.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                throw new BusinessCheckException("???????????????????????????????????????");
            }
        }

        // ?????????????????????
        MtCoupon couponInfo = this.queryCouponById(userCoupon.getCouponId());
        Date begin = couponInfo.getBeginTime();
        Date end = couponInfo.getEndTime();
        Date now = new Date();
        if (now.before(begin)) {
            throw new BusinessCheckException("??????????????????????????????");
        }
        if (end.before(now)) {
            throw new BusinessCheckException("??????????????????");
        }

        // ?????????????????????
        Calendar cal = Calendar.getInstance();
        Boolean isWeekend = false;
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            isWeekend = true;
        }

        String exceptTime = couponInfo.getExceptTime();
        if (null != exceptTime && !exceptTime.equals("")) {
            String[] exceptTimeList = exceptTime.split(",");
            if (exceptTimeList.length > 0) {
                for (String timeStr : exceptTimeList) {
                     if (timeStr.equals("weekend")) {
                         if (isWeekend) {
                             throw new BusinessCheckException("?????????????????????????????????");
                         }
                     } else {
                         String[] timeItem = exceptTime.split("_");
                         if (timeItem.length == 2) {
                             try {
                                 Date startTime = DateUtil.parseDate(timeItem[0], "yyyy-MM-dd HH:mm");
                                 Date endTime = DateUtil.parseDate(timeItem[1], "yyyy-MM-dd HH:mm");
                                 if (now.before(endTime) && now.after(startTime)) {
                                     throw new BusinessCheckException("?????????????????????????????????");
                                 }
                             } catch (ParseException pe) {
                                 throw new BusinessCheckException("?????????????????????????????????.");
                             }
                         }
                     }
                }
            }
        }

        if (couponInfo.getType().equals(CouponTypeEnum.COUPON.getKey())) {
            // ?????????????????????????????????
            userCoupon.setStatus(UserCouponStatusEnum.USED.getKey());
        } else if (couponInfo.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
            // ??????????????????????????????
            BigDecimal balance = userCoupon.getBalance();
            BigDecimal newBalance = balance.subtract(amount);

            if (newBalance.compareTo(new BigDecimal("0")) == -1) {
                throw new BusinessCheckException("???????????????????????????");
            }

            if (newBalance.compareTo(new BigDecimal("0")) == 0) {
                userCoupon.setStatus(UserCouponStatusEnum.USED.getKey());
            }

            userCoupon.setBalance(newBalance);
        } else if (couponInfo.getType().equals(CouponTypeEnum.TIMER.getKey())) {
            // ??????????????????????????????????????????
            int confirmCount = 3;
            if (confirmCount >= Integer.parseInt(couponInfo.getOutRule())) {
                userCoupon.setStatus(UserCouponStatusEnum.USED.getKey());
            }
        }

        userCoupon.setUpdateTime(new Date());
        userCoupon.setUsedTime(new Date());
        userCoupon.setStoreId(storeId);
        userCouponRepository.save(userCoupon);

        // ??????????????????
        MtConfirmLog confirmLog = new MtConfirmLog();
        StringBuilder code = new StringBuilder();
        String sStoreId="00000"+storeId.toString();
        code.append(new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
        code.append(sStoreId.substring(sStoreId.length()-4));
        code.append(SeqUtil.getRandomNumber(6));
        confirmLog.setCode(code.toString());

        confirmLog.setCouponId(couponInfo.getId());
        confirmLog.setUserCouponId(userCouponId.intValue());
        confirmLog.setCreateTime(new Date());
        confirmLog.setUpdateTime(new Date());
        confirmLog.setUserId(userCoupon.getUserId());
        confirmLog.setOperatorUserId(userId);
        if (userId > 0) {
            MtUser userInfo = memberService.queryMemberById(userId);
            if (userInfo != null) {
                confirmLog.setOperator(userInfo.getName());
            }
        }
        confirmLog.setStoreId(storeId);
        confirmLog.setStatus(StatusEnum.ENABLED.getKey());
        confirmLog.setAmount(amount);
        confirmLog.setRemark(remark);

        // ???????????????????????????
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser != null) {
            confirmLog.setOperatorFrom("tAccount");
            confirmLog.setOperator(shiroUser.getAcctName());
        }
        confirmLogRepository.save(confirmLog);

        try {
            List<String> mobileList = new ArrayList<>();
            mobileList.add(userCoupon.getMobile());
            Map<String, String> params = new HashMap<>();
            params.put("couponName", couponInfo.getName());
            if (mtStore != null){
                params.put("storeName", mtStore.getName());
            }
            params.put("sn", code.toString());
            sendSmsService.sendSms("confirm-coupon", mobileList, params);
        } catch (Exception e) {
            //empty
        }

        return confirmLog.getCode();
    }

    /**
     * ?????????ID ??????????????????
     *
     * @param id       ???ID
     * @param operator ?????????
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "????????????")
    public void deleteUserCoupon(Integer id, String operator) throws BusinessCheckException {
        MtUserCoupon usercoupon = this.userCouponRepository.findOne(id);
        if (null == usercoupon) {
            return;
        }

        // ?????????????????????????????????
        if(!usercoupon.getStatus().equals(UserCouponStatusEnum.UNUSED.getKey())) {
            throw new BusinessCheckException("?????????????????????????????????");
        }
        usercoupon.setStatus("D");

        // ????????????
        usercoupon.setUpdateTime(new Date());

        // ?????????
        usercoupon.setOperator(operator);

        // ???????????????????????????????????????
        this.sendLogRepository.updateSingleForRemove(usercoupon.getUuid(),"B");

        userCouponRepository.save(usercoupon);
    }

    /**
     * ?????????ID ????????????????????????
     *
     * @param id             ????????????ID
     * @param userCouponId   ????????????ID
     * @param operator       ?????????
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "??????????????????????????????")
    @Transactional
    public void rollbackUserCoupon(Integer id, Integer userCouponId,String operator) throws BusinessCheckException {

        MtConfirmLog mtConfirmLog = this.confirmLogRepository.findOne(id);

        MtUserCoupon usercoupon = this.userCouponRepository.findOne(userCouponId);

        if (null == mtConfirmLog || !mtConfirmLog.getUserCouponId().equals(userCouponId)) {
            throw new BusinessCheckException("??????????????????????????????");
        }

        if (null == usercoupon) {
            throw new BusinessCheckException("????????????????????????");
        }

        // ???????????????????????????,??????????????????????????????????????????,48??????
        Calendar endTimecal = Calendar.getInstance();
        endTimecal.setTime(mtConfirmLog.getCreateTime());
        endTimecal.add(Calendar.DAY_OF_MONTH, 2);

        if (endTimecal.getTime().before(new Date())) {
            throw new BusinessCheckException("????????????????????????48????????????????????????");
        }

        MtCoupon mtCoupon=couponRepository.findOne(usercoupon.getCouponId());

        // ???????????????????????????,??????????????????????????????????????????
        if (mtCoupon.getEndTime().before(new Date())) {
            throw new BusinessCheckException("???????????????????????????");
        }

        // ??????????????????????????????????????????????????????????????????
        if(!usercoupon.getStatus().equals(UserCouponStatusEnum.USED.getKey()) || !mtConfirmLog.getStatus().equals(StatusEnum.ENABLED.getKey())) {
            throw new BusinessCheckException("???????????????????????????????????????");
        }

        // ?????????????????????
        usercoupon.setStatus(UserCouponStatusEnum.UNUSED.getKey());
        usercoupon.setStoreId(null);
        usercoupon.setUsedTime(null);

        // ????????????
        usercoupon.setUpdateTime(new Date());

        // ??????????????????
        userCouponRepository.save(usercoupon);

        // ????????????
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        mtConfirmLog.setOperator(shiroUser.getAcctName());

        mtConfirmLog.setStatus(StatusEnum.DISABLE.getKey());
        mtConfirmLog.setUpdateTime(new Date());
        mtConfirmLog.setCancelTime(new Date());
        confirmLogRepository.save(mtConfirmLog);
    }

    /**
     * ??????ID????????????????????????
     * @param userCouponId ????????????
     * @throws BusinessCheckException
     * */
    @Override
    public MtUserCoupon queryUserCouponById(Integer userCouponId) throws BusinessCheckException {
        MtUserCoupon usercoupon = this.userCouponRepository.findOne(userCouponId);
        return usercoupon;
    }

    /**
     * ????????????????????????
     *
     * @param uuid       ??????ID
     * @param operator   ?????????
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "????????????")
    @Transactional
    public void removeUserCoupon(Long id, String uuid, String operator) throws BusinessCheckException {
        MtSendLog sendLog = this.sendLogService.querySendLogById(id);

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(1);
        paginationRequest.setPageSize(1);
        Map<String, Object> searchParams = new HashedMap();
        searchParams.put("EQ_uuid", uuid);
        paginationRequest.setSearchParams(searchParams);
        PaginationResponse<MtUserCoupon> paginationResponse = userCouponRepository.findResultsByPagination(paginationRequest);

        Long total = paginationResponse.getTotalElements();

        List<Integer> coupondIdList = userCouponRepository.getCouponIdsByUuid(uuid);
        List<Integer> couponIds = new ArrayList<>();
        couponIds.add(0);

        Date nowDate = new Date();

        for (int i = 0; i < coupondIdList.size(); i++) {
            Integer couponId = coupondIdList.get(i);
            MtCoupon couponInfo = this.queryCouponById(couponId);
            if (couponInfo.getStatus().equals("A") && couponInfo.getEndTime().after(nowDate)) {
                couponIds.add(couponId);
            }
        }

        Integer row = this.userCouponRepository.removeUserCoupon(uuid, couponIds, operator);
        if (row.compareTo( total.intValue()) != -1) {
            this.sendLogRepository.updateForRemove(uuid, "D", total.intValue(), 0);
        } else {
            this.sendLogRepository.updateForRemove(uuid, "B", row, (total.intValue()-row));
        }

        return;
    }

    /**
     * ???????????????????????????
     * @param code 12?????????
     * @throws BusinessCheckException
     * */
    @Override
    @OperationServiceLog(description = "?????????????????????")
    public boolean codeExpired(String code) {
        if (StringUtils.isEmpty(code)) {
            return true;
        }
        try {
            Date dateTime = DateUtil.parseDate(code.substring(0, 14), "yyyyMMddHHmmss");

            Long time = dateTime.getTime();
            Long nowTime = System.currentTimeMillis();

            Long seconds = (nowTime - time) / 1000;
            // ??????1??????
            if (seconds > 3600) {
                return true;
            }
        } catch (Exception e) {
            return true;
        }

        return false;
    }

    /**
     * ????????????????????????
     * @param coupon
     * @return
     * */
    @Override
    @OperationServiceLog(description = "????????????????????????")
    public boolean isCouponEffective(MtCoupon coupon) {
        Date begin = coupon.getBeginTime();
        Date end = coupon.getEndTime();
        Date now = new Date();

        // ?????????
        if (now.before(begin)) {
            return false;
        }

        // ?????????
        if (end.before(now)) {
            return false;
        }

        // ????????????
        if (!coupon.getStatus().equals(StatusEnum.ENABLED.getKey())) {
            return false;
        }

        return true;
    }
}
