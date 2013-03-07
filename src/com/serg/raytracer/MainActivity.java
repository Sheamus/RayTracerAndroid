//TODO: —делать ProgressBar
//многопоточность
//AsyncTask
//эффекты, объекты
//если после проломлени€, отражени€ и т.п. €ркость луча меньше определЄнного порога, то дальше ход луча не продолжать
//Cornell Box
//формат .3ds
//настройки программы, рендеринга, сцены
//caustics

package com.serg.raytracer;

import java.util.Random;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

	static Bitmap bitmap;
    static int j;
    static Canvas canvas;
    long start;
    Scene scene;
    
	ProgressDialog pd;
	Handler h;
	  
	static int steps = 1;
	static double estimated = 0;
	static double donePercents = 0;
	static long time = 0;

	ImageView image;
    Button runButt;
    final int PROGRESS_DLG_ID = 777;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	    runButt = (Button) findViewById(R.id.button1);

	    Board board = new Board();
	    
	    image = (ImageView) findViewById(R.id.imageView1);
        image.setImageBitmap(board.Init());
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void InitScene()
	{
		scene = new Scene();
        scene.light = new Vector(0.0f, -199f, 0.0f);

        scene.SetCamera(new Vector(-110.1f, -110.1f, -110.1f), 180f, 0f);
        scene.MaxReflection = 3;
        scene.MaxRefraction = 2;
        scene.FOCUS = 500;
        scene.Shadows = false;
        
        Random r = new Random();
        
        scene.BeginCSG("CornellBox");
        scene.AddObject(new Plane(new Vector(0, 0, 100), new Vector(0, -1, 0), Color.Yellow(), 	0.1, 0.0, 1.1));//задн€€ стенка
        scene.AddObject(new Plane(new Vector(0, 200, 0), new Vector(0, 0, 1), Color.Red(), 		0.1, 0.0, 1.1));//пол
        scene.AddObject(new Plane(new Vector(0, -200, 0), new Vector(0, 0, -1), Color.White(), 	0.1, 0.0, 1.1));//потолок
        scene.AddObject(new Plane(new Vector(200, 0, 0), new Vector(-1, 0, 0), Color.Green(), 	0.1, 0.0, 1.1));//лева€ стенка
        scene.AddObject(new Plane(new Vector(-200, 0, 0), new Vector(1, 0, 0), Color.Blue(), 	0.1, 0.0, 1.1));//права€ стенка
        scene.EndCSG();
        scene.csgObjects.get(0).operations.add(new Operation("+", 0, 1));
        scene.csgObjects.get(0).operations.add(new Operation("+", 0, 2));
        scene.csgObjects.get(0).operations.add(new Operation("+", 0, 3));
        scene.csgObjects.get(0).operations.add(new Operation("+", 0, 4));
        
        boolean testCSG = true;
        
        if(!testCSG)
        {
            scene.BeginCSG("Balls");
	        for(int i=0;i<5;i++)
	        {
	        	int x = r.nextInt(300)-150;
	        	int y = r.nextInt(400)-200;
	        	int z = r.nextInt(200)-100;
	        	Log.i("sphere x,y,z", x+","+y+","+z);
	        	
		        Color col = Color.FromArgb(r.nextInt(256), r.nextInt(256), r.nextInt(256));
		        Sphere sph = new Sphere(
		        		x, y, z, 
		        		r.nextInt(90)+10, col, 0.1, 0.5, 1.5);
		        scene.AddObject(sph);
	        }
	        scene.EndCSG();
        }
        else{
            scene.BeginCSG("CSG_Difference");
	        scene.AddObject(new Sphere( 30f, -5f, 20f,  100f, Color.Red(), 		0.1f, 0.0f, 1.1f));
	        scene.AddObject(new Sphere(-30f, 10f, -10f, 100f, Color.Green(), 	0.1f, 0.0f, 1.1f));
	        scene.EndCSG();
	        scene.csgObjects.get(scene.csgObjects.size()-1).operations.add(new Operation("-", 6, 5));

            scene.BeginCSG("CSG_Union");
	        scene.AddObject(new Sphere( 90f, 30f, 90f, 100f, Color.Blue(), 		0.1f, 0.0f, 1.1f));
	        scene.AddObject(new Sphere( 50f, 50f, 50f, 100f, Color.Magenta(), 	0.1f, 0.0f, 1.1f));
	        scene.EndCSG();
	        scene.csgObjects.get(scene.csgObjects.size()-1).operations.add(new Operation("&", 7, 8));
        }
	}
	
	@SuppressLint("HandlerLeak")
	public void Render()
	{
		Log.i("Render", ">>");
		
		start = System.currentTimeMillis();
		
		InitScene();

        j = 64;
    	pd = new ProgressDialog(this);
        pd.setTitle("Rendering");
        pd.setMessage("Please wait...");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMax(480-60-64);
        pd.setIndeterminate(true);
        pd.show();

		bitmap = Bitmap.createBitmap(320, 480, Config.ARGB_8888);  
        canvas = new Canvas(bitmap);  

        final Paint p = new Paint();   
        p.setAntiAlias(false);  
        p.setStyle(Paint.Style.FILL_AND_STROKE);  
        p.setStrokeWidth(1);  
        
        h = new Handler() {
          public void handleMessage(Message msg) {
            pd.setIndeterminate(false);
            
    		time = System.currentTimeMillis() - start;

            if (pd.getProgress() < pd.getMax()) {
            	pd.incrementProgressBy(steps);
            	//pd.incrementSecondaryProgressBy(10);
            	h.sendEmptyMessageDelayed(0, 100);

        		time = System.currentTimeMillis() - start;
        		donePercents = 100*(j-64)/(420-64);
        		if(donePercents>0)
        			estimated = 100*time/donePercents;
                
        		long seconds = (long)((estimated - time)/1000);
        		long minutes = (long)(seconds / 60);
        		
        		pd.setMessage("Please wait... " + minutes + "m " + (seconds % 60) + "s");
        		
                for (int i = 0; i < 320; i++)
                {
                    Color c = scene.Render(i - 320 / 2, j - 480 / 2);
                    int icol = 0xff000000 + (c.R << 16) + (c.G << 8) + c.B; 
                    p.setColor(icol);

                    if(steps == 1)
                    	canvas.drawPoint(i, j, p);
                    else
                    	canvas.drawRect(i, j, i+steps+1, j+steps+1, p);
            		
            		i+=(steps-1);
                }
                j+=steps;
            } 
            else {
              pd.dismiss();
            }

    		time = System.currentTimeMillis() - start;

            image.setImageBitmap(bitmap);	
          }
        };
        h.sendEmptyMessageDelayed(0, 200);

        p.setColor(0xff000000);
        canvas.drawText("Render time: " + time + " ms", 5, 380, p);
        p.setColor(0xffffffff);
        canvas.drawText("Render time: " + time + " ms", 4, 379, p);
        
        image.setImageBitmap(bitmap);	
	}
	
	public void onclick(View v) {
	    switch (v.getId()) {
	    case R.id.button1:
	    	Render();
	    	break;
	    default:
	      break;
	    }
	}
}
