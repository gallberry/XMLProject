package jp.xml.sax;

import org.xml.sax.SAXException;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;

public class SAXParserTest {
    /** 読み込むXMLファイル */
    private static String FILE_NAME = "data\\TCA01TestData.xml";

    public static void main(String[] args) {
        try {
            System.out.println("#### > SAXParserTest");
            
            //インスタンスの生成、及び初期化
            SAXHandler.getInstance().init(FILE_NAME);

            //変換後のMapを取得
            HashMap map = SAXHandler.getInstance().getMap();
            
            //Mapの出力
            System.out.println("\n==== 変換結果 ====");
            PrintUtil.mapToXML(map, 0);

            System.out.println("#### < SAXParserTest");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
