package com.luixtech.uidgenerator.springboot.config;

import com.luixtech.uidgenerator.core.epochseconds.EpochSecondsService;
import com.luixtech.uidgenerator.core.uid.UidGenerator;
import com.luixtech.uidgenerator.core.uid.impl.CachedUidGenerator;
import com.luixtech.uidgenerator.core.worker.DefaultWorkerIdAssigner;
import com.luixtech.uidgenerator.core.worker.WorkerIdAssigner;
import com.luixtech.uidgenerator.core.worker.WorkerNodeService;
import com.luixtech.uidgenerator.springboot.epochseconds.MysqlEpochSecondsServiceImpl;
import com.luixtech.uidgenerator.springboot.worker.MysqlWorkerNodeServiceImpl;
import org.apache.commons.lang3.Validate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;

@EnableConfigurationProperties({UidProperties.class})
public class UidAutoConfiguration {

    @Resource
    private UidProperties      uidProperties;
    @Resource
    private ApplicationContext applicationContext;

    @Bean
    public EpochSecondsService mysqlEpochSecondsService() {
        JdbcTemplate jdbcTemplate = applicationContext
                .getBean(uidProperties.getWorker().getJdbcTemplateName(), JdbcTemplate.class);
        Validate.notNull(jdbcTemplate, "jdbcTemplate must not be null, please check your configuration");
        return new MysqlEpochSecondsServiceImpl(uidProperties.getWorker().getWorkerNodeTableName(),
                jdbcTemplate, uidProperties.getEpochSeconds().getStartDate());
    }

    @Bean
    public WorkerNodeService mysqlWorkerNodeService() {
        JdbcTemplate jdbcTemplate = applicationContext
                .getBean(uidProperties.getWorker().getJdbcTemplateName(), JdbcTemplate.class);
        Validate.notNull(jdbcTemplate, "jdbcTemplate must not be null, please check your configuration");
        return new MysqlWorkerNodeServiceImpl(uidProperties.getWorker().getWorkerNodeTableName(), jdbcTemplate);
    }

    @Bean
    public WorkerIdAssigner defaultWorkerIdAssigner() {
        WorkerNodeService workerNodeService = applicationContext
                .getBean(uidProperties.getWorker().getWorkerNodeServiceName(), WorkerNodeService.class);
        return new DefaultWorkerIdAssigner(uidProperties.getWorker().getAppId(),
                uidProperties.getWorker().isAutoCreateWorkerNodeTable(),
                uidProperties.getBits().getWorkerBits(),
                workerNodeService);
    }

    @Bean
    public UidGenerator uidGenerator() {
        CachedUidGenerator uidGenerator = new CachedUidGenerator();
        uidGenerator.setWorkerIdAssigner(getWorkerIdAssigner());
        uidGenerator.setEpochSecondsService(getEpochSecondsService());
        uidGenerator.setDeltaSecondsBits(uidProperties.getBits().getDeltaSecondsBits());
        uidGenerator.setWorkerBits(uidProperties.getBits().getWorkerBits());
        uidGenerator.setSequenceBits(uidProperties.getBits().getSequenceBits());
        uidGenerator.initialize();
        return uidGenerator;
    }

    private WorkerIdAssigner getWorkerIdAssigner() {
        return applicationContext
                .getBean(uidProperties.getWorker().getWorkerIdAssignerName(), WorkerIdAssigner.class);
    }

    private EpochSecondsService getEpochSecondsService() {
        return applicationContext
                .getBean(uidProperties.getEpochSeconds().getEpochSecondsServiceName(), EpochSecondsService.class);
    }
}
