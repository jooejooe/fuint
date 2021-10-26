package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtCart;

   /**
    * mt_cart Repository
    * Created by FSQ
    * Fri Oct 15 16:53:09 GMT+08:00 2021
    */ 
@Repository 
public interface MtCartRepository extends BaseRepository<MtCart, Integer> {
}

