package com.fuint.application.service.verifycode;

import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import com.fuint.application.dao.entities.MtVerifyCode;
import com.fuint.application.dao.repositories.MtVerifyCodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 验证码业务实现类
 * Created by zach 2019/08/20
 */
@Service
public class VerifyCodeServiceImpl implements VerifyCodeService {

    private static final Logger log = LoggerFactory.getLogger(VerifyCodeServiceImpl.class);

    @Autowired
    private MtVerifyCodeRepository verifyCodeRepository;

    /**
     * 添加验证码
     *
     * @param mobile
     * @param verifycode
     * @throws BusinessCheckException
     */
    public MtVerifyCode addVerifyCode(String mobile, String verifycode,Integer expireSecond) throws BusinessCheckException
    {
        if (null == expireSecond || expireSecond<0) {
            expireSecond=0;
        }

        MtVerifyCode reqVerifyCodeDto=new MtVerifyCode();
        try {
            reqVerifyCodeDto.setMobile(mobile);
            reqVerifyCodeDto.setVerifycode(verifycode);
            reqVerifyCodeDto.setValidflag("0");

            //创建时间
            //格式可以自己根据需要修改
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dt=sdf.format(new Date());
            Date addTime = sdf.parse(dt);

            reqVerifyCodeDto.setAddtime(addTime);

            //验证码过期时间2分钟
            addTime.setTime(addTime.getTime()+2*60*1000);
            Date expiretime = addTime;
            reqVerifyCodeDto.setExpiretime(expiretime);
        } catch (ParseException e) {
            throw new BusinessRuntimeException("日期转换异常" + e.getMessage());
        }

        // 发送验证码2分钟后才能继续发送,取最后一条
        Pageable pageable = new PageRequest(0, 1, Sort.Direction.DESC, "addtime");
        Page<MtVerifyCode> verifyCodesPage = verifyCodeRepository.queryVerifyCodeLastRecord(pageable,mobile);
        List<MtVerifyCode> verifyCodesPageList = verifyCodesPage.getContent();
        if (null == verifyCodesPageList||verifyCodesPageList.size()==0) {
           // 没发过短信
           return verifyCodeRepository.save(reqVerifyCodeDto);
        }
        MtVerifyCode verifyCodeLastRecord=verifyCodesPageList.get(0);
        Long curInt=reqVerifyCodeDto.getAddtime().getTime(); //时间毫秒,长整型
        Long lastInt=verifyCodeLastRecord.getAddtime().getTime();
        Integer diffSecond = (int)((curInt-lastInt) / 1000); //间隔秒数
        if (diffSecond<expireSecond) {
            reqVerifyCodeDto.setValidflag("1");
            return reqVerifyCodeDto;
        }

        // 2,同一个手机号码新的验证码插入
        return verifyCodeRepository.save(reqVerifyCodeDto);
    }

    /**
     * 更改验证码状态
     *
     * @param id
     * @param validFlag
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "更改状态")
    public MtVerifyCode updateValidFlag(Long id, String validFlag)  throws BusinessCheckException {

        MtVerifyCode mtVerifyCode = verifyCodeRepository.findOne(id);
        if (mtVerifyCode == null) {
            log.error("验证码不存在.");
            throw new BusinessCheckException("验证码不存在.");
        }

        MtVerifyCode reVerifyCode=null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dt=sdf.format(new Date());
        Date curDt = null;
        try {
            curDt = sdf.parse(dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mtVerifyCode.setValidflag(validFlag);
        //使用时间
        mtVerifyCode.setUsedtime(curDt);
        //更新成功
        reVerifyCode = verifyCodeRepository.save(mtVerifyCode);
        return reVerifyCode;
    }


    /**
     * 根据手机号,验证码，查询时间
     *
     * @param mobile 电话号码
     * @param verifycode 验证码
     * @throws BusinessCheckException
     */
    public MtVerifyCode checkVerifyCode(String mobile, String verifycode) {
        MtVerifyCode  reVerifyCode = null;
        Date queryTime = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dt = sdf.format(new Date());
            queryTime = sdf.parse(dt);
        } catch (ParseException e) {
            throw new BusinessRuntimeException("日期转换异常" + e.getMessage());
        }
        reVerifyCode = verifyCodeRepository.queryByMobileVerifyCode(mobile, verifycode, queryTime);
        return reVerifyCode;
    }
}
