package com.tencent.supersonic.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PDFUtil {
    public static String readPDF(String path) {
        try {
            // 是否排序
            //boolean sort = false;
            int startPage = 1;

            int endPage = Integer.MAX_VALUE;
            String content = null;

            PDDocument document = PDDocument.load(new File(path));
            PDFTextStripper pts = new PDFTextStripper();
            endPage = document.getNumberOfPages();
            pts.setStartPage(startPage);
            pts.setEndPage(endPage);
            try {
                content = pts.getText(document);
            } catch (Exception e) {
                throw e;
            } finally {
                if (null != document) {
                    document.close();
                }
            }
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> splitDoc(String doc) {
        List<String> docList = Arrays.stream(doc.split("\n")).map(text -> text.trim()).filter(o -> {
            return StringUtils.isNotBlank(o);
        }).collect(Collectors.toList());
        return docList;
    }

    public static List<String> getSentence(String path) {
        String text = readPDF(path);
        if (Objects.nonNull(text)) {
            return splitDoc(text);
        }
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        String path = "/Users/gezuopeng/Downloads/Corrector实现解析.pdf";
        path="/Users/gezuopeng/IdeaProjects/supersonic/launchers/standalone/target/classes/doc/Corrector实现解析.pdf";
        //String doc = readPDF(path);
        List<String> docList = getSentence(path);
        //Arrays.stream(doc.split("\n")).map(text -> text.trim()).collect(Collectors.toList());

        for (String key : docList) {
            System.out.println(key);
        }
    }
}
