package com.fuint.application.service.confirmer;

import com.fuint.application.dao.entities.*;
import com.fuint.application.dto.ConfirmLogDto;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import com.fuint.application.dao.repositories.MtConfirmerRepository;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.store.StoreService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.sms.SendSmsInterface;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 核销人员管理接口实现类
 * Created by FSQ
 * Contact wx fsq_better
 */
@Service
public class ConfirmerServiceImpl implements ConfirmerService {

    private static final Logger log = LoggerFactory.getLogger(ConfirmerServiceImpl.class);

    @Autowired
    private MtConfirmerRepository confirmerRepository;
    /**
     * 会员用户信息管理服务接口
     */
    @Autowired
    private MemberService memberService;

    /**
     * 短信发送接口
     */
    @Autowired
    private SendSmsInterface sendSmsService;

    /**
     * 短信发送接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 核销人员查询列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtConfirmer> queryConfirmerListByPagination(PaginationRequest paginationRequest) {
        PaginationResponse<MtConfirmer> paginationResponse = confirmerRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 添加核销人员记录
     *
     * @param reqConfirmerDto
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "添加核销人员记录")
    public MtConfirmer addConfirmer(MtConfirmer reqConfirmerDto) throws BusinessCheckException {
        MtConfirmer mtConfirmer = new MtConfirmer();
        Boolean smsFlag = Boolean.FALSE;
        try {
            // 格式可以自己根据需要修改
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dt = sdf.format(new Date());
            Date addtime = sdf.parse(dt);
            reqConfirmerDto.setUpdateTime(addtime);

            // 编辑不需要重新写创建时间
            if(null == reqConfirmerDto.getId()) {
                reqConfirmerDto.setCreateTime(addtime);
                reqConfirmerDto.setAuditedStatus(StatusEnum.UnAudited.getKey());
            } else if(reqConfirmerDto.getAuditedStatus().equals(StatusEnum.ENABLED.getKey()) ) {
                Integer id = reqConfirmerDto.getId();
                MtConfirmer mtConfirmerTemp;
                mtConfirmerTemp = confirmerRepository.findOne(id);

                if (null == mtConfirmerTemp) {
                    throw new BusinessCheckException("核销人员信息异常.");
                }

                // 关联userid
                MtUser mtUser = new MtUser();
                mtUser.setMobile(reqConfirmerDto.getMobile());
                mtUser.setName(reqConfirmerDto.getRealName());
                MtUser mtUser1 = memberService.queryMemberByMobile(reqConfirmerDto.getMobile());

                if (mtUser1 == null) {
                    mtUser1 = memberService.addMember(mtUser);
                    smsFlag = Boolean.TRUE;
                }

                // 关联核销人员账户id
                reqConfirmerDto.setUserId(mtUser1.getId());
                reqConfirmerDto.setUpdateTime(new Date());
                if (!mtConfirmerTemp.getAuditedStatus().equals(StatusEnum.ENABLED.getKey())) {
                    reqConfirmerDto.setAuditedTime(new Date());
                }

                // 发送短信通知
                if (smsFlag.equals(Boolean.TRUE)) {
                    MtStore mtStore = storeService.queryStoreById(reqConfirmerDto.getStoreId());
                    mtConfirmer.setStoreName(mtStore.getName());
                    List<String> mobileList = new ArrayList<String>();
                    mobileList.add(reqConfirmerDto.getMobile());
                    try {
                        Map<String, String> params = new HashMap<>();
                        params.put("name", reqConfirmerDto.getRealName());
                        params.put("storeName", mtConfirmer.getStoreName());
                        sendSmsService.sendSms("confirmer-authed", mobileList, params);
                    } catch (Exception e) {
                        throw new BusinessCheckException("短信发送失败.");
                    }
                }
            }
        } catch (ParseException e) {
            throw new BusinessRuntimeException("日期转换异常" + e.getMessage());
        }

        return confirmerRepository.save(reqConfirmerDto);
    }

    /**
     * 修改核销人员信息
     *
     * @param reqConfirmerDto
     * @throws BusinessCheckException
     */
    @Override
    public MtConfirmer updateStore(MtConfirmer reqConfirmerDto) throws BusinessCheckException {
        MtConfirmer mtConfirmer = addConfirmer(reqConfirmerDto);
        return  mtConfirmer;
    }

    /**
     * 根据ID获取核销人员信息
     *
     * @param id 核销人员id
     * @throws BusinessCheckException
     */
    @Override
    public MtConfirmer queryConfirmerById(Integer id) {
        MtConfirmer mtConfirmer = confirmerRepository.findOne(id);
        return  mtConfirmer;
    }

    /**
     * 审核更改状态(禁用，审核通过)
     *
     * @param ids
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "核销人员审核更改状态")
    public Integer updateAuditedStatus(List<Integer> ids, String statusEnum) throws BusinessCheckException {
        Integer i = 0;
        Boolean flag = false;
        StatusEnum[] sees = StatusEnum.values();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dt=sdf.format(new Date());
        Date currentDT;
        try {
            currentDT = sdf.parse(dt);
        } catch (ParseException e) {
            throw new BusinessCheckException("日期转换错误!");
        }
        // 遍历枚举key ,如果枚举合法,返回成功
        for (StatusEnum se : sees) {
            if (se.getKey().equals(statusEnum)) {
                flag = true;
                i = confirmerRepository.updateStatus(ids,statusEnum,currentDT);
                if (StatusEnum.ENABLED.getKey().equals(statusEnum)) {
                    // 审核通过，转移到普通会员表
                    for(Integer id : ids) {
                        MtConfirmer mtConfirmer = confirmerRepository.findOne(id);
                        if(mtConfirmer != null) {
                            MtUser tmemberInfo = new MtUser();
                            tmemberInfo.setMobile(mtConfirmer.getMobile());
                            tmemberInfo.setName(mtConfirmer.getRealName());
                            MtUser mtUser1 = memberService.queryMemberByMobile(mtConfirmer.getMobile());
                            if (mtUser1 == null) {
                                mtUser1 = memberService.addMember(tmemberInfo);
                            }

                            // 关联核销人员账户id
                            mtConfirmer.setUserId(mtUser1.getId());
                            mtConfirmer.setUpdateTime(currentDT);
                            mtConfirmer.setAuditedTime(currentDT);
                            confirmerRepository.save(mtConfirmer);

                            // 发送短信通知
                            MtStore mtStore = storeService.queryStoreById(mtConfirmer.getStoreId());
                            if (mtStore == null) {
                                mtStore = new MtStore();
                                mtStore.setName("全部店铺");
                            }
                            mtConfirmer.setStoreName(mtStore.getName());
                            List<String> mobileList = new ArrayList<String>();
                            mobileList.add(mtConfirmer.getMobile());

                            try {
                                Map<String, String> params = new HashMap<>();
                                params.put("name", mtConfirmer.getRealName());
                                params.put("storeName", mtConfirmer.getStoreName());
                                sendSmsService.sendSms("confirmer-authed", mobileList, params);
                            } catch (Exception e) {
                                throw new BusinessCheckException("短信发送失败");
                            }

                        }
                    }
                }
            }
        }

        if (Boolean.FALSE.equals(flag)) {
            throw new BusinessCheckException("枚举值不存在.");
        }

        return i;
    }

    /**
     * 根据条件搜索核销人员
     * */
    @Override
    public List<MtConfirmer> queryConfirmerByParams(Map<String, Object> params) {
        if (MapUtils.isEmpty(params)) {
            params = new HashMap<>();
        }

        Specification<MtConfirmer> specification = confirmerRepository.buildSpecification(params);
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        List<MtConfirmer> result = confirmerRepository.findAll(specification, sort);
        return result;
    }

    /**
     * 根据手机号获取核销人员
     *
     * @param mobile 手机号
     * @throws BusinessCheckException
     */
    @Override
    public MtConfirmer queryConfirmerByMobile(String mobile) {
        MtConfirmer mtConfirmer = confirmerRepository.queryConfirmerByMobile(mobile);
        return mtConfirmer;
    }

    /**
     * 根据用户ID获取核销员信息
     *
     * @param userId 用户ID
     * @throws BusinessCheckException
     */
    @Override
    public MtConfirmer queryConfirmerByUserId(Integer userId) {
        MtConfirmer mtConfirmer = confirmerRepository.queryConfirmerByUserId(userId);
        return mtConfirmer;
    }
}
