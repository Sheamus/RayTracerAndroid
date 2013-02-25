package com.serg.raytracer;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.widget.ImageView;

public class Board {

	public Board()
	{
		
	}
	
	public Bitmap Init(){
		Bitmap bitmap = Bitmap.createBitmap(320, 480, Config.ARGB_8888);  
        
        Canvas canvas = new Canvas(bitmap);  
        Paint p = new Paint();   
        p.setAntiAlias(true);  
        p.setStyle(Paint.Style.FILL);  
        p.setStrokeWidth(1);  
          
        Paint p2 = new Paint();   
        p2.setAntiAlias(true);  
        p2.setColor(0xffffffff);   
        p2.setStyle(Paint.Style.FILL);  

        int sizeX = 10;
        int sizeY = 10;
        int cellsX = 320 / sizeX;
        int cellsY = (480 - 50 - 80) / sizeY;
        Random random = new Random();

        for(int j=0;j<cellsY;j++)
        {
        	for(int i=0;i<cellsX; i++)
        	{
                p.setColor(0xff777777);
                if((i+j)%2 == 0)
                    p.setColor(0xffffffff);
                
        		canvas.drawRect(i*sizeX,  j*sizeY + 50, (i+1)*sizeX-1, (j+1)*sizeY-1 + 50, p);
        	}
        }

        return bitmap;
	}
}
