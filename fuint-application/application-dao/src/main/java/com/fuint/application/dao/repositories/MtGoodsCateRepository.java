package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtGoodsCate;

   /**
    * mt_goods_cate Repository
    * Created by FSQ
    * Sat Oct 09 14:02:13 GMT+08:00 2021
    */ 
@Repository 
public interface MtGoodsCateRepository extends BaseRepository<MtGoodsCate, Integer> {
}

