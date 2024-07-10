package com.tencent.supersonic.common.jsqlparser;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItemVisitorAdapter;

import java.util.Objects;

public class TableNameReplaceVisitor extends FromItemVisitorAdapter {

    private String oriTableName;
    private String tableName;

    public TableNameReplaceVisitor(String oriTableName, String tableName) {
        this.tableName = tableName;
        this.oriTableName = oriTableName;
    }

    @Override
    public void visit(Table table) {
        if (table.getName().equals(oriTableName)) {
            table.setName(tableName);
        } else if (Objects.isNull(oriTableName)) {
            table.setName(tableName);
        }
    }
}
