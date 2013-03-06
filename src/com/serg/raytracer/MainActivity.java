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

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.Dialog;
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
	private Handler mHandler = new Handler();
	private boolean isRendering; 

	static int steps = 5;
	static int y = 0;
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
	    isRendering = false;
	    
	    image = (ImageView) findViewById(R.id.imageView1);
        image.setImageBitmap(board.Init());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public static void Render()
	{
		Log.i("Render", ">>");
		long start = System.currentTimeMillis();

        Scene scene = new Scene();
        scene.light = new Vector(0.0f, -199f, 0.0f);

		bitmap = Bitmap.createBitmap(320, 480, Config.ARGB_8888);  
        Canvas canvas = new Canvas(bitmap);  

        scene.SetCamera(new Vector(-110.1f, -110.1f, -110.1f), 180f, 0f);
        scene.MaxReflection = 5;
        scene.MaxRefraction = 5;
        scene.FOCUS = 500;
        scene.Shadows = false;
        
        Random r = new Random();
        /*
        scene.BeginCSG("CornellBox");
        scene.AddObject(new Plane(new Vector(0, 0, 100), new Vector(0, -1, 0), Color.Yellow(), 	0.0, 0.0, 1.1));//задн€€ стенка
        scene.AddObject(new Plane(new Vector(0, 200, 0), new Vector(0, 0, 1), Color.Red(), 		0.0, 0.0, 1.1));//пол
        scene.AddObject(new Plane(new Vector(0, -200, 0), new Vector(0, 0, -1), Color.White(), 	0.0, 0.0, 1.1));//потолок
        scene.AddObject(new Plane(new Vector(200, 0, 0), new Vector(-1, 0, 0), Color.Green(), 	0.0, 0.0, 1.1));//лева€ стенка
        scene.AddObject(new Plane(new Vector(-200, 0, 0), new Vector(1, 0, 0), Color.Blue(), 		0.0, 0.0, 1.1));//права€ стенка
        scene.EndCSG();
        */
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
	        	
		        Color col = new Color().FromArgb(r.nextInt(256), r.nextInt(256), r.nextInt(256));
		        Sphere sph = new Sphere(
		        		x, y, z, 
		        		r.nextInt(90)+10, col, 0.1, 0.5, 1.5);
		        scene.AddObject(sph);
	        }
	        scene.EndCSG();
        }
        else{
        	steps = 5;
        	
            scene.BeginCSG("TestCSG1");
	        scene.AddObject(new Sphere( 50f, -5f, 20f, 70f, Color.Red(), 		0.0f, 0.0f, 1.1f));
	        scene.AddObject(new Sphere(-50f, 10f, -10f, 70f, Color.Green(), 	0.0f, 0.0f, 1.1f));
	        scene.EndCSG();
        }

        Paint p = new Paint();   
        p.setAntiAlias(false);  
        p.setStyle(Paint.Style.FILL_AND_STROKE);  
        p.setStrokeWidth(1);  
        
		time = System.currentTimeMillis() - start;

		for (int j = 64; j < 420; j++)
        {
        	y = j;
    		time = System.currentTimeMillis() - start;
    		donePercents = 100*(j-64)/(420-64);
    		if(donePercents>0)
    			estimated = 100*time/donePercents;
    		
            for (int i = 0; i < 320; i++)
            {
                Color c = scene.Render(i - 320 / 2, j - 480 / 2);
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

		time = System.currentTimeMillis() - start;
        Log.i("Render time", time + " ms");

        p.setColor(0xff000000);
        canvas.drawText("Render time: " + time + " ms", 5, 380, p);
        p.setColor(0xffffffff);
        canvas.drawText("Render time: " + time + " ms", 4, 379, p);
	}
	
	@Override
    protected Dialog onCreateDialog(int dialogId){
        ProgressDialog progress = null;
        switch (dialogId) {
        case PROGRESS_DLG_ID:
            progress = new ProgressDialog(this);
                progress.setMessage("Rendering...");
            break;
        }
        return progress;
    }
    
    public void runButtonHandler(View button){
        if(button.getId() == R.id.button1)
        {
            new RenderSceneTask().execute("");
        }
    }
    
    class RenderSceneTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            publishProgress(new Void[]{});
           
            Render();

            return bitmap;
        }
        
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Log.i("onProgressUpdate", "updating...");
            showDialog(PROGRESS_DLG_ID);
        }
        
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            Log.i("onPostExecute", "update image");
            dismissDialog(PROGRESS_DLG_ID);
            image.setImageBitmap(result);	
        }
    }
}
