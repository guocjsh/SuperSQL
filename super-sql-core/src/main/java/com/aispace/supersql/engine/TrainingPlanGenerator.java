package com.aispace.supersql.engine;

import java.util.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

public class TrainingPlanGenerator {

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    public static class TrainingPlan {
        private List<TrainingPlanItem> plan;

        public TrainingPlan() {
            this.plan = new ArrayList<>();
        }

        public void addPlanItem(TrainingPlanItem item) {
            this.plan.add(item);
        }

        // 其他方法...
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    public static class TrainingPlanItem {
        public static final String ITEM_TYPE_IS = "IS";
        private String itemType;
        private String itemGroup;
        private String itemName;
        private String itemValue;

        public TrainingPlanItem(String itemType, String itemGroup, String itemName, String itemValue) {
            this.itemType = itemType;
            this.itemGroup = itemGroup;
            this.itemName = itemName;
            this.itemValue = itemValue;
        }

        // 其他方法...
    }

    public static TrainingPlan getTrainingPlanGeneric(List<Map<String, Object>> data) {
        // 确保数据不为空
        if (data.isEmpty()) {
            throw new IllegalArgumentException("数据列表不能为空");
        }

        // 在所有行中查找匹配的列
        String databaseColumn = findFirstMatchingColumn(data, Arrays.asList("database", "table_catalog"));
        String schemaColumn = findFirstMatchingColumn(data, Collections.singletonList("table_schema"));
        String tableColumn = findFirstMatchingColumn(data, Collections.singletonList("table_name"));

        List<String> columns = new ArrayList<>(Arrays.asList(databaseColumn, schemaColumn, tableColumn));
        List<String> candidates = Arrays.asList("column_name", "data_type", "comment");
        columns.addAll(findMatchingColumns(data, candidates));

        TrainingPlan plan = new TrainingPlan();

        Set<String> databases = getUniqueValues(data, databaseColumn);
        for (String database : databases) {
            Set<String> schemas = getUniqueValues(data, schemaColumn, databaseColumn, database);
            for (String schema : schemas) {
                Set<String> tables = getUniqueValues(data, tableColumn, databaseColumn, database, schemaColumn, schema);
                for (String table : tables) {
                    List<Map<String, Object>> filteredData = filterData(data, databaseColumn, database, schemaColumn, schema, tableColumn, table);
                    String doc = generateDocumentation(filteredData, table, database, columns);
                    plan.addPlanItem(new TrainingPlanItem(TrainingPlanItem.ITEM_TYPE_IS, database + "." + schema, table, doc));
                }
            }
        }

        return plan;
    }

    private static String findFirstMatchingColumn(List<Map<String, Object>> data, List<String> patterns) {
        for (Map<String, Object> row : data) {
            for (String column : row.keySet()) {
                for (String pattern : patterns) {
                    if (column.toLowerCase().contains(pattern)) {
                        return column;
                    }
                }
            }
        }
        throw new IllegalArgumentException("未找到匹配的列");
    }

    private static List<String> findMatchingColumns(List<Map<String, Object>> data, List<String> patterns) {
        Set<String> matchingColumns = new HashSet<>();
        for (Map<String, Object> row : data) {
            for (String column : row.keySet()) {
                for (String pattern : patterns) {
                    if (column.toLowerCase().contains(pattern)) {
                        matchingColumns.add(column);
                    }
                }
            }
        }
        return new ArrayList<>(matchingColumns);
    }

    private static Set<String> getUniqueValues(List<Map<String, Object>> data, String targetColumn, String... conditions) {
        Set<String> uniqueValues = new HashSet<>();
        for (Map<String, Object> row : data) {
            boolean match = true;
            for (int i = 0; i < conditions.length; i += 2) {
                String conditionColumn = conditions[i];
                String conditionValue = conditions[i + 1];
                if (!row.get(conditionColumn).toString().equals(conditionValue)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                uniqueValues.add(row.get(targetColumn).toString());
            }
        }
        return uniqueValues;
    }

    private static List<Map<String, Object>> filterData(List<Map<String, Object>> data, String... conditions) {
        List<Map<String, Object>> filteredData = new ArrayList<>();
        for (Map<String, Object> row : data) {
            boolean match = true;
            for (int i = 0; i < conditions.length; i += 2) {
                String conditionColumn = conditions[i];
                String conditionValue = conditions[i + 1];
                if (!row.get(conditionColumn).toString().equals(conditionValue)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                filteredData.add(row);
            }
        }
        return filteredData;
    }

    private static String generateDocumentation(List<Map<String, Object>> data, String table, String database, List<String> columns) {
        StringBuilder doc = new StringBuilder("The following columns are in the " + table + " table in the " + database + " database:\n\n");
        for (Map<String, Object> row : data) {
            for (String column : columns) {
                doc.append(column).append(": ").append(row.get(column)).append("\n");
            }
            doc.append("\n");
        }
        return doc.toString();
    }
}
