package com.fuint.application.service.banner;

import com.fuint.application.dao.entities.MtBanner;
import com.fuint.application.dao.repositories.MtBannerRepository;
import com.fuint.application.dto.BannerDto;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import com.fuint.application.enums.StatusEnum;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * banner管理业务实现类
 * Created by zach 2021-04-13
 */
@Service
public class BannerServiceImpl implements BannerService {

    private static final Logger log = LoggerFactory.getLogger(BannerServiceImpl.class);

    @Autowired
    private MtBannerRepository bannerRepository;

    /**
     * 分页查询Banner列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtBanner> queryBannerListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        PaginationResponse<MtBanner> paginationResponse = bannerRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 添加Banner信息
     *
     * @param bannerDto
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "添加Banner信息")
    public MtBanner addBanner(BannerDto bannerDto) throws BusinessCheckException {
        MtBanner mtBanner = new MtBanner();
        if (null != bannerDto.getId()) {
            mtBanner.setId(bannerDto.getId());
        }
        mtBanner.setTitle(bannerDto.getTitle());
        mtBanner.setStatus(StatusEnum.ENABLED.getKey());
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dt = format.format(new Date());
            Date addTime = format.parse(dt);
            mtBanner.setUpdateTime(addTime);
            mtBanner.setCreateTime(addTime);
        } catch (ParseException e) {
            throw new BusinessRuntimeException("日期转换异常 " + e.getMessage());
        }
        mtBanner.setDescription(bannerDto.getDescription());

        return bannerRepository.save(mtBanner);
    }

    /**
     * 根据ID获取Banner信息信息
     *
     * @param id Banner信息ID
     * @throws BusinessCheckException
     */
    @Override
    public MtBanner queryBannerById(Integer id) throws BusinessCheckException {
        return bannerRepository.findOne(id);
    }

    /**
     * 根据ID删除Banner信息
     *
     * @param id       BannerID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除店铺")
    public void deleteBanner(Integer id, String operator) throws BusinessCheckException {
        MtBanner MtBanner = this.queryBannerById(id);
        if (null == MtBanner) {
            return;
        }

        MtBanner.setStatus(StatusEnum.DISABLE.getKey());
        MtBanner.setUpdateTime(new Date());

        bannerRepository.save(MtBanner);
    }

    /**
     * 修改Banner
     *
     * @param bannerDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "修改Banner")
    public MtBanner updateBanner(BannerDto bannerDto) throws BusinessCheckException {
        MtBanner MtBanner = this.queryBannerById(bannerDto.getId());
        if (null == MtBanner || StatusEnum.DISABLE.getKey().equals(MtBanner.getStatus())) {
            log.error("该Banner状态异常");
            throw new BusinessCheckException("该Banner状态异常");
        }

        MtBanner.setId(bannerDto.getId());
        MtBanner.setTitle(bannerDto.getTitle());
        MtBanner.setDescription(bannerDto.getDescription());
        MtBanner.setUpdateTime(new Date());
        MtBanner.setOperator(bannerDto.getOperator());
        MtBanner.setStatus(bannerDto.getStatus());

        return bannerRepository.save(MtBanner);
    }

    @Override
    public List<MtBanner> queryBannerListByParams(Map<String, Object> params) throws BusinessCheckException {
        if (MapUtils.isEmpty(params)) {
            params = new HashMap<>();
        }

        Specification<MtBanner> specification = bannerRepository.buildSpecification(params);
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        List<MtBanner> result = bannerRepository.findAll(specification, sort);

        return result;
    }
}
