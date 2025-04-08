package com.pms.core_bape_web.utils;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XMLParser {    public Document generateXML(String processPath)
{
    Document doc = null;
    // read the Process Fragment
    File fXmlFile = new File(processPath);
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = null;
    try {
        dBuilder = dbFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
    }

    try {
        assert dBuilder != null;
        doc = dBuilder.parse(fXmlFile);
    } catch (SAXException | IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
    }

    assert doc != null;
    doc.getDocumentElement().normalize();

    return doc;
}
}
