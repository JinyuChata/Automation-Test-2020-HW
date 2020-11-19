package com.example.autoTest.selection;

import com.example.autoTest.Utils;
import com.ibm.wala.ipa.callgraph.CGNode;

import java.util.HashSet;

public class MethodSelection extends AbstractSelection {
    @Override
    public String getType() {
        return "method";
    }

    @Override
    public String genDotString(CGNode[] relation) {
        return "\""
                + Utils.utilGetMethodSignature(relation[0])
                + "\" -> \""
                + Utils.utilGetMethodSignature(relation[1]) + "\";";
    }

    @Override
    public void expandDependency(CGNode[] relation, HashSet<String> changedClassSet) {
        if (changedClassSet.contains(Utils.utilGetMethodSignature(relation[0]))
                && !Utils.utilGetMethodSignature(relation[1]).contains("Test"))
            changedClassSet.add(Utils.utilGetMethodSignature(relation[1]));
    }

    @Override
    public void genSelectionResult(CGNode[] relation, HashSet<String> changedClassSet, HashSet<String> result) {
        if (changedClassSet.contains(Utils.utilGetMethodSignature(relation[0]))
                && Utils.utilGetMethodSignature(relation[1]).contains("Test")
                && !Utils.utilGetMethodSignature(relation[1]).contains("<init>"))
            result.add(Utils.utilGetClassName(relation[1]) + " " + Utils.utilGetMethodSignature(relation[1]));
    }

    @Override
    public String chooseSplitPattern(String line) {
        return line.split(" ")[1];
    }
}
