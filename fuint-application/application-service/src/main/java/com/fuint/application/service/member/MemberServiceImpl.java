package com.fuint.application.service.member;

import com.fuint.application.dao.entities.MtUserGrade;
import com.fuint.application.dao.repositories.MtUserGradeRepository;
import com.fuint.application.service.usergrade.UserGradeService;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dao.entities.MtUserCoupon;
import com.fuint.application.dao.repositories.MtUserCouponRepository;
import com.fuint.application.dao.repositories.MtUserRepository;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.sms.SendSmsInterface;
import com.fuint.application.service.token.TokenService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSONObject;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 会员业务接口实现类
 * Created by FSQ
 * Contact wx fsq_better
 */
@Service
public class MemberServiceImpl implements MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Autowired
    private MtUserRepository userRepository;

    @Autowired
    private MtUserGradeRepository userGradeRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private  MtUserCouponRepository mtUserCouponRepository;

    /**
     * 短信发送接口
     */
    @Autowired
    private SendSmsInterface sendSmsService;

    /**
     * 会员等级接口
     * */
    @Autowired
    private UserGradeService userGradeService;

    /**
     * 分页查询会员用户列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtUser> queryMemberListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        PaginationResponse<MtUser> paginationResponse = userRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 添加会员用户信息
     *
     * @param mtUser
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "添加会员信息")
    public MtUser addMember(MtUser mtUser) throws BusinessCheckException {
        Boolean newFlag = Boolean.FALSE;
        try {
            // 创建时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dt = sdf.format(new Date());
            Date addtime = sdf.parse(dt);
            mtUser.setUpdateTime(addtime);
            // 添加的时候需要加入插入时间
            if (null == mtUser.getId()) {
                newFlag = Boolean.TRUE;
                mtUser.setCreateTime(addtime);
                mtUser.setStatus(StatusEnum.ENABLED.getKey());
                MtUser mtUser_1 = userRepository.queryMemberByMobile(mtUser.getMobile());
                if (mtUser_1 != null) {
                    throw new BusinessCheckException("手机号码已经存在");
                }
            }
        } catch (ParseException e) {
            throw new BusinessRuntimeException("日期转换异常" + e.getMessage());
        }

        userRepository.save(mtUser);

        // 清token缓存
        tokenService.removeTokenLikeMobile(mtUser.getMobile());

        // 新增用户发短信通知
        if (newFlag.equals(Boolean.TRUE) && mtUser.getStatus().equals(StatusEnum.ENABLED.getKey())) {
            // 发送短信
            List<String> mobileList = new ArrayList<String>();
            mobileList.add(mtUser.getMobile());
            // 短信模板
            try {
                Map<String, String> params = new HashMap<>();
                sendSmsService.sendSms("register-sms", mobileList, params);
            } catch (Exception e) {
                throw new BusinessCheckException("注册短信发送失败.");
            }
        }

        return mtUser;
    }

    /**
     * 通过手机号添加会员
     *
     * @param mobile
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "添加会员信息")
    public MtUser addMemberByMobile(String mobile) throws BusinessCheckException {
        MtUser mtUser = new MtUser();
        mtUser.setName(mobile);
        mtUser.setMobile(mobile);
        MtUserGrade grade = userGradeService.getInitUserGrade();
        mtUser.setGradeId(grade.getId()+"");
        mtUser.setCreateTime(new Date());
        mtUser.setUpdateTime(new Date());
        mtUser.setBalance(new BigDecimal("0.00"));
        mtUser.setPoint(0);
        mtUser.setDescription("验证码登录自动注册");
        mtUser.setIdcard("");
        mtUser.setStatus(StatusEnum.ENABLED.getKey());

        userRepository.save(mtUser);
        return mtUser;
    }

    /**
     * 根据手机号获取会员用户信息
     *
     * @param mobile 手机号
     * @throws BusinessCheckException
     */
    @Override
    public MtUser queryMemberByMobile(String mobile) {
        MtUser mtUser = userRepository.queryMemberByMobile(mobile);
        return mtUser;
    }

    /**
     * 根据会员用户ID获取会员响应DTO
     *
     * @param id 会员ID
     * @return
     * @throws BusinessCheckException
     */
    @Override
    public MtUser queryMemberById(Integer id) {
        MtUser mtUser = userRepository.findMembersById(id);
        return mtUser;
    }

    /**
     * 根据openId获取会员信息(为空就注册)
     *
     * @param openId
     * @throws BusinessCheckException
     */
    @Override
    public MtUser queryMemberByOpenId(String openId, JSONObject userInfo) throws BusinessCheckException {
        MtUser user = userRepository.queryMemberByOpenId(openId);

        if (user == null) {
            MtUser mtUser = new MtUser();
            String nickName = userInfo.getString("nickName");
            String mobile = StringUtils.isNotEmpty(userInfo.getString("phone")) ? userInfo.getString("phone") : "";

            // 昵称为空，用手机号
            if (StringUtils.isEmpty(nickName) && StringUtils.isNotEmpty(mobile)) {
                nickName = mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
            }

            mtUser.setMobile(mobile);
            mtUser.setName(nickName);
            mtUser.setOpenId(openId);
            MtUserGrade grade = userGradeService.getInitUserGrade();
            mtUser.setGradeId(grade.getId()+"");
            mtUser.setCreateTime(new Date());
            mtUser.setUpdateTime(new Date());
            mtUser.setBalance(new BigDecimal("0.00"));
            mtUser.setPoint(0);
            mtUser.setDescription("微信登录自动注册");
            mtUser.setIdcard("");
            mtUser.setStatus(StatusEnum.ENABLED.getKey());
            userRepository.save(mtUser);
            user = userRepository.queryMemberByOpenId(openId);
        } else {
            // 更新昵称和手机号码
            String nickName = "";
            String name = user.getName();
            String mobile = StringUtils.isNotEmpty(userInfo.getString("phone")) ? userInfo.getString("phone") : "";
            if (StringUtils.isEmpty(name)) {
                nickName = userInfo.getString("nickName");
                if (StringUtils.isEmpty(nickName) && StringUtils.isNotEmpty(mobile)) {
                    nickName = mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
                }
            }

            if (StringUtils.isNotEmpty(nickName)) {
                user.setName(nickName);
            }
            if (StringUtils.isNotEmpty(mobile)) {
                user.setMobile(mobile);
            }

            user.setUpdateTime(new Date());
            userRepository.save(user);
        }

        return user;
    }

    /**
     * 根据等级ID获取会员等级信息
     *
     * @param id 会员组ID
     * @throws BusinessCheckException
     */
    @Override
    public MtUserGrade queryMemberGradeByGradeId(Integer id) {
        MtUserGrade gradeInfo = userGradeRepository.findOne(id);
        return gradeInfo;
    }

    /**
     * 修改会员用户
     *
     * @param mtUser
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "修改会员用户")
    public MtUser updateMember(MtUser mtUser) throws BusinessCheckException {
        MtUser mtUser_new = this.addMember(mtUser);
        return mtUser_new;
    }

    /**
     * 根据店铺ID 删除店铺信息
     *
     * @param id       店铺信息ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除会员")
    public Integer deleteMember(Integer id, String operator) throws BusinessCheckException {
        MtUser mtUser = this.queryMemberById(id);
        if (null == mtUser) {
            return 0;
        }

        List<String> statusList = Arrays.asList("A");
        List<MtUserCoupon> listMtUserCoupon = mtUserCouponRepository.getUserCouponList(id, statusList);
        if (listMtUserCoupon!=null && listMtUserCoupon.size()>0 ) {
            log.error(id.toString()+"该会员用户有未使用的卡券，不能被删除!");
            throw new BusinessCheckException("该会员用户有未使用的卡券，不能被删除!");
        }

        mtUser.setStatus(StatusEnum.DISABLE.getKey());

        // 清token缓存
        tokenService.removeTokenLikeMobile(mtUser.getMobile());

        // 修改时间
        mtUser.setUpdateTime(new Date());

        userRepository.save(mtUser);
        return 1;
    }

    /**
     *根据创建时间参数查询会员用户信息
     *
     * @param  params
     * @throws BusinessCheckException
     */
    @Override
    public List<MtUser> queryEffectiveMemberRange(Map<String, Object> params) {
        log.info("############ 根据创建时间参数查询会员用户信息 #################.");
        if (MapUtils.isEmpty(params)) {
            params = new HashMap<>();
        }
        Date beginTime = (Date) params.get("beginTime");
        Date endTime = (Date) params.get("endTime");

        List<MtUser> result = userRepository.queryEffectiveMemberRange(beginTime,endTime);
        return result;
    }

    /**
     * 更改状态(禁用)
     *
     * @param ids
     * @throws com.fuint.exception.BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "更改状态")
    public Integer updateStatus(List<Integer> ids, String StatusEnum) throws BusinessCheckException {
        List<MtUser> mtUsers = userRepository.findMembersByIds(ids);
        if (mtUsers == null) {
            log.error("会员不存在.");
            throw new BusinessCheckException("会员不存在.");
        }

        Integer i = userRepository.updateStatus(ids,StatusEnum.toString());
        for (MtUser m:mtUsers) {
            // 清token缓存
            tokenService.removeTokenLikeMobile(m.getMobile());
        }

        return i;
    }

    @Override
    public List<MtUser> queryMembersByParams(Map<String, Object> params) {
        if (MapUtils.isEmpty(params)) {
            params = new HashMap<>();
        }

        Specification<MtUser> specification = userRepository.buildSpecification(params);
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        List<MtUser> result = userRepository.findAll(specification, sort);

        return result;
    }

    /**
     * 根据条件搜索会员分组
     * */
    @Override
    public List<MtUserGrade> queryMemberGradeByParams(Map<String, Object> params) {
        if (MapUtils.isEmpty(params)) {
            params = new HashMap<>();
        }

        Specification<MtUserGrade> specification = userGradeRepository.buildSpecification(params);
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        List<MtUserGrade> result = userGradeRepository.findAll(specification, sort);

        return result;
    }

    /**
     * 获取会员数量
     * */
    @Override
    public Long getUserCount() {
        return userRepository.getUserCount();
    }
}
