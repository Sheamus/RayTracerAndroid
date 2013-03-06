package com.serg.raytracer;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class Scene {
	public double[][] F = new double[4][4];
	public double[][] F_1 = new double[4][4];
	public double FOCUS = 10000.0f;
	public double alpha = 0.0f;
	public boolean Shadows = true;
	public int MaxReflection = 3;
	public int Reflections;
	public int MaxRefraction = 0;
	public int Refractions;
	public Vector light;	
	
	public int[][][] RuleTable = new int[][][] //array [-1..1, -1..1, 1..5] of integer =
	    {{{ 1,  -1,  -1,  -1,  -1},
          { 1,  -1,   0,   0,  -1},
          { 1,  -1,   1,   1,  -1}},
         {{ 0,   0,  -1,   0,  -1},
          { 0,  -1,  -1,   0,   0},
          { 0,  -1,   0,   1,   0}},
	     {{-1,   1,  -1,   1,  -1},
          {-1,   0,  -1,   1,   0},
          {-1,  -1,  -1,   1,   1}}};
		// -A   A-B  B-A   A+B A&B
		
	public ArrayList<BaseObject> objects;
	public ArrayList<CSGobject> csgObjects;
	
	public Scene()
	{
		objects = new ArrayList<BaseObject>();
		csgObjects = new ArrayList<CSGobject>();
		light = new Vector();
	}

	public void SetCamera(Vector pos, double p, double q)
	{
		double lat = q * Math.PI/180.0f;
		double lon = p * Math.PI/180.0f;
		double cos_a = Math.cos(lat);
		double sin_a = Math.sin(lat);
		double cos_b = Math.cos(lon);
		double sin_b = Math.sin(lon);
		double Sa = pos.m_x * cos_a - pos.m_y * sin_a;
		double Sb = -pos.m_z * cos_b + sin_b * (pos.m_x * sin_a + pos.m_y * cos_a);
		double Sc = pos.m_z * sin_b + cos_b * (pos.m_x * sin_a + pos.m_y * cos_a);
		F[0][0] = - cos_a;
		F[1][0] = - sin_a * sin_b;
		F[2][0] = - sin_a * cos_b;
		F[3][0] = 0.0f;
		F[0][1] = sin_a;
		F[1][1] = - cos_a * sin_b;
		F[2][1] = - cos_a * cos_b;
		F[3][1] = 0.0f;
		F[0][2] = 0.0f;
		F[1][2] = cos_b * sin_b;
		F[2][2] = sin_b;
		F[3][2] = 0.0f;
		F[0][3] = Sa;
		F[1][3] = Sb;
		F[2][3] = Sc;
		F[3][3] = 1.0f;
		
		F_1[0][0] = - cos_a;
		F_1[1][0] = sin_a;
		F_1[2][0] = 0.0f;
		F_1[3][0] = 0.0f;
		F_1[0][1] = - sin_a * sin_b;
		F_1[1][1] = - sin_b * cos_a;
		F_1[2][1] = cos_b;
		F_1[3][1] = 0.0f;
		F_1[0][2] = - sin_a * cos_b;
		F_1[1][2] = - cos_b * cos_a;
		F_1[2][2] = - sin_b;
		F_1[3][2] = 0.0f;
		F_1[0][3] = pos.m_x;
		F_1[1][3] = pos.m_y;
		F_1[2][3] = pos.m_z;
		F_1[3][3] = 1.0f;
	}

	public Vector EkrToObj(Vector p)
	{
		return new Vector(
			p.m_x * F_1[0][0] + p.m_y * F_1[0][1] + p.m_z * F_1[0][2] + F_1[0][3],
			p.m_x * F_1[1][0] + p.m_y * F_1[1][1] + p.m_z * F_1[1][2] + F_1[1][3],
			p.m_x * F_1[2][0] + p.m_y * F_1[2][1] + p.m_z * F_1[2][2] + F_1[2][3]
		);
	}
	
	public Ray ShootRay(int x, int y)
	{
		Ray ray = new Ray();
		Vector p2 = new Vector(x + F_1[0][3], y + F_1[1][3], 0f + F_1[2][3]);
		Vector p1 = new Vector(0f + F_1[0][3], 0f + F_1[1][3], -FOCUS + F_1[2][3]);
		
		Vector pob1 = new Vector(
			p1.m_x * Math.cos(alpha)-p1.m_y * Math.sin(alpha),
			p1.m_x * Math.sin(alpha) + p1.m_y * Math.cos(alpha),
			p1.m_z);
		Vector pob2 = new Vector(
			p2.m_x * Math.cos(alpha)-p2.m_y * Math.sin(alpha),
			p2.m_x * Math.sin(alpha) + p2.m_y * Math.cos(alpha),
			p2.m_z);

			ray.p1 = EkrToObj(pob1);
		ray.p2 = EkrToObj(pob2);
		
		return ray;
	}
	
	public ArrayList<RayPoint> IntersectAllObjects(Ray ray)
	{
		ArrayList<RayPoint> RayIntersection = new ArrayList<RayPoint>();
		
		//переделать на обработку групп CSG-объектов, а не примитивов по отдельности!
		for(int i=0; i<objects.size(); i++)
		{
			BaseObject ob = objects.get(i);
			ob.index = i;
			objects.set(i, ob);
			
			ArrayList<RayPoint> rps = objects.get(i).Intersection(ray);
			for(int j=0;j<rps.size();j++)
				RayIntersection.add(rps.get(j)); 
		}
		
		return RayIntersection;
	}
	
	public RayPoint Trace(Ray ray)
	{
		RayPoint pp = null; 
		
		//находим пересечения со всеми объектами сцены
		ArrayList<RayPoint> intersections = IntersectAllObjects(ray);

		//ищем ближайшую точку
		double t_min = Double.MAX_VALUE;
		for(int i=0; i<intersections.size(); i++)
			if (intersections.get(i).t<=t_min)
			{
				t_min = intersections.get(i).t;
				pp = intersections.get(i);
			}

		return pp;
	}
	
	public Color FullTrace(Ray ray)
	{  
		Color color = (new Color());
		//color = (new Color()).Gray();
		RayPoint rp = Trace(ray);
		if (rp!=null)
		{
			//вычисляем направление на источник света и определяем освещённость точки
			double ang_cos = Vector.op_mult((Vector.op_minus(light, rp.p)).normalize(), rp.normal);
			Color obj_color = objects.get(rp.obj_index).GetColor(rp.p);
			Color col = new Color();
			col.R = (int)(ang_cos * obj_color.R);
			col.G = (int)(ang_cos * obj_color.G);
			col.B = (int)(ang_cos * obj_color.B);
			if (ang_cos>0) 
				color = col;
			else
				color = Color.Black();

			//определяем затенённость точки
			if(Shadows)
			{
				Ray ray2ligth = new Ray();
				ray2ligth.p1 = rp.p;
				ray2ligth.p2 = light;
				ArrayList<RayPoint> pts = IntersectAllObjects(ray2ligth);
				
				if (pts.size()>0) 
				{	//ищем ближайшую точку
					RayPoint pp = null;
				    double t_min = Double.MAX_VALUE;
					for(int i=0; i<pts.size(); i++)
						if (pts.get(i).t<t_min && pts.get(i).t>0)
					{
						t_min = pts.get(i).t;
						pp = pts.get(i);
					}
					
					if (pp!=null)
						if (pp.t>1)
							color = Color.FromArgb((int)(0.5f * color.R),
			                           (int)(0.5f * color.G), 
			                           (int)(0.5f * color.B));
				}
			}
			
			//делаем переотражения
			if (Reflections++<MaxReflection && objects.get(rp.obj_index).material.reflictivity>0)
			{
				Vector fall = Vector.op_minus(rp.p, ray.p1).normalize();
				Vector norm = rp.normal.normalize();
				double cs = Vector.op_mult(fall, norm);
				Vector reflect = Vector.op_minus(fall, Vector.op_mult(Vector.op_mult(norm, cs), 2));
				
				Ray ray2 = new Ray();
				ray2.p1 = Vector.op_plus(rp.p, (Vector.op_mult(reflect, 0.000001f)));//смещаем первую точку чуть-чуть подальше от края объекта
				ray2.p2 = Vector.op_plus(rp.p, reflect);
				
				Color c2 = Color.Black();
				c2 = FullTrace(ray2);

				//учитываем коэффициент отражения. Чем он больше, тем зеркальнее поверхность текущего объекта
				c2.R = (int)(c2.R * objects.get(rp.obj_index).material.reflictivity);
				c2.G = (int)(c2.G * objects.get(rp.obj_index).material.reflictivity);
				c2.B = (int)(c2.B * objects.get(rp.obj_index).material.reflictivity);
				
				color = Color.FromArgb((int)((color.R + c2.R)>255?255:(color.R + c2.R)),
				                       (int)((color.G + c2.G)>255?255:(color.G + c2.G)),
				                       (int)((color.B + c2.B)>255?255:(color.B + c2.B)));
			}
			
			//преломление
			if (Refractions++<MaxRefraction && objects.get(rp.obj_index).material.transparency>0)
			{
				//запускаем луч в среду
				Vector fall = Vector.op_minus(rp.p, ray.p1).normalize();
				Vector norm = rp.normal.normalize();
				double cs1 = - Vector.op_mult(fall, norm);
				double n1 = 1.0002926f;//воздух
				double n2 = objects.get(rp.obj_index).n2;
				double n = n1/n2;
				double sq = 1.0f-n*n*(1.0f-cs1*cs1);
				//if (sq>=0)
				{
					double cs2 = Math.sqrt(sq);
					Vector refr = Vector.op_plus(Vector.op_mult(fall, n), Vector.op_mult(norm, (n*cs1-cs2)));
					
					//Random rnd = new Random();
					Ray ray2 = new Ray();
					ray2.p1 = Vector.op_plus(rp.p, (Vector.op_mult(refr, 0.000001f)));//смещаем первую точку чуть-чуть подальше от края объекта
					ray2.p2 = Vector.op_plus(rp.p, refr);

					Color c3 = new Color();
					c3 = FullTrace(ray2);
					
					//учитываем коэффициент поглощения. Чем он больше, тем прозрачнее объект
					c3.R = (int)(c3.R * objects.get(rp.obj_index).material.transparency);
					c3.G = (int)(c3.G * objects.get(rp.obj_index).material.transparency);
					c3.B = (int)(c3.B * objects.get(rp.obj_index).material.transparency);
					
					//Log.i("c3", c3.R + ", " + c3.G + ", " + c3.B);
					
					color = Color.FromArgb(((color.R + c3.R)>255?255:(color.R + c3.R)), 
							((color.G + c3.G)>255?255:(color.G + c3.G)),
							((color.B + c3.B)>255?255:(color.B + c3.B)));
				}

			}
		}
		return color;
	}
	
	public Color Render(int x, int y)
	{
		Ray ray = ShootRay(x, y);
		Reflections = 0;
		Refractions = 0;
		Color c = FullTrace(ray);
		return c;
	}


	public void BeginCSG(String name) {
		CSGobject csg = new CSGobject(name);
		csgObjects.add(csg);
	}


	public void AddObject(BaseObject object) {
		objects.add(object);
	}


	public void EndCSG() {
	}
}
