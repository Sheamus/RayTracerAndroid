package com.serg.raytracer;

import java.util.Random;

import android.os.Bundle;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	    Button btnOK = (Button) findViewById(R.id.button1);
	    btnOK.setOnClickListener(this);

	    Board board = new Board();

	    ImageView image = (ImageView) findViewById(R.id.imageView1);
        image.setImageBitmap(board.Init());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		long start = System.currentTimeMillis();

		ImageView image = (ImageView) findViewById(R.id.imageView1);
        
        Scene s = new Scene();
        s.SetCamera(new Vector(-110.1f, -110.1f, -110.1f), 180f, 45f);
        s.MaxReflection = 2;
        s.MaxRefraction = 2;
        s.FOCUS = 1000;

        Sphere sph = new Sphere(0, 0, 0, 40, Color.FromArgb((byte)255, (byte)222, (byte)200), 1.5);
        s.objects.add(sph);
        Sphere sph2 = new Sphere(-30, 50, -80, 30, Color.FromArgb((byte)22, (byte)150, (byte)250), 1.5);
        s.objects.add(sph2);
        
		Bitmap bitmap = Bitmap.createBitmap(320, 480, Config.ARGB_8888);  
        
        Canvas canvas = new Canvas(bitmap);  
        Paint p = new Paint();   
        p.setAntiAlias(true);  
        p.setStyle(Paint.Style.FILL);  
        p.setStrokeWidth(1);  
        
        for (int j = 50; j < 400; j++)
        {
            for (int i = 0; i < 320; i++)
            {
                Color c = s.Render(i - 320 / 2, j - 480 / 2);
                int icol = (0xff << 24) + (c.R << 16) + (c.G << 8) + c.B; 
                p.setColor(icol);
        		canvas.drawPoint(i, j, p);
        		//i+=1;
            }
            //j+=1;
        }

        image.setImageBitmap(bitmap);

		long time = System.currentTimeMillis() - start;
        Log.i("Render time", time + " ms");

        p.setColor(0xff000000);
        canvas.drawText("Render time: " + time + " ms", 5, 380, p);
        p.setColor(0xffffffff);
        canvas.drawText("Render time: " + time + " ms", 4, 379, p);
        
        image.setImageBitmap(bitmap);
}

}
