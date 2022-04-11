package com.luixtech.uidgenerator.springboot.worker;

import com.luixtech.uidgenerator.core.exception.UidGenerateException;
import com.luixtech.uidgenerator.core.worker.WorkerNodeService;
import com.luixtech.uidgenerator.core.worker.model.WorkerNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MysqlWorkerNodeServiceImpl implements WorkerNodeService {

    private static final String       COL_ID          = "id";
    private static final String       TABLE_QUERY_SQL =
            "select count(*) "
                    + "from information_schema.tables "
                    + "where table_name = ?" +
                    "LIMIT 1";
    private static final String       QUERY_SQL       =
            "select * "
                    + "from %s "
                    + "where app_id = ? and host_name = ? " +
                    "LIMIT 1";
    private final        String       tableName;
    private final        JdbcTemplate jdbcTemplate;

    public MysqlWorkerNodeServiceImpl(String tableName, JdbcTemplate jdbcTemplate) {
        Validate.notNull(tableName, "tableName must not be null");
        Validate.notNull(jdbcTemplate, "jdbcTemplate must not be null");
        this.tableName = tableName;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createTableIfNotExist(boolean autoCreateTable) {
        try {
            Integer result = jdbcTemplate.queryForObject(TABLE_QUERY_SQL, Integer.class, tableName);
            if (result == null || result == 0) {
                String sql = StreamUtils.copyToString(new ClassPathResource("id_generator_worker_node.sql").getInputStream(),
                        Charset.defaultCharset());
                if (autoCreateTable) {
                    // If table does not exist, create it
                    jdbcTemplate.execute(String.format(sql, tableName));
                    log.info("Automatically created table {}", tableName);
                } else {
                    throw new UidGenerateException("Worker node table does not exist, please create it manually with sql: \n"
                            + String.format(sql, tableName));
                }
            }
        } catch (IOException e) {
            // Re-throw it as UidGenerateException
            throw new UidGenerateException("Failed to create worker node table", e);
        }
    }

    @Override
    public void insert(WorkerNode domain) {
        Map<String, Object> existingOne = jdbcTemplate
                .queryForMap(String.format(QUERY_SQL, tableName), domain.getAppId(), domain.getHostName());
        if (existingOne != null) {
            // Re-use the existing ID
            domain.setId((long) existingOne.get("id"));
            return;
        }

        SimpleJdbcInsert insertJdbc = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(tableName)
                .usingGeneratedKeyColumns(COL_ID);

        Map<String, Object> parameters = new HashMap<>(5);
        parameters.put("app_id", domain.getAppId());
        parameters.put("host_name", domain.getHostName());
        parameters.put("port", domain.getPort());
        parameters.put("type", domain.getType());
        parameters.put("uptime", domain.getUptime());
        parameters.put("created_time", domain.getCreatedTime());

        Number result = insertJdbc.executeAndReturnKey(parameters);
        domain.setId(result.longValue());
    }
}
