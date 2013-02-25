package com.serg.raytracer;

import java.util.ArrayList;

public class obj_base {
	public int index;
	public Color color;
	public double n2;
	
	public obj_base()
	{
	}
	
	public ArrayList<RayPoint> Intersection(Ray ray)
	{
		//RayIntersection
		return null;
	}
	
	public Color GetColor(Vector p)
	{
		return color;
	}
}
