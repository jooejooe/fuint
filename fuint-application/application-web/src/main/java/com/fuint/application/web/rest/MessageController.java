package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.MtMessage;
import com.fuint.application.service.message.MessageService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.service.token.TokenService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.MtUser;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息相关controller
 * Created by FSQ
 * Contact wx fsq_better
 */
@RestController
@RequestMapping(value = "/rest/message")
public class MessageController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(MyCouponController.class);

    /**
     * 积分服务接口
     */
    @Autowired
    private MessageService messageService;

    /**
     * Token服务接口
     */
    @Autowired
    private TokenService tokenService;

    /**
     * 查询最新一条未读消息
     *
     * @param request  Request对象
     */
    @RequestMapping(value = "/getOne", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getOne(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        if (StringUtils.isEmpty(token)) {
            return getSuccessResult(false);
        }

        MtUser mtUser = tokenService.getUserInfoByToken(token);
        if (null == mtUser) {
            return getSuccessResult(false);
        }

        MtMessage messageInfo = messageService.getOne(mtUser.getId());
        Map<String, Object> outParams = new HashMap();
        if (messageInfo != null) {
            outParams.put("msgId", messageInfo.getId());
            outParams.put("title", messageInfo.getTitle());
            outParams.put("content", messageInfo.getContent());
        }

        ResponseObject responseObject = getSuccessResult(outParams);

        return getSuccessResult(responseObject.getData());
    }

    /**
     * 将消息置为已读
     */
    @RequestMapping(value = "/readed", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject readed(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        MtUser mtUser = tokenService.getUserInfoByToken(token);

        Integer msgId =  request.getParameter("msgId") == null ? 0 :Integer.parseInt(request.getParameter("msgId"));

        if (null == mtUser) {
            return getSuccessResult(false);
        }

        messageService.readMessage(msgId);

        ResponseObject responseObject = getSuccessResult(true);
        return getSuccessResult(responseObject.getData());
    }
}
