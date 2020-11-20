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

        if (args.length < 3) throw new Exception("wrong arguments");
        else {
            String project_target = args[1];
            String change_info = args[2];
            if (project_target.contains("\\") && !project_target.endsWith("\\"))
                project_target = project_target + "\\";
            else if (project_target.contains("/") && !project_target.endsWith("/"))
                project_target = project_target + "/";

            scope = Steps.buildScope(project_target, change_info);
            List<CGNode[]> relations = Steps.scan(scope);
            if ("-c".equals(args[0])) {
                // 类级
                selection = new ClassSelection();
//                classSelection(relations, Utils.utilGetProjectName(args[1]), "reports/", args[2]);
            } else if ("-m".equals(args[0])) {
                // 方法级
                selection = new MethodSelection();
//                methodSelection(relations);
            } else throw new Exception("wrong type argument");

            selection.execSelection(relations,
                    Utils.utilGetProjectName(project_target),
                    "reports/",
                    change_info);
        }
        // /Users/jinyuzhu/Desktop/ClassicAutomatedTesting/0-CMD
        // /Users/jinyuzhu/Desktop/ClassicAutomatedTesting/0-CMD/data/change_info.txt

//        String path = "/Users/jinyuzhu/Desktop/ClassicAutomatedTesting/0-CMD";
//        scope = Steps.buildScope(path + "/", path + "/data/change_info.txt");
//        List<CGNode[]> relations = Steps.scan(scope);
//        selection = new MethodSelection();
//        selection.execSelection(relations, Utils.utilGetProjectName(path + "/"),
//                "reports/", path + "/data/change_info.txt");

    }


}
