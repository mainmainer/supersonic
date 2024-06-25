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
import com.tencent.supersonic.common.util.JsonUtil;
import com.tencent.supersonic.common.util.MarkDownUtil;
import com.tencent.supersonic.common.util.PDFUtil;
import dev.langchain4j.store.embedding.ComponentFactory;
import dev.langchain4j.store.embedding.EmbeddingQuery;
import dev.langchain4j.store.embedding.InMemoryS2EmbeddingStore;
import dev.langchain4j.store.embedding.S2EmbeddingStore;
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

    private S2EmbeddingStore s2EmbeddingStore = ComponentFactory.getS2EmbeddingStore();

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
            //log.info("sentenceList:{}", text);
            System.out.println(text);
        }
        List<EmbeddingQuery> queries = sentenceList.stream().map(sentence -> {
            EmbeddingQuery embeddingQuery = new EmbeddingQuery();
            embeddingQuery.setQueryId(String.valueOf(sentence.hashCode()));
            embeddingQuery.setQuery(sentence);
            embeddingQuery.setQueryEmbedding(null);
            return embeddingQuery;
        }).collect(Collectors.toList());

        try {
            s2EmbeddingStore.addCollection(embeddingConfig.getKnowledgeBaseCollectionName());
            s2EmbeddingStore.addQuery(embeddingConfig.getKnowledgeBaseCollectionName(), queries);
        } catch (Exception e) {
            log.error("Failed to add bench mark demo data", e);
        }
        if (s2EmbeddingStore instanceof InMemoryS2EmbeddingStore) {
            log.info("start persistentToFile");
            ((InMemoryS2EmbeddingStore) s2EmbeddingStore).persistentIndexToFile(
                    embeddingConfig.getKnowledgeBaseCollectionName());
            log.info("end persistentToFile");
        }
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
