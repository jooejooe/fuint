package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtGoodsSku;

   /**
    * mt_goods_sku Repository
    * Created by FSQ
    * Tue Nov 09 15:03:05 GMT+08:00 2021
    */ 
@Repository 
public interface MtGoodsSkuRepository extends BaseRepository<MtGoodsSku, Integer> {
}

