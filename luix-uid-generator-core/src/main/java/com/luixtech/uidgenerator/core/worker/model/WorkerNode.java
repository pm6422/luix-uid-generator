package com.luixtech.uidgenerator.core.worker.model;

import com.luixtech.uidgenerator.core.worker.WorkerNodeType;
import lombok.Data;

import java.util.Date;

@Data
public class WorkerNode {

    private long   id;
    /**
     * Application ID
     */
    private String appId;
    /**
     * Type of CONTAINER: HostName, ACTUAL : IP.
     */
    private String hostName;
    /**
     * Type of CONTAINER: Port, ACTUAL : Timestamp + Random(0-10000)
     */
    private String port;
    /**
     * type of {@link WorkerNodeType}
     */
    private int    type;
    /**
     * Uptime
     */
    private Date   uptime;
    /**
     * Created time
     */
    private Date   createdTime;
}
