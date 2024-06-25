package com.tencent.supersonic.common.util;

import org.apache.commons.lang3.StringUtils;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profile.pegdown.Extensions;
import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.TextCollectingVisitor;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;

public class MarkDownUtil {
    private static final DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(
            Extensions.ALL
    );

    private static final MutableDataSet FORMAT_OPTIONS = new MutableDataSet();

    static {
        // copy extensions from Pegdown compatible to Formatting
        FORMAT_OPTIONS.set(Parser.EXTENSIONS, Parser.EXTENSIONS.get(OPTIONS));
    }

    static final Parser PARSER = Parser.builder(OPTIONS).build();

    public static String readMarkDown(String fileName) {
        StringBuffer sb = new StringBuffer();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String getText(String pegdown) {

        Node document = PARSER.parse(pegdown);
        //document.
        TextCollectingVisitor textCollectingVisitor = new TextCollectingVisitor();
        String text = textCollectingVisitor.collectAndGetText(document);
        return text;
    }

    public static List<String> splitDoc(String doc) {
        List<String> docList = Arrays.stream(doc.split("\n")).filter(text -> StringUtils.isNotBlank(text)
                && text.length() > 10).map(text -> text.trim()).collect(Collectors.toList());
        return docList;
    }

    public static List<String> getSentence(String path) {
        String text = readMarkDown(path);
        if (Objects.nonNull(text)) {
            return splitDoc(text);
        }
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        String path = "/Users/gezuopeng/IdeaProjects/supersonic-website/content/docs/Chat BI/配置插件.md";

        List<String> docList = getSentence(path);
        // System.out.println(text);
        for (String key : docList) {
            System.out.println(key);
        }
    }
}
