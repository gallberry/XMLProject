package jp.xml.sax;

import org.xml.sax.SAXException;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;

public class SAXParserTest {
    /** �ǂݍ���XML�t�@�C�� */
    private static String FILE_NAME = "data\\TCA01TestData.xml";

    public static void main(String[] args) {
        try {
            System.out.println("#### > SAXParserTest");
            
            //�C���X�^���X�̐����A�y�я�����
            SAXHandler.getInstance().init(FILE_NAME);

            //�ϊ����Map���擾
            HashMap map = SAXHandler.getInstance().getMap();
            
            //Map�̏o��
            System.out.println("\n==== �ϊ����� ====");
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
