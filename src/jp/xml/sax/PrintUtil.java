package jp.xml.sax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class PrintUtil {
    public static void mapToXML(Map map, int indent) {
        Iterator i = map.keySet().iterator();

        while (i.hasNext()) {
            String key = (String) i.next();
            Object value = map.get(key);

            if (value instanceof String) {
                System.out.print(addIndent(indent));
                System.out.println("<" + key + ">" + value + "</" + key + ">");
            } else if (value instanceof HashMap) {
                System.out.println(addIndent(indent) + "<" + key + ">");
                mapToXML((HashMap) value, indent + 1);
                System.out.println(addIndent(indent) + "</" + key + ">");
            } else if(value instanceof ArrayList){
                System.out.println(addIndent(indent) + "<" + key + ">");
                System.out.println(addIndent(indent) + "ArrayList‚ª•\Ž¦‚³‚ê‚é—\’è");
                System.out.println(addIndent(indent) + "</" + key + ">");
            } else {
                System.out.print(addIndent(indent));

                if (value != null) {
                    System.out.println("<" + key + " class=" +
                        value.getClass().getName() + "/>");
                } else {
                    System.out.println("<" + key + ">null</" + key + ">");
                }
            }
        }
    }

    private static String addIndent(int indent) {
        StringBuffer buff = new StringBuffer();

        for (int i = 0; i < indent; i++) {
            buff.append("    ");
        }

        return buff.toString();
    }
}
