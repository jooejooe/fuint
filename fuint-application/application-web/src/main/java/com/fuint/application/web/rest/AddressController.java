package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.MtAddress;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.address.AddressService;
import com.fuint.application.service.token.TokenService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收货地址controller
 * Created by FSQ
 * Contact wx fsq_better
 */
@RestController
@RequestMapping(value = "/rest/address")
public class AddressController extends BaseController {

    /**
     * Token服务接口
     */
    @Autowired
    private TokenService tokenService;

    /**
     * 收货地址服务接口
     * */
    @Autowired
    private AddressService addressService;

    /**
     * 保存收货地址
     */
    @RequestMapping(value = "/save")
    @CrossOrigin
    public ResponseObject save(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String name = param.get("name") == null ? "" : param.get("name").toString();

        if (StringUtils.isEmpty(token)) {
            return getFailureResult(1001);
        }

        MtUser mtUser = tokenService.getUserInfoByToken(token);
        if (null == mtUser) {
            return getFailureResult(1001);
        }

        MtAddress mtAddress = new MtAddress();
        mtAddress.setName(name);
        addressService.addAddress(mtAddress);

        return getSuccessResult(true);
    }

    /**
     * 获取收货地址列表
     */
    @RequestMapping(value = "/list")
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> param = new HashMap<>();

        MtUser mtUser = tokenService.getUserInfoByToken(token);
        if (null == mtUser) {
            return getFailureResult(1001);
        } else {
            param.put("EQ_userId", mtUser.getId().toString());
        }

        param.put("EQ_status", StatusEnum.ENABLED.getKey());

        List<MtAddress> addressList = addressService.queryListByParams(param);

        result.put("list", addressList);

        return getSuccessResult(result);
    }
}
