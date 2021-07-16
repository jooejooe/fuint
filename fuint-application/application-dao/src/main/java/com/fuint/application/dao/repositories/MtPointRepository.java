package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtPoint;

/**
 * mt_point Repository
 * Created by FSQ
 * Contact wx fsq_better
 */
@Repository 
public interface MtPointRepository extends BaseRepository<MtPoint, Integer> {
}

