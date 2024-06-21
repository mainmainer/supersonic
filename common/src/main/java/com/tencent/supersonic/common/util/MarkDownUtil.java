//package com.tencent.supersonic.common.util;
//
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.text.PDFTextStripper;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//import com.vladsch.flexmark.parser.Parser;
//import com.vladsch.flexmark.profile.pegdown.Extensions;
//import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter;
//import com.vladsch.flexmark.util.ast.Node;
//import com.vladsch.flexmark.util.ast.TextCollectingVisitor;
//import com.vladsch.flexmark.util.data.DataHolder;
//import com.vladsch.flexmark.util.data.MutableDataSet;
//
//public class MarkDownUtil {
//    final private static DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(
//            Extensions.ALL
//    );
//
//    static final MutableDataSet FORMAT_OPTIONS = new MutableDataSet();
//    static {
//        // copy extensions from Pegdown compatible to Formatting
//        FORMAT_OPTIONS.set(Parser.EXTENSIONS, Parser.EXTENSIONS.get(OPTIONS));
//    }
//
//    static final Parser PARSER = Parser.builder(OPTIONS).build();
//    public static String readMarkDown(String fileName) {
//        //String fileName = "D:\\data\\test\\newFile3.txt";
//        StringBuffer sb=new StringBuffer();
//        // 带缓冲的流读取，默认缓冲区8k
//        try (BufferedReader br = new BufferedReader(new FileReader(fileName))){
//            String line;
//            while ((line = br.readLine()) != null) {
//                sb.append(line+"\n");
//                //System.out.println(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return sb.toString();
//    }
//    public static String getText(String pegdown){
//
//        Node document = PARSER.parse(pegdown);
//        document.
//        TextCollectingVisitor textCollectingVisitor = new TextCollectingVisitor();
//        String text = textCollectingVisitor.collectAndGetText(document);
//        return text;
//    }
//    public static List<String> splitDoc(String doc){
//        List<String> docList= Arrays.stream(doc.split("\n")).map(text->text.trim()).collect(Collectors.toList());
//        return docList;
//    }
//    public static void main(String[] args){
//        String path="/Users/gezuopeng/IdeaProjects/supersonic-website/content/docs/Chat BI/配置插件.md";
//        String doc = readMarkDown(path);
//        String text = getText(doc);
//        System.out.println(text);
////        List<String> docList= Arrays.stream(doc.split("\n")).map(text->text.trim()).collect(Collectors.toList());
////        System.out.println(docList.size());
////        for(String key:docList){
////            System.out.println(key);
////        }
//    }
//}
