package com.fuint.application.service.cart;

import com.fuint.application.dao.entities.MtCart;
import com.fuint.application.dao.repositories.MtCartRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.enums.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * 购物车业务实现类
 * Created by FSQ
 * Contact wx fsq_better
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private MtCartRepository cartRepository;

    /**
     * 保存购物车
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    @Override
    public boolean saveCart(MtCart reqDto, String action) throws BusinessCheckException {
        MtCart mtCart = new MtCart();

        if (reqDto.getGoodsId() > 0) {
            mtCart.setGoodsId(reqDto.getGoodsId());
        }
        if (reqDto.getUserId() > 0) {
            mtCart.setUserId(reqDto.getUserId());
        }
        if (reqDto.getNum() > 0) {
            mtCart.setNum(reqDto.getNum());
        }

        // 数量为0，删除购物车
        if (reqDto.getNum() == 0) {
           this.removeCart(reqDto.getGoodsId(), reqDto.getUserId());
        }

        mtCart.setStatus(StatusEnum.ENABLED.getKey());
        mtCart.setUpdateTime(new Date());

        Map<String, Object> params = new HashMap<>();
        params.put("EQ_userId", mtCart.getUserId().toString());
        params.put("EQ_goodsId", mtCart.getGoodsId().toString());

        List<MtCart> cartList = this.queryCartListByParams(params);

        if (action.equals("-") && cartList.size() == 0) {
            return true;
        }

        if (cartList.size() > 0) {
            mtCart = cartList.get(0);
            if (action.equals("+")) {
                mtCart.setNum(mtCart.getNum() + 1);
            } else {
                Integer num = mtCart.getNum() - 1;
                if (num <= 0) {
                    this.removeCart(reqDto.getGoodsId(), reqDto.getUserId());
                    return true;
                } else {
                    mtCart.setNum(mtCart.getNum() - 1);
                }
            }
        } else {
            mtCart.setCreateTime(new Date());
        }

        cartRepository.save(mtCart);

        return true;
    }

    /**
     * 删除购物车
     *
     * @param goodsId
     * @param userId
     * @throws BusinessCheckException
     */
    @Override
    public void removeCart(Integer goodsId, Integer userId) throws BusinessCheckException {
        Map<String, Object> params = new HashMap<>();
        params.put("EQ_userId",userId.toString());
        params.put("EQ_goodsId", goodsId.toString());

        List<MtCart> cartList = this.queryCartListByParams(params);
        if (cartList.size() > 0) {
            Integer id = cartList.get(0).getId();
            cartRepository.delete(id);
        }

        return;
    }

    @Override
    public List<MtCart> queryCartListByParams(Map<String, Object> params) {
        String status =  params.get("status") == null ? StatusEnum.ENABLED.getKey(): params.get("status").toString();
        String userId =  params.get("userId") == null ? "0" : params.get("userId").toString();
        params.put("EQ_status", status);

        Specification<MtCart> specification = cartRepository.buildSpecification(params);
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        List<MtCart> result = cartRepository.findAll(specification, sort);

        return result;
    }
}
