package priv.yzf.itemmaker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.graphics.Color;


public class MainActivity extends Activity { 
    public boolean saveAble = true;
    String inputPath;
	String outputPath;

	EditText input;
	EditText output;
	EditText outputsize;
    Button exportButton;
	Switch usenewSys;
	ModelSaver.Options options;

	MainActivity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		activity = this;

		inputPath = "/storage/emulated/0/input/cookie.png";
		outputPath = "/sdcard/export/";
		options = new ModelSaver.Options();
        exportButton = findViewById(R.id.export_button);
		input = findViewById(R.id.input_path);
		output = findViewById(R.id.output_path);
		outputsize = findViewById(R.id.pixel_per_cm);
		usenewSys = findViewById(R.id.new_system);
       // FixedModelSaver.saveModelSync(inputPath, outputPath, options, new OnFinish(), new OnERROR());
    }

	public void export(View view) {
		runOnUiThread(new Runnable(){

				@Override
				public void run() {
					if (saveAble == false){
						return;
					}
                    saveAble = false;
					exportButton.setBackgroundColor(Color.parseColor("#666666"));
					exportButton.setText("导出中...");
					
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
						//Toast.makeText(activity, "输出路径 不能为空", Toast.LENGTH_LONG).show();
						outputPath = "/sdcard/export/";
					}
					if (sizeStr.equals("")) {
						sizeStr = "16.0";
					}

					float size = Float.parseFloat(sizeStr);
					options.pixelsPerCM = size;
					if(usenewSys.isChecked()){
					    FixedModelSaver.saveModelSync(inputPath, outputPath, options, new OnFinish(), new OnERROR());
					}else{
						ModelSaver.defaultSaveModelSync(inputPath,outputPath,options,new OnFinish(),new OnERROR());
					}
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
						saveAble = true;
						exportButton.setBackgroundColor(Color.parseColor("#6644ff"));
						exportButton.setText("导出");
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
						saveAble = true;
						exportButton.setBackgroundColor(Color.parseColor("#6644ff"));
						exportButton.setText("导出");
					}
				});
		}
	}
	public void jumptoGithub(View view){
		Intent intent= new Intent();        
		intent.setAction("android.intent.action.VIEW");    
		Uri content_url = Uri.parse("https://github.com/yzf12346/MC-item-Model-maker");   
		intent.setData(content_url);  
		startActivity(intent);
	}
} 
