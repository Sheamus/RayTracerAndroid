package com.serg.raytracer;

import java.util.HashMap;

import android.util.Log;

public class Operation {
	public int Left;
	public int Right;
	public String Name;

	public Operation(String name, int left, int right){
		Name = name;
		Left = left;
		Right = right;
	}
	
	public int GetResult(HashMap<Integer, Integer> pointPosition)
	{
		if (pointPosition == null) return 0;
		Log.i("GetResult()", "Left=" + Left + ", " + "Right="+Right);
		int left = pointPosition.get(Left)+1;
		int right = pointPosition.get(Right)+1;
		
		if (Name == "-")
			return OperationRule.RuleTable[left][right][1];
		if (Name == "+")
			return OperationRule.RuleTable[left][right][3];
		if (Name == "&")
			return OperationRule.RuleTable[left][right][4];
		
		return 0;
	}
}
