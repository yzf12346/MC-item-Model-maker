package priv.yzf.itemmaker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ActionMode.Callback;
import java.util.concurrent.RunnableFuture;
import android.app.AlertDialog;
import android.os.Message;
import android.view.View;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;
import android.view.View.OnClickListener;


public class MainActivity extends Activity { 

    String inputPath;
	String outputPath;

	EditText input;
	EditText output;
	EditText outputsize;
    Button exportButton;
	ModelSaver.Options options;

	MainActivity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		activity = this;

		inputPath = "";
		outputPath = "";
		options = new ModelSaver.Options();

		input = findViewById(R.id.input_path);
		output = findViewById(R.id.output_path);
		outputsize = findViewById(R.id.pixel_per_cm);

    }

	public void export(View view) {
		runOnUiThread(new Runnable(){

				@Override
				public void run() {

                    //Toast.makeText(activity,"tost",Toast.LENGTH_LONG).show();
					inputPath = input.getText().toString();
					outputPath = output.getText().toString();
					String sizeStr = outputsize.getText().toString();
                    if (inputPath.equals("")&&outputPath.equals("")){
						Toast.makeText(activity,"输入路径 和 输出路径 不能为空",Toast.LENGTH_LONG).show();
						return;
					}
					if (inputPath.equals("")) {
						Toast.makeText(activity, "输入路径 不能为空", Toast.LENGTH_LONG).show();
						return;
					}
					if (outputPath.equals("")) {
						Toast.makeText(activity, "输出路径 不能为空", Toast.LENGTH_LONG).show();
						return;
					}
					if (outputsize.equals("")) {
						sizeStr = "16.0";
					}

					float size = Float.parseFloat(sizeStr);
					options.pixelsPerCM = size;
					ModelSaver.saveModelSync(inputPath, outputPath, options, new OnFinish(), new OnERROR());
					
				}
			});
	}

	public class OnFinish implements Runnable {

		@Override
		public void run() {
			runOnUiThread(new Runnable(){
					@Override
					public void run() {
						Toast.makeText(activity, "导出完成\n路径：" + outputPath, Toast.LENGTH_SHORT).show();
					}
				});
		}
	}

	public class OnERROR implements Runnable {

		@Override
		public void run() {
			runOnUiThread(new Runnable(){
					@Override
					public void run() {
						Toast.makeText(activity, "错误:\n输入路径或输出路径为空", Toast.LENGTH_SHORT).show();
					}
				});
		}
	}
} 
