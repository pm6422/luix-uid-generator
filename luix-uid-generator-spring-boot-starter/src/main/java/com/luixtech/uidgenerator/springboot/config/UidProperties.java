package com.luixtech.uidgenerator.springboot.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

/**
 * Properties of UID generator.
 */
@ConfigurationProperties(prefix = "uid")
@Data
@Validated
public class UidProperties {
    private final Worker       worker       = new Worker();
    private final EpochSeconds epochSeconds = new EpochSeconds();
    private final Bits         bits         = new Bits();

    @Data
    public static class Worker {
        private boolean autoCreateWorkerNodeTable = true;
        @NotEmpty
        private String  workerNodeTableName       = "id_generator_worker_node";
        @NotEmpty
        private String  workerNodeServiceName     = "mysqlWorkerNodeService";
        @NotEmpty
        private String  workerIdAssignerName      = "defaultWorkerIdAssigner";
        @NotEmpty
        private String  appId;
        @NotEmpty
        private String  dataSourceName            = "dataSource";
    }

    @Data
    public static class EpochSeconds {
        /**
         * 起始时间，也就是应用第一次使用本uid generator的时间，格式为：yyyy-MM-dd，如：2022-02-01
         */
        private String startDate;
        @NotEmpty
        private String epochSecondsServiceName = "mysqlEpochSecondsService";
    }

    @Data
    public static class Bits {
        @Positive
        private int deltaSecondsBits = 29;
        @Positive
        private int workerBits       = 12;
        @Positive
        private int sequenceBits     = 22;
    }
}
