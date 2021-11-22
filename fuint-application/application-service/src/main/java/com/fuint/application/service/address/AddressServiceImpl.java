package com.fuint.application.service.address;

import com.fuint.application.dao.entities.MtAddress;
import com.fuint.application.dao.repositories.MtAddressRepository;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import com.fuint.application.enums.StatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

/**
 * 收货地址业务实现类
 * Created by FSQ
 * Contact wx fsq_better
 */
@Service
public class AddressServiceImpl implements AddressService {

    private static final Logger log = LoggerFactory.getLogger(AddressServiceImpl.class);

    @Autowired
    private MtAddressRepository addressRepository;

    /**
     * 添加收货地址
     *
     * @param mtAddress
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "添加收货地址")
    public MtAddress addAddress(MtAddress mtAddress) throws BusinessCheckException {
        if (null != mtAddress.getId()) {
            mtAddress.setId(mtAddress.getId());
        }
        mtAddress.setName(mtAddress.getName());

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dt = format.format(new Date());
            Date addTime = format.parse(dt);
            mtAddress.setUpdateTime(addTime);
            mtAddress.setCreateTime(addTime);
        } catch (ParseException e) {
            throw new BusinessRuntimeException("日期转换异常 " + e.getMessage());
        }

        return addressRepository.save(mtAddress);
    }

    /**
     * 根据ID获取收货地址
     *
     * @param id
     * @throws BusinessCheckException
     */
    @Override
    public MtAddress detail(Integer id) throws BusinessCheckException {
        return addressRepository.findOne(id);
    }

    /**
     * 根据ID删除收货地址
     *
     * @param id
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除收货地址")
    public void deleteAddress(Integer id, String operator) throws BusinessCheckException {
        MtAddress MtAddress = this.detail(id);
        if (null == MtAddress) {
            return;
        }

        MtAddress.setStatus(StatusEnum.DISABLE.getKey());
        MtAddress.setUpdateTime(new Date());

        addressRepository.save(MtAddress);
    }

    /**
     * 修改Banner
     *
     * @param mtAddress
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "修改收货地址")
    public MtAddress updateAddress(MtAddress mtAddress) throws BusinessCheckException {
        return addressRepository.save(mtAddress);
    }

    @Override
    public List<MtAddress> queryListByParams(Map<String, Object> params) {
        Map<String, Object> param = new HashMap<>();

        String status =  params.get("status") == null ? StatusEnum.ENABLED.getKey(): params.get("status").toString();
        param.put("EQ_status", status);

        Specification<MtAddress> specification = addressRepository.buildSpecification(param);
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        List<MtAddress> result = addressRepository.findAll(specification, sort);

        return result;
    }
}
