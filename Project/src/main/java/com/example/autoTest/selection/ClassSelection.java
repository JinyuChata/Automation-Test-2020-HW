package com.example.autoTest.selection;

import com.example.autoTest.Utils;
import com.ibm.wala.ipa.callgraph.CGNode;

import java.io.*;
import java.util.HashSet;
import java.util.List;

public class ClassSelection extends AbstractSelection {
    @Override
    public String getType() {
        return "class";
    }

    @Override
    public String genDotString(CGNode[] relation) {
        return "\""
                + Utils.utilGetClassName(relation[0])
                + "\" -> \""
                + Utils.utilGetClassName(relation[1]) + "\";";
    }

    @Override
    public void expandDependency(CGNode[] relation, HashSet<String> changedClassSet) {
        if (changedClassSet.contains(Utils.utilGetClassName(relation[0]))
                && !Utils.utilGetClassName(relation[1]).contains("Test"))
            changedClassSet.add(Utils.utilGetClassName(relation[1]));
    }

    private static void classSelection(List<CGNode[]> relations, String project_name,
                                       String reportPath, String change_info)
            throws Exception {
        // 1. 图节点关系中的类关系写在dot文件
        HashSet<String> dotString = new HashSet<>();
        relations.forEach(relation -> {
            dotString.add("\""
                    + relation[0].getMethod().getDeclaringClass().getName().toString()
                    + "\" -> \""
                    + relation[1].getMethod().getDeclaringClass().getName().toString());
        });

        File reportDir = new File(reportPath);
        if (!reportDir.exists()) reportDir.mkdir();

        // 输出dot文件
        String type = "class";
        String dotPath = reportPath + type + "-" + project_name + ".dot";

        PrintStream ps = new PrintStream(new FileOutputStream(dotPath));
        ps.println("digraph " + project_name.toLowerCase() + "_" + type + " {");
        // 输出set中
        for (String relaStr : dotString) {
            ps.println("\t" + relaStr);
        }
        ps.println("}");

        // 2. 输出pdf (手工)
        // 3. changedList
        HashSet<String> changedClassSet = new HashSet<String>();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(change_info))
        );

        String nextLine;
        while ((nextLine = reader.readLine()) != null) {
            changedClassSet.add(nextLine.split(" ")[0]);
        }
        reader.close();

        // 4. 节点依赖于变化的类的init 则这个节点受到影响
        String selectionPath = "./selection-" + type + ".txt";
        File selectionFile = new File(selectionPath);
        PrintStream pss = new PrintStream(new FileOutputStream(selectionFile));

        HashSet<String> res = new HashSet<>();

        // 先探寻所有被影响的
        for (CGNode[] relation : relations) {
            // 如果被改变的里有relation的头 那么尾也算是被改变的
            // Test 在此时不算
            if (changedClassSet.contains(Utils.utilGetClassName(relation[0]))
                    && !Utils.utilGetClassName(relation[1]).contains("Test"))
                changedClassSet.add(Utils.utilGetClassName(relation[1]));
        }

        for (CGNode[] relation : relations) {
            // 被指向的后面是test
            // 但是不要求输出Test<init>
            if (changedClassSet.contains(Utils.utilGetClassName(relation[0]))
                    && Utils.utilGetClassName(relation[1]).contains("Test")
                    && !Utils.utilGetMethodSignature(relation[1]).contains("<init>"))
                res.add(Utils.utilGetClassName(relation[1]) + " " + Utils.utilGetMethodSignature(relation[1]));
        }
        System.out.println("=======================");
        res.forEach(System.out::println);
        res.forEach(pss::println);

    }

    @Override
    public void genSelectionResult(CGNode[] relation, HashSet<String> changedClassSet, HashSet<String> result) {
        if (changedClassSet.contains(Utils.utilGetClassName(relation[0]))
                && Utils.utilGetClassName(relation[1]).contains("Test")
                && !Utils.utilGetMethodSignature(relation[1]).contains("<init>"))
            result.add(Utils.utilGetClassName(relation[1]) + " " + Utils.utilGetMethodSignature(relation[1]));
    }

    @Override
    public String chooseSplitPattern(String line) {
        return line.split(" ")[0];
    }
}
