package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtRegion;

   /**
    * mt_region Repository
    * Created by FSQ
    * Mon Nov 22 16:39:22 GMT+08:00 2021
    */ 
@Repository 
public interface MtRegionRepository extends BaseRepository<MtRegion, Integer> {
}

