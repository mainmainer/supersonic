package com.tencent.supersonic.demo;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.tencent.supersonic.auth.api.authentication.pojo.User;
import com.tencent.supersonic.chat.server.agent.Agent;
import com.tencent.supersonic.chat.server.agent.AgentConfig;
import com.tencent.supersonic.chat.server.agent.AgentTool;
import com.tencent.supersonic.chat.server.agent.AgentToolType;
import com.tencent.supersonic.chat.server.service.AgentService;
import com.tencent.supersonic.common.config.EmbeddingConfig;
import com.tencent.supersonic.common.service.EmbeddingService;
import com.tencent.supersonic.common.util.JsonUtil;
import com.tencent.supersonic.common.util.MarkDownUtil;
import com.tencent.supersonic.common.util.PDFUtil;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.TextSegmentConvert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Order(5)
public class KnowledgeBaseDemo implements CommandLineRunner {
    @Autowired
    protected AgentService agentService;

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private EmbeddingConfig embeddingConfig;

    @Override
    public void run(String... args) throws Exception {
        doRun();
        addAgent();
    }

    public void doRun() {
        String path = this.getClass().getClassLoader().getResource("").getPath() + "doc";
        log.info("KnowledgeBaseDemo path:{}", path);
        File dir = new File(path);
        File[] files = dir.listFiles();
        List<String> sentenceList = new ArrayList<>();
        for (File file : files) {
            String filePath = file.getAbsolutePath();
            log.info("doc file path:{}", filePath);
            if (filePath.endsWith("md")) {
                List<String> pdfSentenceList = MarkDownUtil.getSentence(filePath);
                sentenceList.addAll(pdfSentenceList);
            }
            if (filePath.endsWith("pdf")) {
                List<String> markdownSentenceList = PDFUtil.getSentence(filePath);
                sentenceList.addAll(markdownSentenceList);
            }
        }
        for (String text : sentenceList) {
            log.info("sentenceList:{}", text);
            //System.out.println(text);
        }
        List<TextSegment> queries = sentenceList.stream().map(sentence -> {
            TextSegment query = TextSegment.from(sentence);
            TextSegmentConvert.addQueryId(query, String.valueOf(sentence.hashCode()));
            return query;
        }).collect(Collectors.toList());

        try {
            embeddingService.addCollection(embeddingConfig.getKnowledgeBaseCollectionName());
            embeddingService.addQuery(embeddingConfig.getKnowledgeBaseCollectionName(), queries);
        } catch (Exception e) {
            log.error("Failed to add bench mark demo data", e);
        }
        //if (s2EmbeddingStore instanceof InMemoryS2EmbeddingStore) {
        //    log.info("start persistentToFile");
        //    ((InMemoryS2EmbeddingStore) s2EmbeddingStore).persistentIndexToFile(
        //            embeddingConfig.getKnowledgeBaseCollectionName());
        //    log.info("end persistentToFile");
        //}
    }

    private void addAgent() {
        Agent agent = new Agent();
        agent.setId(1000);
        agent.setName("supersonic疑难解答");
        agent.setDescription("supersonic疑难解答");
        agent.setStatus(1);
        agent.setEnableSearch(0);
        agent.setExamples(Lists.newArrayList());
        AgentConfig agentConfig = new AgentConfig();

        AgentTool agentTool = new AgentTool();
        agentTool.setId("1");
        agentTool.setType(AgentToolType.KNOWLEDGE_BASE);
        agentConfig.getTools().add(agentTool);

        agent.setAgentConfig(JSONObject.toJSONString(agentConfig));
        log.info("agent:{}", JsonUtil.toString(agent));
        agentService.createAgent(agent, User.getFakeUser());
    }

}
