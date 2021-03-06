package com.fuint.application.service.coupongroup;

import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.base.util.RequestHandler;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtCoupon;
import com.fuint.application.dao.entities.MtCouponGroup;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dao.entities.MtUserCoupon;
import com.fuint.application.dao.repositories.MtCouponGroupRepository;
import com.fuint.application.dao.repositories.MtCouponRepository;
import com.fuint.application.dao.repositories.MtUserCouponRepository;
import com.fuint.application.dto.CouponCellDto;
import com.fuint.application.dto.GroupDataDto;
import com.fuint.application.dto.ReqCouponGroupDto;
import com.fuint.application.dto.ReqSendLogDto;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.enums.UserCouponStatusEnum;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.usercoupon.UserCouponService;
import com.fuint.application.service.sendlog.SendLogService;
import com.fuint.application.service.sms.SendSmsInterface;
import com.fuint.application.util.CommonUtil;
import com.fuint.application.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import com.fuint.application.util.XlsUtil;
import org.apache.commons.lang.StringUtils;
import com.fuint.util.StringUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.lang.String;
import java.util.*;
import java.util.regex.Pattern;

/**
 * ?????????????????????
 * Created by FSQ
 * Contact wx fsq_better
 */
@Service
public class CouponGroupServiceImpl implements CouponGroupService {
    private static final Logger log = LoggerFactory.getLogger(CouponGroupServiceImpl.class);

    @Autowired
    private MtCouponGroupRepository couponGroupRepository;

    @Autowired
    private MtCouponRepository couponRepository;

    @Autowired
    private MtUserCouponRepository userCouponRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private SendLogService sendLogService;

    /**
     * ??????????????????
     */
    @Autowired
    private SendSmsInterface sendSmsService;

    /**
     * ??????????????????????????????
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtCouponGroup> queryCouponGroupListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        paginationRequest.setSortColumn(new String[]{"status asc", "id desc"});
        PaginationResponse<MtCouponGroup> paginationResponse = couponGroupRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * ??????????????????
     *
     * @param reqCouponGroupDto
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "??????????????????")
    public MtCouponGroup addCouponGroup(ReqCouponGroupDto reqCouponGroupDto) throws BusinessCheckException {
        MtCouponGroup couponGroup = new MtCouponGroup();

        couponGroup.setName(CommonUtil.replaceXSS(reqCouponGroupDto.getName()));
        couponGroup.setMoney(new BigDecimal("0"));
        couponGroup.setTotal(0);
        couponGroup.setDescription(CommonUtil.replaceXSS(reqCouponGroupDto.getDescription()));
        couponGroup.setStatus(StatusEnum.ENABLED.getKey());

        //????????????
        couponGroup.setCreateTime(new Date());

        //????????????
        couponGroup.setUpdateTime(new Date());

        couponGroup.setNum(0);

        //?????????
        couponGroup.setOperator(reqCouponGroupDto.getOperator());

        couponGroupRepository.save(couponGroup);

        return couponGroup;
    }

    /**
     * ??????????????????ID????????????????????????
     *
     * @param id ????????????ID
     * @throws BusinessCheckException
     */
    @Override
    public MtCouponGroup queryCouponGroupById(Integer id) throws BusinessCheckException {
        return couponGroupRepository.findOne(id.intValue());
    }

    /**
     * ??????????????????ID ????????????????????????
     *
     * @param id       ????????????ID
     * @param operator ?????????
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "??????????????????")
    public void deleteCouponGroup(Integer id, String operator) throws BusinessCheckException {
        MtCouponGroup couponGroup = this.queryCouponGroupById(id);
        if (null == couponGroup) {
            return;
        }

        couponGroup.setStatus(StatusEnum.DISABLE.getKey());

        //????????????
        couponGroup.setUpdateTime(new Date());

        //?????????
        couponGroup.setOperator(operator);

        couponGroupRepository.save(couponGroup);
    }

    /**
     * ??????????????????
     *
     * @param reqcouponGroupDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "??????????????????")
    public MtCouponGroup updateCouponGroup(ReqCouponGroupDto reqcouponGroupDto) throws BusinessCheckException {

        MtCouponGroup couponGroup = this.queryCouponGroupById(reqcouponGroupDto.getId());
        if (null == couponGroup || StatusEnum.DISABLE.getKey().equalsIgnoreCase(couponGroup.getStatus())) {
            log.error("?????????????????????????????????");
            throw new BusinessCheckException("?????????????????????????????????");
        }

        couponGroup.setId(reqcouponGroupDto.getId().intValue());
        couponGroup.setName(CommonUtil.replaceXSS(reqcouponGroupDto.getName()));

        couponGroup.setDescription(CommonUtil.replaceXSS(reqcouponGroupDto.getDescription()));

        couponGroup.setTotal(0);

        // ????????????
        couponGroup.setUpdateTime(new Date());

        // ?????????
        couponGroup.setOperator(reqcouponGroupDto.getOperator());

        couponGroupRepository.save(couponGroup);

        return couponGroup;
    }

    /**
     * ????????????????????????
     *
     * @param id
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    public Integer getCouponNum(Integer id) throws BusinessCheckException {
        Long num = couponRepository.queryNumByGroupId(id);
        return num.intValue();
    }

    /**
     * ?????????????????????
     *
     * @param groupId
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    public BigDecimal getCouponMoney(Integer groupId) throws BusinessCheckException {
        List<MtCoupon> couponList = couponRepository.queryByGroupId(groupId.intValue());
        MtCouponGroup groupInfo = this.queryCouponGroupById(groupId);
        BigDecimal money = BigDecimal.valueOf(0);
        if (couponList.size() > 0) {
            for (int i = 0; i<couponList.size(); i++) {
               BigDecimal number = couponList.get(i).getAmount().multiply(BigDecimal.valueOf(couponList.get(i).getSendNum()));
               number = number.multiply(BigDecimal.valueOf(groupInfo.getTotal()));
               money = money.add(number);
            }
        }
        return money;
    }

    /**
     * ?????????????????????
     *
     * @param  groupId  ??????ID
     * @throws BusinessCheckException
     * */
    @Override
    public Integer getSendedNum(Integer groupId) {
        List<Object[]> list = userCouponRepository.getSendedNum(groupId);
        if (null == list || list.size() < 1) {
            return 0;
        }

        Object[] obj =  list.get(0);
        Integer couponId = (Integer)obj[0];
        Long num = (Long)obj[1];

        MtCoupon couponInfo = couponRepository.findOne(couponId);
        Integer totalNum = num.intValue() / couponInfo.getSendNum();

        return totalNum > 0 ? totalNum : 0;
    }

    /**
     * ??????????????????
     *
     * @param file excel??????
     * @param operator ?????????
     * */
    @Override
    @Transactional
    public String importSendCoupon(MultipartFile file, String operator, String filePath) throws BusinessCheckException {
        String originalFileName = file.getOriginalFilename();
        boolean isExcel2003 = XlsUtil.isExcel2003(originalFileName);
        boolean isExcel2007 = XlsUtil.isExcel2007(originalFileName);

        if (!isExcel2003 && !isExcel2007) {
            log.error("importSendCouponController->uploadFile???{}", "?????????????????????");
            throw new BusinessCheckException("?????????????????????");
        }

        List<List<String>> content = null;
        try {
            content = XlsUtil.readExcelContent(file.getInputStream(), isExcel2003, 1, null, null, null);
        } catch (IOException e) {
            log.error("CouponGroupServiceImpl->parseExcelContent{}", e);
            throw new BusinessCheckException("????????????"+e.getMessage());
        }

        StringBuffer errorMsg = new StringBuffer();
        StringBuffer errorMsgNoGroup = new StringBuffer();
        StringBuffer errorMsgNoNum = new StringBuffer();
        StringBuffer errorMsgNoRegister = new StringBuffer();

        List<CouponCellDto> rows = new ArrayList<>();

        for (int i = 0; i < content.size(); i++) {
            List<Integer> groupIdArr = new ArrayList<>();
            List<Integer> numArr = new ArrayList<>();

            List<String> rowContent = content.get(i);
            String mobile = rowContent.get(0).toString();

            if (StringUtils.isBlank(mobile) || mobile.length() < 11 || mobile.length() > 11) {
                errorMsg.append("???" + i + "?????????,???????????????:"+mobile);
                continue;
            }

            for (int j = 1; j < rowContent.size(); j++) {
                Integer item = 0;
                String cellContent = rowContent.get(j);
                if (null == cellContent || cellContent.equals("")) {
                    continue;
                }

                if (j%2 != 0) {
                    Pattern pattern = Pattern.compile("^[1-9]\\d*$");
                    if (item == null || (!pattern.matcher(cellContent).matches())) {
                        throw new BusinessCheckException("???" + (i+1) + "??????"+ j +"?????????, ??????ID??????");
                    }

                    item = Integer.parseInt(cellContent);
                    if (item < 0) {
                        errorMsg.append("???" + (i+1) + "??????"+ j +"?????????, ??????ID??????");
                        continue;
                    }
                    groupIdArr.add(item);
                } else {
                    Pattern pattern = Pattern.compile("^[1-9]\\d*$");
                    if (item == null || (!pattern.matcher(rowContent.get(j)).matches())) {
                        throw new BusinessCheckException("???" + (i+1) + "??????"+ j +"?????????, ????????????");
                    }

                    item = Integer.parseInt(rowContent.get(j));
                    if (item < 0) {
                        errorMsg.append("???" + (i+1) + "??????"+ j +"?????????, ????????????");
                        continue;
                    }
                    numArr.add(item);
                }
            }

            if (groupIdArr.size() != numArr.size()) {
                throw new BusinessCheckException("???????????????????????????????????????");
            }

            CouponCellDto item = new CouponCellDto();
            item.setMobile(mobile);
            item.setGroupId(groupIdArr);
            item.setNum(numArr);

            rows.add(item);
        }

        if (rows.size() < 1) {
            throw new BusinessCheckException("????????????????????????????????????");
        }

        if (rows.size() > 1000) {
            throw new BusinessCheckException("??????????????????????????????1000???");
        }

        // ???????????????????????????
        Map<String, Integer> groupIdMap = new HashMap<>();
        for (CouponCellDto dto : rows) {
            MtUser userInfo = memberService.queryMemberByMobile(dto.getMobile());
            if (null == userInfo || !userInfo.getStatus().equals("A")) {
                if (StringUtil.isNotBlank(errorMsgNoGroup.toString())) {
                    errorMsgNoGroup.append("," + dto.getMobile());
                } else {
                    errorMsgNoGroup.append("????????????????????????????????????"+dto.getMobile());
                }
            }

            for (int k = 0; k < dto.getGroupId().size(); k++) {
                Integer num = dto.getNum().get(k);
                Integer total = groupIdMap.get(dto.getGroupId().get(k).toString()) == null ? 0 : groupIdMap.get(dto.getGroupId().get(k).toString());
                groupIdMap.put(dto.getGroupId().get(k).toString(), (total+num));
            }
        }

        if (StringUtil.isNotBlank(errorMsgNoRegister.toString())) {
            throw new BusinessCheckException(errorMsgNoRegister.toString());
        }

        for (String key : groupIdMap.keySet()) {
             MtCouponGroup groupInfo = this.queryCouponGroupById(Integer.parseInt(key));

             if (null == groupInfo) {
                 if (StringUtil.isNotBlank(errorMsgNoGroup.toString())) {
                     errorMsgNoGroup.append("," + key);
                 } else {
                     errorMsgNoGroup.append("??????ID????????????"+key);
                 }
                 continue;
             }

             if (!groupInfo.getStatus().equals("A")) {
                 throw new BusinessCheckException("??????ID"+key+"????????????????????????");
             }

             List<MtCoupon> couponList = couponService.queryCouponListByGroupId(Integer.parseInt(key));
             if (couponList.size() < 1) {
                 throw new BusinessCheckException("??????ID"+key+"??????????????????????????????");
             }

             Integer totalNum = groupInfo.getTotal() == null ? 0 : groupInfo.getTotal();
             Integer sendNum = groupIdMap.get(key);
             Integer sendedNum = this.getSendedNum(Integer.parseInt(key));
             if ((totalNum - sendedNum) < sendNum) {
                 Integer needNum = sendNum - (totalNum - sendedNum);
                 if (StringUtil.isNotBlank(errorMsgNoNum.toString())) {
                     errorMsgNoNum.append(";??????ID:"+key+"????????????,???????????????"+needNum+"???");
                 } else {
                     errorMsgNoNum.append("??????ID:" + key + "????????????,???????????????" + needNum + "???");
                 }
             }
        }

        if (StringUtil.isNotBlank(errorMsgNoGroup.toString())) {
            throw new BusinessCheckException(errorMsgNoGroup.toString());
        }

        if (StringUtil.isNotBlank(errorMsgNoNum.toString())) {
            throw new BusinessCheckException(errorMsgNoNum.toString());
        }

        if (StringUtil.isNotBlank(errorMsg.toString())) {
            throw new BusinessCheckException(errorMsg.toString());
        }

        // ????????????
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        // ??????????????????????????????????????????
        try {
            for (CouponCellDto cellDto : rows) {
                // ????????????
                Integer totalNum = 0;
                // ???????????????
                BigDecimal totalMoney = new BigDecimal("0.0");

                for (int gid = 0; gid < cellDto.getGroupId().size(); gid++) {
                    couponService.sendCoupon(cellDto.getGroupId().get(gid).intValue(), cellDto.getMobile(), cellDto.getNum().get(gid), uuid);
                    List<MtCoupon> couponList = couponService.queryCouponListByGroupId(cellDto.getGroupId().get(gid).intValue());
                    // ???????????????????????????
                    for (MtCoupon coupon : couponList) {
                         totalNum = totalNum + (coupon.getSendNum()*cellDto.getNum().get(gid));
                         totalMoney = totalMoney.add((coupon.getAmount().multiply(new BigDecimal(cellDto.getNum().get(gid)).multiply(new BigDecimal(coupon.getSendNum())))));
                    }
                }

                MtUser mtUser = memberService.queryMemberByMobile(cellDto.getMobile());

                // ????????????
                ReqSendLogDto dto = new ReqSendLogDto();
                dto.setType(2);
                dto.setMobile(cellDto.getMobile());
                dto.setUserId(mtUser.getId());
                dto.setFileName(originalFileName);
                dto.setFilePath(filePath);
                dto.setGroupId(0);
                dto.setGroupName("");
                dto.setSendNum(0);
                dto.setOperator(operator);
                dto.setUuid(uuid);
                sendLogService.addSendLog(dto);

                // ????????????
                try {
                    List<String> mobileList = new ArrayList<>();
                    mobileList.add(cellDto.getMobile());

                    Map<String, String> params = new HashMap<>();
                    params.put("totalNum", totalNum+"");
                    params.put("totalMoney", totalMoney+"");
                    sendSmsService.sendSms("received-coupon", mobileList, params);
                } catch (Exception e) {
                    //empty
                }
            }
        } catch (BusinessCheckException e) {
            throw new BusinessCheckException(e.getMessage());
        }
        return uuid;
    }

    /**
     * ????????????
     *
     * @param file excel??????
     * @param request
     * */
    public String saveExcelFile(MultipartFile file, HttpServletRequest request) throws Exception {
        String fileName = file.getOriginalFilename();

        String imageName = fileName.substring(fileName.lastIndexOf("."));

        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        ServletContext servletContext = webApplicationContext.getServletContext();
        String pathRoot = servletContext.getRealPath("");

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        String filePath = "/static/uploadFiles/"+DateUtil.formatDate(new Date(), "yyyyMMdd")+"/";

        String path = filePath + uuid + imageName;

        try {
            File tempFile = new File(pathRoot + path);
            if (!tempFile.getParentFile().exists()) {
                tempFile.getParentFile().mkdirs();
            }

            CommonUtil.saveMultipartFile(file, pathRoot + path);
        } catch (Exception e) {
            //empty
        }

        return path;
    }

    /**
     * ????????????????????????
     *
     * @param groupId ??????ID
     * */
    public GroupDataDto getGroupData(Integer groupId, HttpServletRequest request, Model model) throws BusinessCheckException {
        MtCouponGroup groupInfo = this.queryCouponGroupById(groupId);

        // ???????????????
        Integer sendNum = this.getSendedNum(groupId);

        // ???????????????
        Integer unSendNum = groupInfo.getTotal() - sendNum;

        // ???????????????
        PaginationRequest requestUserCouponUse = RequestHandler.buildPaginationRequest(request, model);
        requestUserCouponUse.getSearchParams().put("EQ_groupId", groupId.toString());
        requestUserCouponUse.getSearchParams().put("EQ_status", UserCouponStatusEnum.USED.getKey());
        PaginationResponse<MtUserCoupon> dataUserCoupon = userCouponService.queryUserCouponListByPagination(requestUserCouponUse);
        Long useNum = dataUserCoupon.getTotalElements();

        // ???????????????
        Date nowDate = new Date();
        Integer expireNum = 0;
        List<MtCoupon> couponList = couponService.queryCouponListByGroupId(groupId);
        List<MtUserCoupon> userCouponList = userCouponRepository.queryExpireNumByGroupId(groupId);
        if (null != userCouponList) {
            for (MtUserCoupon userCoupon: userCouponList) {
                MtCoupon couponInfo = null;
                for (MtCoupon coupon: couponList) {
                    if (userCoupon.getCouponId().toString().equals(coupon.getId().toString())) {
                        couponInfo = coupon;
                        break;
                    }
                }
                if (null == couponInfo) {
                    continue;
                }
                if (nowDate.after(couponInfo.getEndTime())) {
                    expireNum++;
                }
            }
        }

        // ???????????????
        PaginationRequest requestUserCouponCancel = RequestHandler.buildPaginationRequest(request, model);
        requestUserCouponCancel.getSearchParams().put("EQ_groupId", groupId.toString());
        requestUserCouponCancel.getSearchParams().put("EQ_status", UserCouponStatusEnum.DISABLE.getKey());
        PaginationResponse<MtUserCoupon> dataUserCouponCancel = userCouponService.queryUserCouponListByPagination(requestUserCouponCancel);
        Long cancelNum = dataUserCouponCancel.getTotalElements();

        GroupDataDto data = new GroupDataDto();
        data.setSendNum(sendNum);
        data.setUnSendNum(unSendNum);
        data.setUseNum(useNum.intValue());
        data.setExpireNum(expireNum.intValue());
        data.setCancelNum(cancelNum.intValue());

        return data;
    }
}
