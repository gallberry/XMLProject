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
    /** NULL�^�O�p������ */
    private static final String NULL = "null";

    /** �󕶎��^�O�p������ */
    private static final String KARA = "kara";

    /** ���X�g�p�^�O�� */
    private static final String DATA_LIST = "DATA_LIST";

    /** �ԋp�pMap */
    private static HashMap returnMap = new HashMap();

    /** ���݂̃^�O�� */
    private static String tagName = null;

    /** ���̃N���X�B��̃C���X�^���X */
    private static SAXHandler instance = null;

    /** ��͒��̃��O���o���t���O */
    private boolean isDebug = true;

    //�r���̃f�[�^���i�[���邽�߂̃C���X�^���X
    private HashMap[] maps = new HashMap[64];
    private String[] mapsName = new String[64];
    private ArrayList[] lists = new ArrayList[8];
    private String[] listsName = new String[8];

    /** ���݂̃}�b�v�K�w */
    private int position = 0;

    /** ���݂̃��X�g�� */
    private int listNum = 0;

    /** ���݁A�l�̓ǂݍ��ݒ����ǂ��� */
    private boolean readValue = false;
    
    /** ���݃��X�g�f�[�^�̓ǂݍ��ݒ����ǂ��� */
    private boolean readList = false;

    /**
     * �V���O���g���p�^�[���̂��߂̃R���X�g���N�^
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
     * @return �ԋp�p�}�b�v
     */
    public HashMap getMap() {
        return returnMap;
    }

    /**
     * �B��̃C���X�^���X�������\�b�h
     * @return �C���X�^���X
     */
    public static SAXHandler getInstance() {
        if (instance == null) {
            instance = new SAXHandler();
        }

        return instance;
    }

    /**
     * ����������1�x�����p�[�X��������
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
        debug("�h�L�������g�J�n");
        maps[position] = new HashMap();
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName,
        Attributes attributes) {
        //�l�̓ǂݍ��ݒ��ɊJ�n�v�f��������1�K�w������
        if (readValue) {
            debug("��1�K�w������܂�");
            mapsName[position] = tagName;
            debug("�e[" + mapsName[position] + "]");
            tagName = qName;
            maps[++position] = new HashMap();
        } else {
            tagName = qName;
            readValue = true;

            if (qName.equals(DATA_LIST)) {
                debug("DATA_LIST�����o����܂���");
                debug("�v�fname[" + attributes.getValue("name") + "]");
                lists[listNum] = new ArrayList();
                listsName[listNum] = attributes.getValue("name");
                listNum++;
                readList = true;
            }
        }

        debug("�v�f�J�n[" + position + "]:" + qName);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int offset, int length) {
        String data = new String(ch, offset, length);

        //�l�����݂��Ȃ���ΏI��
        if ((data == null) || (data.trim().length() == 0)) {
            return;
        }

        debug("�e�L�X�g�f�[�^[" + position + "]�F" + data);

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
            debug("��1�K�w�オ��܂�");
            maps[position - 1].put(mapsName[position - 1], maps[position]);
            position--;
        }

        //DATA_LIST�^�O���I�������烊�X�g���}�b�v�ɕt�^
        if (qName.equals(DATA_LIST)) {
            debug("DATA_LIST���I�����܂���");
            debug("DATA_LIST��[" + listsName[listNum -1] + "]");
//            for (int i = 0; i < lists.length; i++) {
//                System.out.println(lists[listNum - 1].get(i));
//            }
            maps[position].put(listsName[listNum-1],lists[listNum-1]);
        }

        debug("�v�f�I��[" + position + "]:" + qName + "\n");
        readValue = false;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() {
        debug("�h�L�������g�I��");

        //�߂�l��map���Z�b�g
        returnMap.putAll(maps[position]);
    }
}
