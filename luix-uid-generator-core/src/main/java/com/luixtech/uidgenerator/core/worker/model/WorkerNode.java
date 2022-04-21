package com.luixtech.uidgenerator.core.worker.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class WorkerNode {

    public static final int WORKER_NODE_TYPE_PHYSICAL_MACHINE = 1;
    public static final int WORKER_NODE_TYPE_CONTAINER        = 2;

    private long          id;
    /**
     * Application ID
     */
    private String        appId;
    /**
     * HostName for CONTAINER: or IP for PHYSICAL_MACHINE
     */
    private String        hostName;
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
