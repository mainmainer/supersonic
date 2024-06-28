package com.tencent.supersonic.common.jsqlparser;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.GroupByElement;
import net.sf.jsqlparser.statement.select.GroupByVisitor;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class GroupByReplaceVisitor implements GroupByVisitor {

    ParseVisitorHelper parseVisitorHelper = new ParseVisitorHelper();
    private Map<String, String> fieldNameMap;
    private boolean exactReplace;

    public GroupByReplaceVisitor(Map<String, String> fieldNameMap, boolean exactReplace) {
        this.fieldNameMap = fieldNameMap;
        this.exactReplace = exactReplace;
    }

    public void visit(GroupByElement groupByElement) {
        groupByElement.getGroupByExpressionList();
        ExpressionList groupByExpressionList = groupByElement.getGroupByExpressionList();
        List<Expression> groupByExpressions = groupByExpressionList.getExpressions();

        for (int i = 0; i < groupByExpressions.size(); i++) {
            Expression expression = groupByExpressions.get(i);
            String columnName = expression.toString();
            if (expression instanceof Function && Objects.nonNull(
                    ((Function) expression).getParameters().getExpressions().get(0))) {
                columnName = ((Function) expression).getParameters().getExpressions().get(0).toString();
            }
            String replaceColumn = parseVisitorHelper.getReplaceValue(columnName, fieldNameMap,
                    exactReplace);
            if (StringUtils.isNotEmpty(replaceColumn)) {
                if (expression instanceof Column) {
                    groupByExpressions.set(i, new Column(replaceColumn));
                }
                if (expression instanceof Function) {
                    try {
                        log.info("replaceColumn:{}", replaceColumn);
                        log.info("expression:{}", expression);
                        Expression element = CCJSqlParserUtil.parseExpression(replaceColumn);
                        ExpressionList<Expression> expressionList = new ExpressionList<Expression>();
                        expressionList.add(element);
                        log.info("Parameters:{}", ((Function) expression).getParameters());
                        if (((Function) expression).getParameters().size() > 1) {
                            ((Function) expression).getParameters().stream().skip(1).forEach(e -> {
                                log.info("expressionList:{}", e);
                                expressionList.add((StringValue) e);
                            });
                        }
                        ((Function) expression).setParameters(expressionList);
                    } catch (JSQLParserException e) {
                        log.error("e", e);
                    }
                }
            }
        }
    }
}
