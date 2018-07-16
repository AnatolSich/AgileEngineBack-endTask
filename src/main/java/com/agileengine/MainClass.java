package com.agileengine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MainClass {

    private static String resourcePath;
    private static String targetElementId;
    private static String diffResourcePath;

    public static void main(String[] args) {

        checkInputs(args);
        Optional<Element> targetElementOpt = findElementById(new File(resourcePath), targetElementId);
        Optional<Elements> allDiffResourceElementsOpt = findAllPageElements(new File(diffResourcePath));

        if (!targetElementOpt.isPresent()) {
            System.out.println("No original element found");
            System.exit(1);
        }
        Element targetElement = targetElementOpt.get();

        if (!allDiffResourceElementsOpt.isPresent()) {
            System.out.println("No elements in diff-case found");
            System.exit(1);
        }

        Elements allDiffResourceElements = allDiffResourceElementsOpt.get();

        if (allDiffResourceElements.isEmpty()) {
            System.out.println("No elements in diff-case found");
            System.exit(1);
        } else {
            List<StringBuilder> list = findGoalElementPath(targetElement, allDiffResourceElements);
            System.out.println("Result:");
            for (StringBuilder str:list
                    ) {
                System.out.println(str);
            }

        }
    }


    private static List<StringBuilder> findGoalElementPath(Element targetElementOpt,
                                                           Elements allDiffResourceElementsOpt) {
        List<StringBuilder> list = new ArrayList<>();
        Map<Element, Integer> map = new HashMap<>();
        List<Attribute> targetAttrs = targetElementOpt.attributes().asList();
        boolean flag = false;
        int maxMatchNumber = 0;
        Element goalElement = null;

        for (Element element : allDiffResourceElementsOpt
                ) {
            int matchNumber = 0;

            List<Attribute> tempAttrs = element.attributes().asList();

            for (Attribute targetAttr : targetAttrs
                    ) {
                for (Attribute tempAttr : tempAttrs
                        ) {
                    if (tempAttr.equals(targetAttr)) {
                        matchNumber++;
                        break;
                    }
                }
            }
            map.put(element, matchNumber);
            if (matchNumber > maxMatchNumber) {
                maxMatchNumber = matchNumber;
                goalElement = element;
                flag = true;
            } else if (matchNumber == maxMatchNumber) {
                flag = false;
            }
        }

        if (maxMatchNumber > 0) {
            System.out.println("******Goal Element********");
            if (flag) {
                System.out.println(goalElement);
                list.add(getPathFromElement(goalElement));
            } else {
                System.out.println("Multiple result");
                for (Map.Entry<Element, Integer> entry : map.entrySet()
                        ) {
                    Element key = entry.getKey();
                    Integer value = entry.getValue();
                    if (value == maxMatchNumber) {
                        list.add(getPathFromElement(key));
                    }
                }
            }
            System.out.println("**************");
        } else {
            list.add(new StringBuilder("No fitted element in diff-case found"));
        }
        return list;
    }

    private static StringBuilder getPathFromElement(Element element) {
        List<Element> parents = element.parents();
        StringBuilder goal = new StringBuilder();
        for (int i = parents.size() - 1; i > 0; i--) {
            if (i < parents.size() - 1) {
                goal = goal.append(">");
            }
            goal = goal.append(parents.get(i).tagName());
        }
        return goal;
    }

    private static Optional<Elements> findAllPageElements(File htmlFile) {
        try {
            Document doc = Jsoup.parse(
                    htmlFile,
                    "utf8",
                    htmlFile.getAbsolutePath());
            return Optional.of(doc.getAllElements());
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file: " + htmlFile.getAbsolutePath());
            System.out.println("Invalid diff-case filepath");
            System.exit(1);
        } catch (NullPointerException e) {
            System.out.println("No elements in diff-case found");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return Optional.empty();
    }

    private static Optional<Element> findElementById(File htmlFile, String targetElementId) {
        try {
            Document doc = Jsoup.parse(
                    htmlFile,
                    "utf8",
                    htmlFile.getAbsolutePath());
            System.out.println("********targetElement attributes********");
            for (Attribute atr : doc.getElementById(targetElementId).attributes()
                    ) {
                System.out.println(atr);
            }
            System.out.println("**************");
            return Optional.of(doc.getElementById(targetElementId));
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file: " + htmlFile.getAbsolutePath());
            System.out.println("Invalid original filepath");
            System.exit(1);
        } catch (NullPointerException e) {
            System.out.println("No original element found");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return Optional.empty();
    }

    private static void checkInputs(String args[]) {
        int argNumber = args.length;
        if (argNumber > 3) {
            System.out.println("Too much arguments");
            System.exit(1);
        } else if (argNumber < 2) {
            System.out.println("Not enough arguments");
            System.exit(1);
        } else if (argNumber == 2) {
            File file1 = new File(args[0]);
            File file2 = new File(args[1]);
            if (file1.isFile() & file2.isFile()) {
                resourcePath = args[0];
                targetElementId = "make-everything-ok-button";
                diffResourcePath = args[1];
            } else {
                System.out.println("Must be filepath arguments");
                System.exit(1);
            }
        } else {
            resourcePath = args[0];
            targetElementId = args[1];
            diffResourcePath = args[2];
        }
        System.out.println("******Inputs********");
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                System.out.print("resourcePath = ");
                System.out.println(resourcePath);
            }
            if (i == 1) {
                System.out.print("targetElementId = ");
                System.out.println(targetElementId);
            }
            if (i == 2) {
                System.out.print("diffResourcePath = ");
                System.out.println(diffResourcePath);
            }
        }
        System.out.println("************");
    }
}
