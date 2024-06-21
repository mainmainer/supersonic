package com.tencent.supersonic.common.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.util.Arrays;
import java.util.List;
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
            System.out.println("Total Page: " + endPage);
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
    public static List<String> splitDoc(String doc){
        List<String> docList= Arrays.stream(doc.split("\n")).map(text->text.trim()).collect(Collectors.toList());
        return docList;
    }
    public static void main(String[] args){
        String path="/Users/gezuopeng/Downloads/Corrector实现解析.pdf";
        String doc = readPDF(path);
        List<String> docList= Arrays.stream(doc.split("\n")).map(text->text.trim()).collect(Collectors.toList());
        System.out.println(docList.size());
        for(String key:docList){
            System.out.println(key);
        }
    }
}
