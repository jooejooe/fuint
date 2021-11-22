package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtAddress;

   /**
    * mt_address Repository
    * Created by FSQ
    * Mon Nov 22 15:46:58 GMT+08:00 2021
    */ 
@Repository 
public interface MtAddressRepository extends BaseRepository<MtAddress, Integer> {
}

