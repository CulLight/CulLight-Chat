package com.CulLight.Chat.server;

import java.util.ArrayList;
import java.util.List;

public class UniqueColor {
	
	private static List<String> colorStr = new ArrayList<String>();
	private static int index = 0;
	
	private UniqueColor(){}
	
	static {
		colorStr.add("blue");
		colorStr.add("red");
		colorStr.add("green");
		colorStr.add("cyan");
		colorStr.add("gray");
		colorStr.add("magenta");
		colorStr.add("orange");
		colorStr.add("pink");
		colorStr.add("yellow");
	}
	
	public static String getUniqueColor() {
		if (index > colorStr.size() - 1) index = 0;
		return colorStr.get(index++);
	}
	
}
