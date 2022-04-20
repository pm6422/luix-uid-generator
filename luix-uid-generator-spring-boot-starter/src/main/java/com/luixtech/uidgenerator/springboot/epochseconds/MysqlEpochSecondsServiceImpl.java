package com.luixtech.uidgenerator.springboot.epochseconds;

import com.luixtech.uidgenerator.core.epochseconds.EpochSecondsService;
import com.luixtech.uidgenerator.core.exception.UidGenerateException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class MysqlEpochSecondsServiceImpl implements EpochSecondsService {

    private final String     tableName;
    private final DSLContext dslContext;
    private final String     specifiedEpochStr;

    public MysqlEpochSecondsServiceImpl(String tableName, DSLContext dslContext, String specifiedEpochStr) {
        Validate.notNull(tableName, "tableName must not be null");
        Validate.notNull(dslContext, "dslContext must not be null");

        this.tableName = tableName;
        this.dslContext = dslContext;
        this.specifiedEpochStr = specifiedEpochStr;
    }

    @Override
    public long getEpochSeconds() {
        if (StringUtils.isNotEmpty(specifiedEpochStr)) {
            // Use default epoch
            return Instant.parse(specifiedEpochStr + "T00:00:00Z").getEpochSecond();
        }
        try {
            Date firstDate = Objects.requireNonNull(dslContext.select(DSL.field("created_time"))
                            .from(tableName)
                            .orderBy(DSL.field("created_time"))
                            .limit(1)
                            .fetchOne())
                    .into(Date.class);
            // Truncate to zero clock
            return DateUtils.truncate(firstDate, Calendar.DATE).getTime() / 1000;
        } catch (Exception e) {
            // Re-throw it as UidGenerateException
            throw new UidGenerateException("Failed to get created time column value of first record from table " + tableName, e);
        }
    }
}
