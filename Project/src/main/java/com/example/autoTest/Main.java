package com.example.autoTest;

import com.example.autoTest.selection.AbstractSelection;
import com.example.autoTest.selection.ClassSelection;
import com.example.autoTest.selection.MethodSelection;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;

import java.io.*;
import java.util.HashSet;
import java.util.List;

// /Users/jinyuzhu/Desktop/ClassicAutomatedTesting
public class Main {


    public static void main(String[] args) throws Exception {
        AnalysisScope scope;
        AbstractSelection selection;

//        if (args.length < 3) throw new Exception("wrong arguments");
//        else {
//            scope = Steps.buildScope(args[1], args[2]);
//            List<CGNode[]> relations = Steps.scan(scope);
//            if ("-c".equals(args[0])) {
//                // 类级
//                classSelection(relations, Utils.utilGetProjectName(args[1]), "reports/", args[2]);
//            } else if ("-m".equals(args[0])) {
//                // 方法级
//                methodSelection(relations);
//            } else throw new Exception("wrong type argument");
//        }

        String path = "/Users/jinyuzhu/Desktop/ClassicAutomatedTesting/0-CMD";
        scope = Steps.buildScope(path + "/", path + "/data/change_info.txt");
        List<CGNode[]> relations = Steps.scan(scope);
        selection = new MethodSelection();
        selection.execSelection(relations, Utils.utilGetProjectName(path + "/"),
                "reports/", path + "/data/change_info.txt");

    }


}
