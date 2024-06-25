package com.tencent.supersonic.chat.server.executor;

import com.tencent.supersonic.chat.server.pojo.ChatExecuteContext;
import com.tencent.supersonic.common.pojo.Constants;
import com.tencent.supersonic.common.util.JsonUtil;
import com.tencent.supersonic.headless.api.pojo.SemanticParseInfo;
import com.tencent.supersonic.headless.api.pojo.response.QueryResult;
import com.tencent.supersonic.headless.api.pojo.response.QueryState;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class KnowledgeBaseExecutor implements ChatExecutor {

    @Override
    public QueryResult execute(ChatExecuteContext chatExecuteContext) {
        SemanticParseInfo parseInfo = chatExecuteContext.getParseInfo();
        if (!parseInfo.getQueryMode().equals(Constants.KNOWLEDGE_QUERY_MODE)) {
            return null;
        }
        log.info("KnowledgeBaseExecutor execute!");
        QueryResult queryResult = new QueryResult();
        queryResult.setQueryMode(Constants.KNOWLEDGE_QUERY_MODE);
        Map<String, Object> properties = parseInfo.getProperties();
        Map<String, String> knowledgeMap = JsonUtil.toMap(JsonUtil.toString(properties.get(Constants.CONTEXT)),
                String.class, String.class);
        queryResult.setResponse(knowledgeMap.get(Constants.KNOWLEDGE_RESULT));
        queryResult.setQueryState(QueryState.SUCCESS);
        return queryResult;
    }

}
