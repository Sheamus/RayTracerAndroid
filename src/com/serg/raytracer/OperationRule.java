package com.serg.raytracer;

public class OperationRule {
	public static int[][][] RuleTable = new int[][][] 
			// -A   A-B  B-A  A+B  A&B
			{{{ 1,  -1,  -1,  -1,  -1},
	          { 1,  -1,   0,   0,  -1},
	          { 1,  -1,   1,   1,  -1}},
	         {{ 0,   0,  -1,   0,  -1},
	          { 0,   0,  -1,   0,   0},
	          { 0,  -1,   0,   1,   0}},
		     {{-1,   1,  -1,   1,  -1},
	          {-1,   0,  -1,   1,   0},
	          {-1,  -1,  -1,   1,   1}}};
}
