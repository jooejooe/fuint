package com.fuint.application.service.usergrade;

import com.fuint.application.dao.entities.MtUserGrade;
import com.fuint.application.dao.repositories.MtUserGradeRepository;
import com.fuint.application.enums.UserGradeCatchTypeEnum;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.enums.StatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员等级业务接口实现类
 * Created by FSQ
 * Contact wx fsq_better
 */
@Service
public class UserGradeServiceImpl implements UserGradeService {

    private static final Logger log = LoggerFactory.getLogger(UserGradeServiceImpl.class);

    @Autowired
    private MtUserGradeRepository userGradeRepository;

    /**
     * 分页查询会员等级列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtUserGrade> queryUserGradeListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        PaginationResponse<MtUserGrade> paginationResponse = userGradeRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 添加会员等级信息
     *
     * @param mtUserGrade
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "添加会员等级信息")
    public MtUserGrade addUserGrade(MtUserGrade mtUserGrade) throws BusinessCheckException {
        return userGradeRepository.save(mtUserGrade);
    }

    /**
     * 根据ID获取会员等级信息
     *
     * @param id 会员等级ID
     * @return
     * @throws BusinessCheckException
     */
    @Override
    public MtUserGrade queryUserGradeById(Integer id) throws BusinessCheckException {
        MtUserGrade mtUserGrade = userGradeRepository.findOne(id);
        return mtUserGrade;
    }

    /**
     * 修改会员等级
     *
     * @param mtUserGrade
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "修改会员等级")
    public MtUserGrade updateUserGrade(MtUserGrade mtUserGrade) throws BusinessCheckException {
        MtUserGrade userGrade = userGradeRepository.findOne(mtUserGrade.getId());

        if (null != userGrade) {
           userGradeRepository.save(mtUserGrade);
        }

        return mtUserGrade;
    }

    /**
     * 根据ID删除会员等级
     *
     * @param id       ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除会员等级")
    public Integer deleteUserGrade(Integer id, String operator) throws BusinessCheckException {
        MtUserGrade mtUserGrade = this.queryUserGradeById(id);
        if (null == mtUserGrade) {
            return 0;
        }

        mtUserGrade.setStatus(StatusEnum.DISABLE.getKey());

        userGradeRepository.save(mtUserGrade);

        return mtUserGrade.getId();
    }

    /**
     * 获取默认的会员等级
     *
     * @throws BusinessCheckException
     */
    @Override
    public MtUserGrade getInitUserGrade() {
        Map<String, Object> param = new HashMap<>();
        param.put("EQ_status", StatusEnum.ENABLED.getKey());

        Specification<MtUserGrade> specification = userGradeRepository.buildSpecification(param);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        List<MtUserGrade> dataList = userGradeRepository.findAll(specification, sort);

        MtUserGrade result = new MtUserGrade();
        result.setId(0);

        for (MtUserGrade grade : dataList) {
            if (grade.getCatchType().equals(UserGradeCatchTypeEnum.INIT.getKey())) {
                return grade;
            }
        }

         return result;
    }

    /**
     * 获取付费会员等级列表
     *
     * @throws BusinessCheckException
     * */
    @Override
    public List<MtUserGrade> getPayUserGradeList() {
        Map<String, Object> param = new HashMap<>();
        param.put("EQ_status", StatusEnum.ENABLED.getKey());
        param.put("EQ_catchType", UserGradeCatchTypeEnum.PAY.getKey());

        Specification<MtUserGrade> specification = userGradeRepository.buildSpecification(param);
        Sort sort = new Sort(Sort.Direction.DESC, "catchValue");
        List<MtUserGrade> dataList = userGradeRepository.findAll(specification, sort);

        return dataList;
    }
}
