package com.serg.raytracer;

public class OperationRule {
	public static int[][][] RuleTable = new int[][][] //array [-1..1, -1..1, 0..4] of integer =
		    {{{ 1,  -1,  -1,  -1,  -1},
	          { 1,  -1,   0,   0,  -1},
	          { 1,  -1,   1,   1,  -1}},
	         {{ 0,   0,  -1,   0,  -1},
	          { 0,   0,  -1,   0,   0},
	          { 0,  -1,   0,   1,   0}},
		     {{-1,   1,  -1,   1,  -1},
	          {-1,   0,  -1,   1,   0},
	          {-1,  -1,  -1,   1,   1}}};
			// -A   A-B  B-A   A+B A&B
}
