package com.serg.raytracer;

import java.util.ArrayList;

import android.util.Log;

public class Sphere extends obj_base
{

    public Vector center;
    public double r;

    public Sphere(double x, double y, double z, double _r, Color col, double reflect, double transparent, double n)
    {
        center = new Vector(x, y, z);
        r = _r;
        material.color = col;
        material.reflictivity = reflect;
        material.transparency = transparent;
	    n2 = n;
    }

    public ArrayList<RayPoint> Intersection(Ray ray)
    {
    	ArrayList<RayPoint> RayIntersection = new ArrayList<RayPoint>();
    	
        Vector visual = Vector.op_minus(ray.p2, ray.p1).normalize();
        double b = Vector.op_mult(visual, Vector.op_minus(ray.p1, center));
        double c2 = Vector.op_minus(ray.p1, center).magnitude2() - r * r;
        if ((b * b - c2) >= 0.0f)
        {
            double t1 = -b - Math.sqrt(b * b - c2);
            double t2 = -b + Math.sqrt(b * b - c2);

            if (t1 > 0.0f && t1 <= t2)
            {
                RayPoint rp = new RayPoint();
                rp.p = Vector.op_plus(ray.p1, Vector.op_mult(visual, t1));
                rp.normal = Vector.op_minus(rp.p, center).normalize();
                rp.t = t1;
                rp.obj_index = this.index;
                RayIntersection.add(rp);
            }

            if (t2 > 0.0f && t2 < t1)
            {
                RayPoint rp = new RayPoint();
                rp.p = Vector.op_plus(ray.p1, Vector.op_mult(visual, t2));
                rp.normal = Vector.op_minus(rp.p, center).normalize();
                rp.t = t2;
                rp.obj_index = this.index;
                RayIntersection.add(rp);
            }
        }
        return RayIntersection;
    }

    public Color GetColor(Vector p)
    {
        return material.color;
    }
}