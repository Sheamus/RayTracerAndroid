package com.serg.raytracer;

import java.util.ArrayList;

public class obj_base {
	public int index;
	public double n2;
	public Material material;
	
	public obj_base()
	{
		material = new Material(0.1, 0.1);
	}
	
	public ArrayList<RayPoint> Intersection(Ray ray)
	{
		//RayIntersection
		return null;
	}
	
	public Color GetColor(Vector p)
	{
		return material.color;
	}
}
