package jp.xml.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 *
 * @author Administrator
 */
public class SAXHandler extends DefaultHandler {
    /** NULLタグ用文字列 */
    private static final String NULL = "null";

    /** 空文字タグ用文字列 */
    private static final String KARA = "kara";

    /** リスト用タグ名 */
    private static final String DATA_LIST = "DATA_LIST";

    /** 返却用Map */
    private static HashMap returnMap = new HashMap();

    /** 現在のタグ名 */
    private static String tagName = null;

    /** このクラス唯一のインスタンス */
    private static SAXHandler instance = null;

    /** 解析中のログを出すフラグ */
    private boolean isDebug = true;

    //途中のデータを格納するためのインスタンス
    private HashMap[] maps = new HashMap[64];
    private String[] mapsName = new String[64];
    private ArrayList[] lists = new ArrayList[8];
    private String[] listsName = new String[8];

    /** 現在のマップ階層 */
    private int position = 0;

    /** 現在のリスト数 */
    private int listNum = 0;

    /** 現在、値の読み込み中かどうか */
    private boolean readValue = false;
    
    /** 現在リストデータの読み込み中かどうか */
    private boolean readList = false;

    /**
     * シングルトンパターンのためのコンストラクタ
     */
    private SAXHandler() {
    }

    /**
     * @param str
     */
    private void debug(String str) {
        if (isDebug) {
            StringBuffer buff = new StringBuffer();

            for (int i = 0; i < position; i++) {
                buff.append("  ");
            }

            System.out.println(buff.toString() + str);
            buff = null;
        }
    }

    /**
     * @return 返却用マップ
     */
    public HashMap getMap() {
        return returnMap;
    }

    /**
     * 唯一のインスタンス生成メソッド
     * @return インスタンス
     */
    public static SAXHandler getInstance() {
        if (instance == null) {
            instance = new SAXHandler();
        }

        return instance;
    }

    /**
     * 初期化時に1度だけパースをかける
     * @param fileName
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public void init(String fileName)
        throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(new File(fileName), this);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() {
        debug("ドキュメント開始");
        maps[position] = new HashMap();
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName,
        Attributes attributes) {
        //値の読み込み中に開始要素がきたら1階層下がる
        if (readValue) {
            debug("→1階層下がります");
            mapsName[position] = tagName;
            debug("親[" + mapsName[position] + "]");
            tagName = qName;
            maps[++position] = new HashMap();
        } else {
            tagName = qName;
            readValue = true;

            if (qName.equals(DATA_LIST)) {
                debug("DATA_LISTが検出されました");
                debug("要素name[" + attributes.getValue("name") + "]");
                lists[listNum] = new ArrayList();
                listsName[listNum] = attributes.getValue("name");
                listNum++;
                readList = true;
            }
        }

        debug("要素開始[" + position + "]:" + qName);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int offset, int length) {
        String data = new String(ch, offset, length);

        //値が存在しなければ終了
        if ((data == null) || (data.trim().length() == 0)) {
            return;
        }

        debug("テキストデータ[" + position + "]：" + data);

        if (data.compareToIgnoreCase(NULL) == 0) {
            maps[position].put(tagName, null);
        } else if (data.compareToIgnoreCase(KARA) == 0) {
            maps[position].put(tagName, "");
        } else {
            maps[position].put(tagName, data);
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName) {
        if ((position > 0) && qName.equals(mapsName[(position - 1)])) {
            debug("←1階層上がります");
            maps[position - 1].put(mapsName[position - 1], maps[position]);
            position--;
        }

        //DATA_LISTタグが終了したらリストをマップに付与
        if (qName.equals(DATA_LIST)) {
            debug("DATA_LISTが終了しました");
            debug("DATA_LIST名[" + listsName[listNum -1] + "]");
//            for (int i = 0; i < lists.length; i++) {
//                System.out.println(lists[listNum - 1].get(i));
//            }
            maps[position].put(listsName[listNum-1],lists[listNum-1]);
        }

        debug("要素終了[" + position + "]:" + qName + "\n");
        readValue = false;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() {
        debug("ドキュメント終了");

        //戻り値にmapをセット
        returnMap.putAll(maps[position]);
    }
}
