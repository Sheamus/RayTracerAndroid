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
		int left = pointPosition.get(Left)+1;
		int right = pointPosition.get(Right)+1;
		int result = 0;

		if (Name.equals("-"))
			result = OperationRule.RuleTable[left][right][1];
		if (Name.equals("+"))
			result = OperationRule.RuleTable[left][right][3];
		if (Name.equals("&"))
			result = OperationRule.RuleTable[left][right][4];

		//Log.i("GetResult()", "A(" + (left-1) + ") "+Name+" " + "B("+(right-1) + ") = " + result);

		return result;
	}
}
