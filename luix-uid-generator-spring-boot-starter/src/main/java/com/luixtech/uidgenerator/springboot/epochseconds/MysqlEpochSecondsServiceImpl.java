package com.luixtech.uidgenerator.springboot.epochseconds;

import com.luixtech.uidgenerator.core.epochseconds.EpochSecondsService;
import com.luixtech.uidgenerator.core.exception.UidGenerateException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public class MysqlEpochSecondsServiceImpl implements EpochSecondsService {

    private static final String FIRST_CREATED_TIME_SQL =
            "select created_time "
                    + "from {0} "
                    + "order by created_time asc " +
                    "LIMIT 1;";

    private final String       tableName;
    private final JdbcTemplate jdbcTemplate;
    private final String       specifiedEpochStr;

    public MysqlEpochSecondsServiceImpl(String tableName, JdbcTemplate jdbcTemplate, String specifiedEpochStr) {
        Validate.notNull(tableName, "tableName must not be null");
        Validate.notNull(jdbcTemplate, "jdbcTemplate must not be null");

        this.tableName = tableName;
        this.jdbcTemplate = jdbcTemplate;
        this.specifiedEpochStr = specifiedEpochStr;
    }

    @Override
    public long getEpochSeconds() {
        if (StringUtils.isNotEmpty(specifiedEpochStr)) {
            // Use default epoch
            return Instant.parse(specifiedEpochStr + "T00:00:00Z").getEpochSecond();
        }
        try {
            Date firstDate = jdbcTemplate.queryForObject(MessageFormat.format(FIRST_CREATED_TIME_SQL, tableName), Date.class);
            // Truncate to zero clock
            return DateUtils.truncate(firstDate, Calendar.DATE).getTime() / 1000;
        } catch (Exception e) {
            // Re-throw it as UidGenerateException
            throw new UidGenerateException("Failed to get first created time from table " + tableName, e);
        }
    }
}
