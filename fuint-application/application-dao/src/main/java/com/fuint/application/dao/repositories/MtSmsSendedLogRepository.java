package com.fuint.application.dao.repositories;

import com.fuint.base.dao.BaseRepository;
import org.springframework.stereotype.Repository;
import com.fuint.application.dao.entities.MtSmsSendedLog;

/**
 * mt_sms_sended_log Repository
 * Created by FSQ
 * Contact wx fsq_better
 */
@Repository 
public interface MtSmsSendedLogRepository extends BaseRepository<MtSmsSendedLog, Integer> {
}

