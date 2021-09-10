package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtSetting;
import java.util.List;

/**
 * mt_setting Repository
 * Created by FSQ
 * Contact wx fsq_better
 */
@Repository 
public interface MtSettingRepository extends BaseRepository<MtSetting, Integer> {
   @Query("select t from MtSetting t where t.type = :type")
   List<MtSetting> querySettingByType(@Param("type") String type);

   @Query("select t from MtSetting t where t.name = :name and t.status = 'A'")
   MtSetting querySettingByName(@Param("name") String name);
}

