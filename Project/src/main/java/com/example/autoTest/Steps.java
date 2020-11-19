package com.example.autoTest;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.config.AnalysisScopeReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Steps {
    public static AnalysisScope buildScope(String project_target, String change_info)
            throws Exception {
        File targetFileFolder = new File(project_target);

        // 获取classFiles
        List<File> classFiles = Utils.getDir(targetFileFolder);
        if (classFiles.size() == 0) throw new Exception("No class file");

        // 获取changeInfo
        BufferedReader changeInfoBufferedReader =
                new BufferedReader(new InputStreamReader(new FileInputStream(new File(change_info))));

        // 构建scope
        AnalysisScope scope = AnalysisScopeReader.readJavaScope("scope.txt",
                new File("exclusion.txt"), Main.class.getClassLoader());

        // add Classes to scope
        for (File classFile : classFiles) {
            scope.addClassFileToScope(ClassLoaderReference.Application, classFile);
        }

        return scope;
    }

    public static List<CGNode[]> scan(AnalysisScope scope) throws Exception {
        // 1.生成类层次关系对象
        ClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(scope);

        // 2.生成进入点
        Iterable<Entrypoint> eps = new AllApplicationEntrypoints(scope, cha);
        // 3.利用CHA算法构建调用图
        CHACallGraph cg = new CHACallGraph(cha);
        cg.init(eps);

        // 4.0 遍历前的记录
        ArrayList<CGNode[]> relations = new ArrayList<>();

        // 4.遍历cg中所有的节点
        for (CGNode node : cg) {
            // node中包含了很多信息，包括类加载器、方法信息等，这里只筛选出需要的信息
            CGNodeInfo cgNodeInfo = Utils.cgNodeInfoGetter(node);
            if (cgNodeInfo == null) continue;

            Iterator<CGNode> succNodes = cg.getSuccNodes(node);
            succNodes.forEachRemaining(succ -> {
                CGNodeInfo succInfo = Utils.cgNodeInfoGetter(succ);
                if (succInfo == null) return;
                relations.add(new CGNode[]{succ, node});
            });

            Iterator<CGNode> predNodes = cg.getPredNodes(node);
            predNodes.forEachRemaining(pred -> {
                CGNodeInfo predInfo = Utils.cgNodeInfoGetter(pred);
                if (predInfo == null) return;
                relations.add(new CGNode[]{node, pred});
            });

        }

        return relations;
    }

}
