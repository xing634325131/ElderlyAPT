package com.kingtime.freeweather.utils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * @author xp
 * @created 2014年10月3日
 */
public class XMLReader {

	public static List<String> readToStringList(String xmlString) throws Exception{
		List<String> stringInfos = new ArrayList<String>();
		
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		XmlPullParser parser = factory.newPullParser();
		parser.setInput(new StringReader(xmlString));
		//System.out.println(xmlString);
		int eventType = parser.getEventType();
		while(eventType != XmlPullParser.END_DOCUMENT){
			//System.out.println("eventtype-->"+eventType);
			if(eventType == XmlPullParser.START_TAG){
				//System.out.println(parser.getName());
				if(parser.getName().equals("string")){
					parser.next();
					//System.out.println(parser.getText());
					stringInfos.add(parser.getText());
				}
			}
			eventType = parser.next();
		}
	
		return stringInfos;
	}
}
