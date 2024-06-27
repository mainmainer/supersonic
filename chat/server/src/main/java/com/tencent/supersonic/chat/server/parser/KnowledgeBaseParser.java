package com.tencent.supersonic.chat.server.parser;

import com.google.common.collect.Lists;
import com.tencent.supersonic.chat.server.pojo.ChatParseContext;
import com.tencent.supersonic.common.config.EmbeddingConfig;
import com.tencent.supersonic.common.pojo.Constants;
import com.tencent.supersonic.common.service.EmbeddingService;
import com.tencent.supersonic.common.util.ContextUtils;
import com.tencent.supersonic.common.util.S2ChatModelProvider;
import com.tencent.supersonic.headless.api.pojo.SchemaElement;
import com.tencent.supersonic.headless.api.pojo.SchemaElementMatch;
import com.tencent.supersonic.headless.api.pojo.SemanticParseInfo;
import com.tencent.supersonic.headless.api.pojo.response.ParseResp;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.RetrieveQuery;
import dev.langchain4j.store.embedding.RetrieveQueryResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
public class KnowledgeBaseParser implements ChatParser {

    private static final String instruction = ""
            + "#角色: 你是一个知识问答助手！\n"
            + "#任务: 请根据当前问题、相关背景基础知识，理解相关背景基础知识的含义，回答出结果，请尽可能用中文！\n"
            + "#当前问题: %s\n"
            + "#相关背景基础知识: %s\n";
    private static final int retrieveNum = 30;

    @Override
    public void parse(ChatParseContext chatParseContext, ParseResp parseResp) {
        EmbeddingService embeddingService = ContextUtils.getBean(EmbeddingService.class);
        Long startTime = System.currentTimeMillis();
        if (!chatParseContext.enableKnowledge()) {
            return;
        }
        log.info("knowledgeBaseParser parse!");
        EmbeddingConfig embeddingConfig = ContextUtils.getBean(EmbeddingConfig.class);
        List<String> queryTextsList = new ArrayList<>();
        queryTextsList.add(parseResp.getQueryText());
        RetrieveQuery retrieveQuery = RetrieveQuery.builder().queryTextsList(queryTextsList).build();

        List<RetrieveQueryResult> retrieveQueryResultList = embeddingService.retrieveQuery(
                embeddingConfig.getKnowledgeBaseCollectionName(), retrieveQuery, retrieveNum);
        if (CollectionUtils.isEmpty(retrieveQueryResultList)) {
            return;
        }
        List<String> retrieveQueryList = retrieveQueryResultList.get(0).getRetrieval().stream()
                .map(o -> o.getQuery()).collect(Collectors.toList());

        String promptStr = String.format(instruction, parseResp.getQueryText(), String.join("\n", retrieveQueryList));
        Prompt prompt = PromptTemplate.from(promptStr).apply(Collections.EMPTY_MAP);

        ChatLanguageModel chatLanguageModel = S2ChatModelProvider.provide(chatParseContext.getAgent().getLlmConfig());
        Response<AiMessage> response = chatLanguageModel.generate(prompt.toSystemMessage());

        String result = response.content().text();
        log.info("KnowledgeBaseParser parse:{}", result);
        SemanticParseInfo semanticParseInfo = buildSemanticParseInfo(result, chatParseContext);
        semanticParseInfo.setQueryMode(Constants.KNOWLEDGE_QUERY_MODE);
        semanticParseInfo.setScore(1.0);
        parseResp.getSelectedParses().add(semanticParseInfo);
        parseResp.getParseTimeCost().setSqlTime(System.currentTimeMillis() - startTime);
    }

    protected SemanticParseInfo buildSemanticParseInfo(String result, ChatParseContext chatParseContext) {
        List<SchemaElementMatch> schemaElementMatches = chatParseContext.getMapInfo().getMatchedElements(null);
        if (schemaElementMatches == null) {
            schemaElementMatches = Lists.newArrayList();
        }
        SemanticParseInfo semanticParseInfo = new SemanticParseInfo();
        semanticParseInfo.setElementMatches(schemaElementMatches);
        SchemaElement schemaElement = new SchemaElement();
        schemaElement.setDataSet(null);
        semanticParseInfo.setDataSet(schemaElement);
        Map<String, Object> properties = new HashMap<>();
        Map<String, String> knowledgeMap = new HashMap<>();
        knowledgeMap.put(Constants.KNOWLEDGE_RESULT, result);
        properties.put(Constants.CONTEXT, knowledgeMap);
        properties.put("type", "knowledgeBase");
        semanticParseInfo.setProperties(properties);
        semanticParseInfo.setScore(1.0);
        //semanticParseInfo.setTextInfo(String.format("将由插件工具**%s**来解答", plugin.getName()));
        return semanticParseInfo;
    }

}
