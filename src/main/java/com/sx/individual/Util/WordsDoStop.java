package com.sx.individual.Util;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ShellComponent
public class WordsDoStop {
    /**
     * 输出文件单词数
     */
    @ShellMethod(key = "wf.exe", value = "统计单词个数", prefix = "-")
    public static void outWords(String x, String f, @ShellOption(defaultValue = "-1")int n) throws IOException {

        double startTime = System.currentTimeMillis();

        Map<String, Integer> map = new HashMap<>();
        List<String> stringList = IODemoByNIO.readFileByChannel(f);

        //读取stopwords文件
        List<String> stopList = IODemoByNIO.readFileByChannel(x);
        String st = "";
        for(int i = 0; i<stopList.size(); i++) {
            st += stopList.get(i);
        }
        st = st.toLowerCase();

        //读取处理目标文件
        //String s = "";
        for(int i = 0; i<stringList.size(); i++){
            String s = stringList.get(i);
            s = s.toLowerCase();
            String regex = "\\W+";
            Pattern pat = Pattern.compile(regex);
            Matcher matcher = pat.matcher(s);
            s = matcher.replaceAll(" ");
            //s = s.replace(".", " ");
            String[] ss = s.split("\\s+");
            for(int j = 0; j<ss.length; j++){
                if(ss[j].matches("[a-z]+[0-9]*") && st.indexOf(ss[j]) == -1){
                    int value = map.getOrDefault(ss[j], 0);
                    map.put(ss[j], value+1);
                }
            }
        }

        List<Map.Entry<String,Integer>> list = new ArrayList<Map.Entry<String,Integer>>(map.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String,Integer>>() {
            //排序
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                if(o1.getValue().compareTo(o2.getValue()) == 0){
                    return o1.getKey().compareTo(o2.getKey());
                }
                return -(o1.getValue().compareTo(o2.getValue()));
            }

        });
        int i = 0;
        IODemoByNIO.out = "";
        if(n == -1)
            n = Integer.MAX_VALUE;
        System.out.println("单词"+ "         单词数");
        IODemoByNIO.out += "单词"+ "         单词数" + "\n";
        for(Map.Entry<String,Integer> mapping:list){
            if(i < n) {
                System.out.printf("%-13s", mapping.getKey());
                String tt = String.format("%-13s", mapping.getKey());
                IODemoByNIO.out += tt;
                System.out.println(mapping.getValue());
                IODemoByNIO.out += mapping.getValue() + "\n";
            }
            else
                break;
            i++;
        }

        System.out.println("耗时： " + (System.currentTimeMillis() - startTime) + "ms");
        IODemoByNIO.out += "耗时： " + ((System.currentTimeMillis() - startTime)/1000) + "s\n" ;
        IODemoByNIO.writeFileByChannel("停词表单词统计.txt");
    }

    //@ShellMethod(key = "wf.exe -d -s", value = "统计该目录下所有目录所有txt单词个数")
    public static void directoryTxt(String stopwords, String path, @ShellOption(defaultValue = "-1")int n) throws IOException {
        File file = new File(path);

        File[] fileArr = file.listFiles();
        for(File f:fileArr){
            String s = f.getName();
            if(f.isFile() && s.substring(s.length()-3, s.length()).equals("txt")){
                System.out.println(f.getName());
                outWords(stopwords, f.getPath(), n);
                System.out.println();
            }
        }
    }

    //@ShellMethod(key = "wf.exe -x", value = "统计该目录下所有txt单词个数")
    public static void directory(String stopwords, String path, @ShellOption(defaultValue = "-1")int n) throws IOException {
        File file = new File(path);

        File[] fileArr = file.listFiles();

        for(File f :fileArr){
            String s = f.getName();
            if(f.isFile() && s.substring(s.length()-3, s.length()).equals("txt")){
                System.out.println(f.getName());
                outWords(stopwords, f.getPath(), n);
                System.out.println();
            }
            else if(f.isDirectory()){
                directory(stopwords, f.getPath(), n);
            }
        }
    }

}
