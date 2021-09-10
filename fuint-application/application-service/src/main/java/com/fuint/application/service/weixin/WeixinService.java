package com.fuint.application.service.weixin;

import com.alibaba.fastjson.JSONObject;
import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.MtOrder;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dto.UserOrderDto;
import com.fuint.exception.BusinessCheckException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 微信业务接口
 * Created by FSQ
 * Contact wx fsq_better
 */
public interface WeixinService {
    ResponseObject createPrepayOrder(MtUser userInfo, MtOrder orderInfo, Integer payAmount, Integer giveAmount, String ip) throws BusinessCheckException;
    boolean paymentCallback(UserOrderDto orderInfo) throws BusinessCheckException;;
    Map<String,String> processResXml(HttpServletRequest request);
    void processRespXml(HttpServletResponse response, boolean flag);
    JSONObject wxLogin(String code);
    String getPhoneNumber(String encryptedData, String session_key, String iv);
}