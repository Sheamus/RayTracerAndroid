package com.serg.raytracer;

public class Color {
	public byte R;
	public byte G;
	public byte B;
	
	public static Color Black()
	{
		return FromArgb((byte)0, (byte)0, (byte)0);
	}
	
	public static Color FromArgb(byte r, byte g, byte b)
	{
		Color c = new Color();
		c.R = r;
		c.G = g;
		c.B = b;
		return c;
	}
	
	public static int ToInt(Color c)
	{
		return (c.R & 0xff) + ((c.G >> 8) & 0xff) + ((c.G >> 16) & 0xff); 
	}
}
