package com.fuint.application.web.rest;

import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import com.fuint.application.service.token.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 页面接口controller
 * Created by zach on 2021/4/12.
 */
@RestController
@RequestMapping(value = "/rest/page")
public class PageController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(SignController.class);

    @Autowired
    private TokenService tokenService;

    /**
     * 获取页面数据
     */
    @RequestMapping(value = "/data", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getPageData(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        Map<String, Object> outParams = new HashMap();

        List<String> bannerList = new ArrayList<>();
        List<String> goodsList = new ArrayList<>();
        outParams.put("banner", bannerList);
        outParams.put("goods", goodsList);

        ResponseObject responseObject = getSuccessResult(outParams);

        return getSuccessResult(responseObject.getData());
    }
}
