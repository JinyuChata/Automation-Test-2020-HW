package com.example.autoTest;

import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
    public static String utilGetClassName(CGNode node) {
        return node.getMethod().getDeclaringClass().getName().toString();
    }

    public static String utilGetMethodSignature(CGNode node) {
        return node.getMethod().getSignature();
    }

    public static String utilGetProjectName(String project_target) {
        String fileSeparator = "/";
        if (project_target.endsWith("/") || project_target.endsWith("\\")) {
            project_target = project_target.substring(0, project_target.length()-1);
        }
        String[] paths = project_target.split(File.separator.equals("\\") ? "\\\\" : File.separator);
        List<String> asList = Arrays.asList(paths);
        project_target = String.join(File.separator, asList.subList(0, asList.size() - 1)) + File.separator;
        System.out.println(project_target);

//        int i = project_target.lastIndexOf(fileSeparator);
//        if (i == -1) {
//            fileSeparator = "/";
//            i = project_target.lastIndexOf(fileSeparator);
//        }

        File pomFile = new File(project_target + "pom.xml");
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(pomFile);
            NodeList nodeList = doc.getElementsByTagName("artifactId");
            return nodeList.item(0).getFirstChild().getNodeValue();
        } catch (Exception e) {
            System.err.println("读取该xml文件失败");
            e.printStackTrace();
        }
        return null;
    }

    public static List<File> getDir(File file) {
        File[] fileArray = file.listFiles();
        List<File> resFiles = new ArrayList<>();
        assert fileArray != null;
        for (File f : fileArray) {
            if (f.isDirectory()) {
                resFiles.addAll(getDir(f));
            } else {
                if (f.getName().endsWith(".class")) {
                    System.out.println(f.getAbsolutePath());
                    resFiles.add(f);
                }
            }
        }

        return resFiles;
    }

    public static CGNodeInfo cgNodeInfoGetter(CGNode node) {
        // node中包含了很多信息，包括类加载器、方法信息等，这里只筛选出需要的信息
        if (node.getMethod() instanceof ShrikeBTMethod) {
            // node.getMethod()返回一个比较泛化的IMethod实例，不能获取到我们想要的信息
            // 一般地，本项目中所有和业务逻辑相关的方法都是ShrikeBTMethod对象
            ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
            // 使用Primordial类加载器加载的类都属于Java原生类，我们一般不关心。
            if ("Application".equals(method.getDeclaringClass().getClassLoader().toString())) {
                // 获取声明该方法的类的内部表示
                String classInnerName = method.getDeclaringClass().getName().toString();
                // 获取方法签名
                String signature = method.getSignature();

                // $
                if (classInnerName.contains("$")) return null;
                System.out.println(classInnerName + " " + signature);
                return new CGNodeInfo(classInnerName, signature);
            }
        }
        return null;
    }

}
