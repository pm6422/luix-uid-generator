package com.luixtech.uidgenerator.springboot.worker;

import com.luixtech.uidgenerator.core.exception.UidGenerateException;
import com.luixtech.uidgenerator.core.worker.WorkerNodeService;
import com.luixtech.uidgenerator.core.worker.model.WorkerNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

@Slf4j
public class MysqlWorkerNodeServiceImpl implements WorkerNodeService {

    private static final String     COL_ID = "id";
    private final        String     tableName;
    private final        DSLContext dslContext;

    public MysqlWorkerNodeServiceImpl(String tableName, DSLContext dslContext) {
        Validate.notNull(tableName, "tableName must not be null");
        Validate.notNull(dslContext, "dslContext must not be null");

        this.tableName = tableName;
        this.dslContext = dslContext;
    }

    @Override
    public void createTableIfNotExist(boolean autoCreateTable) {
        try {
            Record workerNodeTableRecord = dslContext.selectFrom("information_schema.tables")
                    .where("table_name = ?", tableName)
                    .fetchOne();
            if (workerNodeTableRecord == null) {
                String sql = StreamUtils.copyToString(new ClassPathResource("id_generator_worker_node.sql").getInputStream(),
                        Charset.defaultCharset());
                if (autoCreateTable) {
                    // If table does not exist, create it
                    dslContext.execute(String.format(sql, tableName));
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
        Record existingOne = dslContext.selectFrom(tableName)
                .where("app_id = ?", domain.getId())
                .and("host_name = ?", domain.getHostName())
                .limit(1)
                .fetchOne();
        if (existingOne != null) {
            // Re-use the existing ID
            domain.setId((long) Objects.requireNonNull(existingOne.get(COL_ID)));
            return;
        }
        Long id = insertAndReturnId(domain);
        domain.setId(id);
    }

    public Long insertAndReturnId(WorkerNode domain) {
        return dslContext.transactionResult(configuration -> {
            DSLContext dslContext1 = DSL.using(configuration);
            return Objects.requireNonNull(dslContext1.insertInto(DSL.table(tableName))
                            .set(DSL.field("app_id"), domain.getAppId())
                            .set(DSL.field("host_name"), domain.getHostName())
                            .set(DSL.field("port"), domain.getPort())
                            .set(DSL.field("type"), domain.getType())
                            .set(DSL.field("uptime"), domain.getUptime())
                            .set(DSL.field("created_time"), domain.getCreatedTime())
                            .returningResult(DSL.field(COL_ID))
                            .fetchOne())
                    .into(Long.class);
        });
    }
}
