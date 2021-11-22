package com.fuint.application.service.address;

import com.fuint.application.dao.entities.MtAddress;
import com.fuint.exception.BusinessCheckException;
import java.util.List;
import java.util.Map;

/**
 * 收货地址业务接口
 * Created by FSQ
 * Contact wx fsq_better
 */
public interface AddressService {

    /**
     * 添加收货地址
     *
     * @param mtAddress
     * @throws BusinessCheckException
     */
    MtAddress addAddress(MtAddress mtAddress) throws BusinessCheckException;

    /**
     * 根据ID获取Banner信息
     *
     * @param id Banner ID
     * @throws BusinessCheckException
     */
    MtAddress detail(Integer id) throws BusinessCheckException;

    /**
     * 根据ID删除信息
     *
     * @param id       ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void deleteAddress(Integer id, String operator) throws BusinessCheckException;

    /**
     * 更新收货地址
     * @param mtAddress
     * @throws BusinessCheckException
     * */
    MtAddress updateAddress(MtAddress mtAddress) throws BusinessCheckException;

    /**
     * 根据条件搜索
     * */
    List<MtAddress> queryListByParams(Map<String, Object> params) throws BusinessCheckException;
}
