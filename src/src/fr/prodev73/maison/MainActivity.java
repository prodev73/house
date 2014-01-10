package fr.prodev73.maison;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import fr.prodev73.maison.view.ImageViewSvg;

public class MainActivity extends Activity{

  private ImageViewSvg imageViewSvg;
  private Vibrator vibe = null;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    imageViewSvg = (ImageViewSvg) findViewById(R.id.imageView);
    imageViewSvg.drawSvg(R.raw.rdc);
    vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
  }

  @Override
	public void onBackPressed() {
		if(!imageViewSvg.isOriginal())
		{
			imageViewSvg.restore();
			Toast.makeText(this, "Click once more time to exit", Toast.LENGTH_LONG).show();
		}
		else
			super.onBackPressed();
	}
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.plan_rdc) {
      vibe.vibrate(100);
      imageViewSvg.drawSvg(R.raw.rdc);
      return true;
    }else if (id == R.id.plan_etage) {
      vibe.vibrate(100);
      imageViewSvg.drawSvg(R.raw.etage);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
