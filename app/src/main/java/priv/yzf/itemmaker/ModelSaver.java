package priv.yzf.itemmaker;
import android.graphics.Bitmap;
import java.io.PrintStream;
import static java.lang.String.*;
import android.graphics.Color;
import java.io.File;
import android.graphics.Canvas;
import android.graphics.Paint;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import android.graphics.BitmapFactory;
import java.util.StringTokenizer;
import android.util.Log;
public class ModelSaver {
	public static boolean saveModel(String bitmapPath,String outputpath,Options opts){
		File bitmapFile = new File(bitmapPath);
		if (!bitmapFile.exists()){
			return false;
		}
		if (bitmapFile.isFile()){
			save(bitmapFile.toString(),outputpath,opts);
		}
		if (bitmapFile.isDirectory()){
			for (File child : bitmapFile.listFiles()){
				if (child.toString().endsWith("_out.png")){
					continue;
				}
				if (!child.toString().endsWith(".png")){
					continue;
				}
				Log.i("MODEL",child.toString());
		
				save(child.toString(),outputpath,opts);
			}
		}
		return true;
	}
	public static void saveModelSync(final String inputpath,final String outputpath,final Options opts,final Runnable onFinish,final Runnable onError){
		new Thread(new Runnable(){

				@Override
				public void run() {
					boolean isfinish = saveModel(inputpath,outputpath,opts);
					if (isfinish){
					    onFinish.run();
					}else{
						onError.run();
					}
				}
				
		}).start();
	}
    public static void save(String bitmapPath,String outputPath,Options opts){
		
		float[][][] faces ={
			{// top
				{0,1,0},
				{0,1,1},
				{1,1,1},
				{1,1,0}
			},
			{// down
                {0,0,0},
				{1,0,0},
				{1,0,1},
			    {0,0,1}
			},
			{// left
				{1,0,0},
				{1,1,0},
				{1,1,1},
				{1,0,1}
			},
			{// right
                {0,0,0},
				{0,0,1},
				{0,1,1},
				{0,1,0}
			},
			{//front
                {0,0,1},
				{1,0,1},
				{1,1,1},
				{0,1,1}
			},
			{// back
                {0,0,0},
				{0,1,0},
				{1,1,0},
				{1,0,0}
			}
		};
		
		Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath);
		
		String bitmapName = new File(bitmapPath).getName().replace(".png","");
		
		try {
			PrintStream stream =new PrintStream(Paths.get(outputPath,bitmapName+"_out.obj").toString());

			int imgW = bitmap.getWidth();
			int imgH = bitmap.getHeight();

			float pixelPerCM = opts.pixelsPerCM; 
			float modelUnit = 1 / pixelPerCM;

			float UVunitX = 1f / imgW;
			float UVunitY = 1f / imgH;
			int vCount = 1;
			for (int ix = 0;ix < imgW;ix++) {
				for (int iy = 0;iy < imgW;iy++) {
                    float x = modelUnit * ix;
					float y = 0;
					float z = modelUnit * iy;

					if (bitmap.getPixel(ix,iy) == Color.TRANSPARENT){
						continue;
					}
					
					double Ux = (double)ix*UVunitX;
					double Uy = (double)iy*UVunitY;
					double Vx = Ux + UVunitX;
					double Vy = Uy + UVunitY;

					double paddingx = UVunitX*0.05;
					double paddingy = UVunitY*0.05;
					
					Uy =1-Uy;
					Vy = 1-Vy;
					
					Ux += paddingx;
					Uy -= paddingy;
					Vx -= paddingx;
					Vy += paddingy;
					
					for (float[][] arrss: faces) {
						int addCount = arrss.length;
						for (float[] values : arrss) {
							double vx = x + values[0]*modelUnit;
							double vy = y + values[1]*modelUnit;
							double vz = z + values[2]*modelUnit;
							stream.printf("v %f %f %f\n", vx, vy, vz);
							stream.printf("vn 0 1 0\n");
						}
						stream.printf("vt %f %f\n", Ux, Uy);
						stream.printf("vt %f %f\n", Vx, Uy);
						stream.printf("vt %f %f\n", Vx, Vy);
						stream.printf("vt %f %f\n", Ux, Vy);

						stream.printf("f %d/%d/%d %d/%d/%d %d/%d/%d %d/%d/%d \n",
									  vCount, vCount, vCount,
									  vCount + 1, vCount + 1, vCount + 1,
									  vCount + 2, vCount + 2, vCount + 2,
									  vCount + 3, vCount + 3, vCount + 3);
				        
					    vCount += addCount;
					}
				}
			}
            Bitmap scaledbitmap = getScaledBitmap(bitmap);
			saveBitmap(scaledbitmap,Paths.get(outputPath,bitmapName+"_out.png").toString());
			stream.close();
		} catch (Exception e) {e.printStackTrace();}
		
	}
	public static void saveBitmap(Bitmap map,String path){
		try{
			FileOutputStream output = new FileOutputStream(path);
			map.compress(Bitmap.CompressFormat.PNG,80,output);
		}catch(Exception e){e.printStackTrace();}
	}
	public static Bitmap getScaledBitmap(Bitmap bitmap){
		int GOBAL_SCALE = 1024;
		float unitX = GOBAL_SCALE/bitmap.getWidth();
		float unitY = GOBAL_SCALE/bitmap.getHeight();
		Bitmap scaledBitmap = Bitmap.createBitmap(GOBAL_SCALE,GOBAL_SCALE,Bitmap.Config.ARGB_8888);
		Canvas scaledCanvas = new Canvas(scaledBitmap);
		for (int x = 0;x<bitmap.getWidth();x++){
			for (int y = 0;y<bitmap.getHeight();y++){
                int color = bitmap.getPixel(x,y);
				Paint paint = new Paint();
				paint.setColor(color);
				scaledCanvas.drawRect(unitX*x,unitY*y,unitX*x+unitX,unitY*y+unitY,paint);
			}
		}
		
		return scaledBitmap;
	}
	public static class Options {
		public float pixelsPerCM = 16;// 多少像素为1cm
	}
}
