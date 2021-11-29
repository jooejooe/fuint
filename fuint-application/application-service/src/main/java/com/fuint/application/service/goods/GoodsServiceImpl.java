package com.fuint.application.service.goods;

import com.fuint.application.dao.entities.MtGoods;
import com.fuint.application.dao.entities.MtGoodsSku;
import com.fuint.application.dao.entities.MtGoodsSpec;
import com.fuint.application.dao.repositories.MtGoodsRepository;
import com.fuint.application.dao.repositories.MtGoodsSkuRepository;
import com.fuint.application.dao.repositories.MtGoodsSpecRepository;
import com.fuint.application.dto.GoodsDto;
import com.fuint.application.dto.GoodsSpecValueDto;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.domain.Specification;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.enums.StatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 商品业务实现类
 * Created by FSQ
 * Contact wx fsq_better
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    private static final Logger log = LoggerFactory.getLogger(CateServiceImpl.class);

    @Autowired
    private MtGoodsRepository goodsRepository;

    @Autowired
    private MtGoodsSpecRepository goodsSpecRepository;

    @Autowired
    private MtGoodsSkuRepository goodsSkuRepository;

    @Autowired
    private Environment env;

    /**
     * 分页查询商品列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtGoods> queryGoodsListByPagination(PaginationRequest paginationRequest) {
        PaginationResponse<MtGoods> paginationResponse = goodsRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 添加商品
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "保存商品")
    public MtGoods saveGoods(MtGoods reqDto) {
        MtGoods mtGoods = new MtGoods();
        if (reqDto.getId() > 0) {
            mtGoods = this.queryGoodsById(reqDto.getId());
        }

        if (StringUtils.isNotEmpty(reqDto.getName())) {
            mtGoods.setName(reqDto.getName());
        }
        if (StringUtils.isNotEmpty(reqDto.getStatus())) {
            mtGoods.setStatus(StatusEnum.ENABLED.getKey());
        }
        if (StringUtils.isNotEmpty(reqDto.getLogo())) {
            mtGoods.setLogo(reqDto.getLogo());
        }
        if (StringUtils.isNotEmpty(reqDto.getIsSingleSpec())) {
            mtGoods.setIsSingleSpec(reqDto.getIsSingleSpec());
        }
        if (StringUtils.isNotEmpty(reqDto.getDescription())) {
            mtGoods.setDescription(reqDto.getDescription());
        }
        if (StringUtils.isNotEmpty(reqDto.getOperator())) {
            mtGoods.setOperator(reqDto.getOperator());
        }
        if (reqDto.getCateId() > 0) {
            mtGoods.setCateId(reqDto.getCateId());
        }
        if (StringUtils.isNotEmpty(reqDto.getGoodsNo())) {
            mtGoods.setGoodsNo(reqDto.getGoodsNo());
        }
        if (reqDto.getSort() > 0) {
            mtGoods.setSort(reqDto.getSort());
        }
        if (reqDto.getPrice().compareTo(new BigDecimal("0")) > 0) {
            mtGoods.setPrice(reqDto.getPrice());
        }
        if (reqDto.getLinePrice().compareTo(new BigDecimal("0")) > 0) {
            mtGoods.setLinePrice(reqDto.getLinePrice());
        }
        if (reqDto.getWeight().compareTo(new BigDecimal("0")) > 0) {
            mtGoods.setWeight(reqDto.getWeight());
        }
        if (reqDto.getInitSale() > 0) {
            mtGoods.setInitSale(reqDto.getInitSale());
        }
        if (reqDto.getStock() > 0) {
            mtGoods.setStock(reqDto.getStock());
        }
        if (StringUtils.isNotEmpty(reqDto.getSalePoint())) {
            mtGoods.setSalePoint(reqDto.getSalePoint());
        }
        if (StringUtils.isNotEmpty(reqDto.getCanUsePoint())) {
            mtGoods.setCanUsePoint(reqDto.getCanUsePoint());
        }
        if (StringUtils.isNotEmpty(reqDto.getIsMemberDiscount())) {
            mtGoods.setIsMemberDiscount(reqDto.getIsMemberDiscount());
        }
        if (StringUtils.isNotEmpty(reqDto.getImages())) {
            mtGoods.setImages(reqDto.getImages());
        }

        mtGoods.setUpdateTime(new Date());
        if (reqDto.getId() <= 0) {
            mtGoods.setCreateTime(new Date());
        }

        return goodsRepository.save(mtGoods);
    }

    /**
     * 根据ID获取商品信息
     *
     * @param id 商品ID
     * @throws BusinessCheckException
     */
    @Override
    public MtGoods queryGoodsById(Integer id) {
        return goodsRepository.findOne(id);
    }

    /**
     * 根据ID获取商品详情
     *
     * @param id 商品ID
     * @throws BusinessCheckException
     */
    @Override
    public GoodsDto getGoodsDetail(Integer id) throws InvocationTargetException, IllegalAccessException {
        MtGoods mtGoods = goodsRepository.findOne(id);

        GoodsDto goodsInfo = new GoodsDto();

        if (mtGoods != null) {
            BeanUtils.copyProperties(goodsInfo, mtGoods);
        }
        String basePath = env.getProperty("images.upload.url");
        if (StringUtils.isNotEmpty(goodsInfo.getLogo())) {
            goodsInfo.setLogo(basePath + goodsInfo.getLogo());
        }

        // 规格列表
        Map<String, Object> param = new HashMap<>();
        param.put("EQ_goodsId", id.toString());
        Specification<MtGoodsSpec> specification1 = goodsSpecRepository.buildSpecification(param);
        Sort sort1 = new Sort(Sort.Direction.ASC, "id");
        List<MtGoodsSpec> goodsSpecList = goodsSpecRepository.findAll(specification1, sort1);
        goodsInfo.setSpecList(goodsSpecList);

        // sku列表
        Specification<MtGoodsSku> specification2 = goodsSkuRepository.buildSpecification(param);
        Sort sort2 = new Sort(Sort.Direction.ASC, "id");
        List<MtGoodsSku> goodsSkuList = goodsSkuRepository.findAll(specification2, sort2);
        goodsInfo.setSkuList(goodsSkuList);

        return goodsInfo;
    }

    /**
     * 根据ID删除商品信息
     *
     * @param id       ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除商品")
    public void deleteGoods(Integer id, String operator) throws BusinessCheckException {
        MtGoods cateInfo = this.queryGoodsById(id);
        if (null == cateInfo) {
            return;
        }

        cateInfo.setStatus(StatusEnum.DISABLE.getKey());
        cateInfo.setUpdateTime(new Date());

        goodsRepository.save(cateInfo);
    }

    @Override
    public List<MtGoods> queryGoodsListByParams(Map<String, Object> params) {
        Map<String, Object> param = new HashMap<>();

        String status =  params.get("EQ_status") == null ? StatusEnum.ENABLED.getKey(): params.get("EQ_status").toString();
        param.put("EQ_status", status);

        Specification<MtGoods> specification = goodsRepository.buildSpecification(param);
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        List<MtGoods> result = goodsRepository.findAll(specification, sort);

        return result;
    }

    @Override
    public List<GoodsSpecValueDto> getSpecListBySkuId(Integer skuId) {
        if (skuId < 0 || skuId == null) {
           return new ArrayList<>();
        }
        List<GoodsSpecValueDto> result = new ArrayList<>();

        MtGoodsSku goodsSku = goodsSkuRepository.findOne(skuId);

        String specIds = goodsSku.getSpecIds();
        String specIdArr[] = specIds.split("-");
        for (String specId : specIdArr) {
            MtGoodsSpec mtGoodsSpec = goodsSpecRepository.findOne(Integer.parseInt(specId));
            GoodsSpecValueDto dto = new GoodsSpecValueDto();
            dto.setSpecValueId(mtGoodsSpec.getId());
            dto.setSpecName(mtGoodsSpec.getName());
            dto.setSpecValue(mtGoodsSpec.getValue());
            result.add(dto);
        }

        return result;
    }
}
