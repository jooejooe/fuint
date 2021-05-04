package com.fuint.application.web.rest;

import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.service.token.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单类controller
 * Created by zach on 2021/05/3.
 */
@RestController
@RequestMapping(value = "/rest/order")
public class OrderController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(SignController.class);

    @Autowired
    private TokenService tokenService;

    /**
     * 获取订单列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException{
        String userToken = request.getHeader("Access-Token");
        MtUser userInfo = tokenService.getUserInfoByToken(userToken);

        if (userInfo == null) {
            return getFailureResult(1001, "用户未登录");
        }

        Map<String, Object> outParams = new HashMap<String, Object>();

        return getSuccessResult(outParams);
    }

    /**
     * 获取订单详情
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException{
        String userToken = request.getHeader("Access-Token");
        MtUser mtUser = tokenService.getUserInfoByToken(userToken);

        if (mtUser == null) {
            return getFailureResult(1001, "用户未登录");
        }

        return getSuccessResult(null);
    }
}
