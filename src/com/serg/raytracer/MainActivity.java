//TODO: Сделать ProgressBar

package com.serg.raytracer;

import java.util.Random;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;


public class MainActivity extends Activity implements OnClickListener {

	private Bitmap bitmap;
	private Handler mHandler = new Handler();
	private boolean isRendering; 
	private Thread genThread;
	private ImageView image;
	private int steps = 0;
	int y = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	    Button btnOK = (Button) findViewById(R.id.button1);
	    btnOK.setOnClickListener(this);

	    Board board = new Board();
	    isRendering = false;
	    
	    ImageView image = (ImageView) findViewById(R.id.imageView1);
        image.setImageBitmap(board.Init());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void Render()
	{
		long start = System.currentTimeMillis();

        Scene s = new Scene();
        s.SetCamera(new Vector(-110.1f, -110.1f, -110.1f), 180f, 45f);
        s.MaxReflection = 2;
        s.MaxRefraction = 2;
        s.FOCUS = 1000;

        Random r = new Random();
        for(int i=0;i<20;i++)
        {
	        Color col = new Color().FromArgb(r.nextInt(256), r.nextInt(256), r.nextInt(256));
	        Sphere sph = new Sphere(
	        		r.nextInt(200)-100, 
	        		r.nextInt(200)-100, 
	        		r.nextInt(200)-100, 
	        		r.nextInt(100)+10, col, 1.5);
	        s.objects.add(sph);
        }
        
        Log.i("s.objects", "" + s.objects.size());
        
		bitmap = Bitmap.createBitmap(320, 480, Config.ARGB_8888);  
        Canvas canvas = new Canvas(bitmap);  

        Paint p = new Paint();   
        p.setAntiAlias(true);  
        p.setStyle(Paint.Style.FILL);  
        p.setStrokeWidth(1);  
        
        for (int j = 64; j < 420; j++)
        {
        	y = j;
            for (int i = 0; i < 320; i++)
            {
                Color c = s.Render(i - 320 / 2, j - 480 / 2);
                int icol = 0xff000000 + (c.R << 16) + (c.G << 8) + c.B; 
                p.setColor(icol);

                if(steps == 0)
                	canvas.drawPoint(i, j, p);
                else
                	canvas.drawRect(i, j, i+steps+1, j+steps+1, p);
        		
        		i+=steps;
            }
            j+=steps;
        }

		long time = System.currentTimeMillis() - start;
        Log.i("Render time", time + " ms");

        p.setColor(0xff000000);
        canvas.drawText("Render time: " + time + " ms", 5, 380, p);
        p.setColor(0xffffffff);
        canvas.drawText("Render time: " + time + " ms", 4, 379, p);
	}
	
	@Override
	public void onClick(View arg0) {
		image = (ImageView) findViewById(R.id.imageView1);
		/*
		int i = 0;
	    Runnable r = Runnable(){
	        public void run(){
	            image.setImageBitmap(bitmap);
	            image.postDelayed(r, 100); //set to go off again in 3 seconds.
	         }
	    };
	    image.postDelated(r,100); // set first time for 3 seconds
		*/
		
		if(!isRendering)
		{
			isRendering = true;
			Thread.currentThread().setName("Main Thread");
		    Log.v("RayTracer", "Rendering has run");
		    genThread = new Thread(new RenderScene());
		    genThread.start();
		}
		else
		{
		    Log.v("RayTracer", "Rendering has stopped");
			isRendering = false;
		}
		
		while(isRendering)
		{
		    //Log.v("RayTracer", "image.setImageBitmap(bitmap);");
	        image.setImageBitmap(bitmap);
	        try {
	        	Log.i("Y", "y=" + y);
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	
	public class RenderScene implements Runnable{
		String TAG = "RayTracer";

		    public RenderScene()
		    {
		    //  mainThreadHandler = h;
		    }
		    @Override
		    public void run() {
		    	
					Render();
	/*
			    	for(int i=0; isRendering && i < 10; i++){
			            Log.v(TAG, new Integer(i).toString());
			            try {
			                Thread.sleep(1000);
			            } catch (InterruptedException e) {
			                e.printStackTrace();
			            }
			        }*/
		        isRendering = false;
		    }
		}
}
