package com.luixtech.uidgenerator.core.worker.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class WorkerNode {

    public static final int WORKER_NODE_TYPE_CONTAINER        = 1;
    public static final int WORKER_NODE_TYPE_PHYSICAL_MACHINE = 2;

    private long          id;
    /**
     * Application ID
     */
    private String        appId;
    /**
     * Type of CONTAINER: HostName, ACTUAL : IP.
     */
    private String        hostName;
    /**
     * Type of CONTAINER: Port, ACTUAL : Timestamp + Random(0-10000)
     */
    private String        port;
    /**
     * Work node type
     */
    private int           type;
    /**
     * Uptime
     */
    private LocalDate     uptime;
    /**
     * Created time
     */
    private LocalDateTime createdTime;
}
