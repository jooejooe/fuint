package com.fuint.application.service.point;

import com.fuint.application.dao.entities.MtPoint;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dao.repositories.MtPointRepository;
import com.fuint.application.dao.repositories.MtUserRepository;
import com.fuint.application.enums.StatusEnum;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 积分管理业务实现类
 * Created by FSQ
 * Contact wx fsq_better
 */
@Service
public class PointServiceImpl implements PointService {

    private static final Logger log = LoggerFactory.getLogger(PointServiceImpl.class);

    @Autowired
    private MtPointRepository pointRepository;

    @Autowired
    private MtUserRepository userRepository;

    /**
     * 分页查询积分列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtPoint> queryPointListByPagination(PaginationRequest paginationRequest) {
        PaginationResponse<MtPoint> paginationResponse = pointRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 添加积分记录
     *
     * @param mtPoint
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    public void addPoint(MtPoint mtPoint) {
        if (mtPoint.getUserId() < 0) {
           return;
        }
        mtPoint.setStatus(StatusEnum.ENABLED.getKey());
        mtPoint.setCreateTime(new Date());
        mtPoint.setUpdateTime(new Date());

        MtUser user = userRepository.findOne(mtPoint.getUserId());
        Integer newAmount = user.getPoint() + mtPoint.getAmount();
        if (newAmount < 0) {
           return;
        }
        user.setPoint(newAmount);

        userRepository.save(user);
        pointRepository.save(mtPoint);
    }
}
