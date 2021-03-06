package com.CulLight.Chat.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UniqueIdentifier {
	
	//this class should have only one instance (singelton)
	//easiest way is to have a private constructor and a static method to access the class
	private UniqueIdentifier() {}
	
	private static List<Integer> ids = new ArrayList<Integer>();
	//number of unique identifier
	private static final int RANGE = 10000;
	
	private static int index = 0;
	
	//because class is static, the following class will run without any method
	//it will just exist
	static {
		for(int i = 0; i < RANGE; i++) {
			ids.add(i);
		}
		//shuffle array
		Collections.shuffle(ids);
	}
		
	public static int getIdentifier() {
		if (index > ids.size() - 1) index = 0;
		return ids.get(index++);
	}
}
