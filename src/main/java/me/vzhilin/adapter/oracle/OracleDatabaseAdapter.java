package me.vzhilin.adapter.oracle;

import me.vzhilin.adapter.DatabaseAdapter;
import me.vzhilin.adapter.ValueConverter;
import me.vzhilin.catalog.Table;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.util.HashMap;
import java.util.Map;

public class OracleDatabaseAdapter implements DatabaseAdapter {
    private final Map<JDBCType, ValueConverter> matchers = new HashMap<>();

    public OracleDatabaseAdapter() {
        ValueConverter doubleMatcher = new BigDecimalConverter();
        matchers.put(JDBCType.NUMERIC, doubleMatcher);
        matchers.put(JDBCType.FLOAT, doubleMatcher);
        matchers.put(JDBCType.DOUBLE, doubleMatcher);

        ValueConverter intMatcher = new IntConverter();
        matchers.put(JDBCType.INTEGER, intMatcher);
        matchers.put(JDBCType.SMALLINT, intMatcher);
        matchers.put(JDBCType.TINYINT, intMatcher);

        ValueConverter textMatcher = new TextConverter();
        matchers.put(JDBCType.VARCHAR, textMatcher);
        matchers.put(JDBCType.DATE, new NeverConverter()); // TODO DATES
        matchers.put(JDBCType.BLOB, new NeverConverter());
        matchers.put(JDBCType.CHAR, new NeverConverter()); // TODO
    }

    @Override
    public JDBCType getType(String typeName) {
        return JDBCType.valueOf(typeName);
    }

    @Override
    public ValueConverter getConverter(JDBCType type) {
        ValueConverter valueConverter = matchers.get(type);
        if (valueConverter == null) {
            throw new NullPointerException("no matchers for type: " + type);
        }
        return valueConverter;
    }

    @Override
    public String qualifiedTableName(Table table) {
        String schemaName = table.getSchemaName();
        if (schemaName != null) {
            return String.format("\"%s\".\"%s\"", schemaName, table.getName());
        } else {
            return String.format("\"%s\"", table.getName());
        }
    }

    private static final class BigDecimalConverter implements ValueConverter {
        @Override
        public Object fromString(String text) {
            try {
                return new BigDecimal(text);
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    private static final class IntConverter implements ValueConverter {
        @Override
        public Object fromString(String text) {
            try {
                return Long.parseLong(text);
            } catch (NumberFormatException ex) {
                return false;
            }
        }
    }

    private static final class TextConverter implements ValueConverter {
        @Override
        public Object fromString(String text) {
            return text;
        }
    }

    private static final class NeverConverter implements ValueConverter {
        @Override
        public Object fromString(String text) {
            return null;
        }
    }
}
