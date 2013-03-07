package com.serg.raytracer;

import java.util.ArrayList;
import java.util.HashMap;

public class CSGobject {
	public String Name;
	public ArrayList<Integer> objIndex;
	
	public CSGobject(String name)
	{
		Name = name;
		objIndex = new ArrayList<Integer>();
	}

	//для всех положений текущей точки относительно всех примитивов CSG-объекта вычисляем её положение 
	public int Calculate(HashMap<Integer, Integer> pointPosition) {
		return 0;
	}
}
