package com.luixtech.uidgenerator.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * DockerUtils
 */
@Slf4j
public abstract class DockerUtils {

    /**
     * Environment param keys
     */
    private static final String ENV_KEY_HOST          = "JPAAS_HOST";
    private static final String ENV_KEY_PORT          = "JPAAS_HTTP_PORT";
    private static final String ENV_KEY_PORT_ORIGINAL = "JPAAS_HOST_PORT_8080";

    /**
     * Docker host & port
     */
    private static String DOCKER_HOST = "";
    private static String DOCKER_PORT = "";

    /**
     * Whether is docker
     */
    private static boolean IS_DOCKER;

    static {
        retrieveFromEnv();
    }

    /**
     * Retrieve docker host
     *
     * @return empty string if not a docker
     */
    public static String getDockerHost() {
        return DOCKER_HOST;
    }

    /**
     * Retrieve docker port
     *
     * @return empty string if not a docker
     */
    public static String getDockerPort() {
        return DOCKER_PORT;
    }

    /**
     * Whether a docker
     *
     * @return
     */
    public static boolean isDocker() {
        return IS_DOCKER;
    }

    /**
     * Retrieve host & port from environment
     */
    private static void retrieveFromEnv() {
        // retrieve host & port from environment
        DOCKER_HOST = System.getenv(ENV_KEY_HOST);
        DOCKER_PORT = System.getenv(ENV_KEY_PORT);

        // not found from 'JPAAS_HTTP_PORT', then try to find from 'JPAAS_HOST_PORT_8080'
        if (StringUtils.isBlank(DOCKER_PORT)) {
            DOCKER_PORT = System.getenv(ENV_KEY_PORT_ORIGINAL);
        }

        boolean hasEnvHost = StringUtils.isNotBlank(DOCKER_HOST);
        boolean hasEnvPort = StringUtils.isNotBlank(DOCKER_PORT);

        // docker can find both host & port from environment
        if (hasEnvHost && hasEnvPort) {
            IS_DOCKER = true;

            // found nothing means not a docker, maybe a physical machine
        } else if (!hasEnvHost && !hasEnvPort) {
            IS_DOCKER = false;

        } else {
            log.error("Missing host or port from env for Docker. host:{}, port:{}", DOCKER_HOST, DOCKER_PORT);
            throw new RuntimeException(
                    "Missing host or port from env for Docker. host:" + DOCKER_HOST + ", port:" + DOCKER_PORT);
        }
    }

}
