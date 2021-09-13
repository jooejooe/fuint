package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtMessage;
import java.util.List;

/**
 * mt_message Repository
 * Created by FSQ
 * Mon Sep 13 10:38:55 GMT+08:00 2021
 */
@Repository 
public interface MtMessageRepository extends BaseRepository<MtMessage, Integer> {
   /**
    * 获取最新一条消息
    *
    * @return
    * */
   @Query(value = "select t from MtMessage t where t.userId=:userId and t.type='pop' and t.isRead = 'N' order by t.id desc")
   List<MtMessage> findNewMessage(@Param("userId") Integer userId);
}

