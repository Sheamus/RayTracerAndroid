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

	//��� ���� ��������� ������� ����� ������������ ���� ���������� CSG-������� ��������� � ��������� 
	public int Calculate(HashMap<Integer, Integer> pointPosition) {
		return 0;
	}
}
