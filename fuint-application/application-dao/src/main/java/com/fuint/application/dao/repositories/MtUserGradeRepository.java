package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtUserGrade;

/**
 * mt_user_grade Repository
 * Created by FSQ
 * Contact wx fsq_better
 */
@Repository 
public interface MtUserGradeRepository extends BaseRepository<MtUserGrade, Integer> {
}

