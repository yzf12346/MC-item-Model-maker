package priv.yzf.itemmaker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Paths;

public class FixedModelSaver {

	public static boolean saveModel(String bitmapPath, String outputpath, ModelSaver.Options opts) {
		File bitmapFile = new File(bitmapPath);
		if (!bitmapFile.exists()) {
			return false;
		}
		if (bitmapFile.isFile()) {
			save(bitmapFile.toString(), outputpath, opts);
		}
		if (bitmapFile.isDirectory()) {
			for (File child : bitmapFile.listFiles()) {
				if (child.toString().endsWith("_out.png")) {
					continue;
				}
				if (!child.toString().endsWith(".png")) {
					continue;
				}
				Log.i("MODEL", child.toString());

				save(child.toString(), outputpath, opts);
			}
		}
		return true;
	}
	public static void saveModelSync(final String inputpath, final String outputpath, final ModelSaver.Options opts, final Runnable onFinish, final Runnable onError) {
		new Thread(new Runnable(){

				@Override
				public void run() {
					boolean isfinish = saveModel(inputpath, outputpath, opts);
					if (isfinish) {
					    onFinish.run();
					} else {
						onError.run();
					}
				}

			}).start();
	}

	// 面剔除保存
	public static void save(String bitmapPath, String outputPath, ModelSaver.Options opts) {
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

		String bitmapName = new File(bitmapPath).getName().replace(".png", "");

		try {
			PrintStream stream =new PrintStream(Paths.get(outputPath, bitmapName + "_out.obj").toString());

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

					if (bitmap.getPixel(ix, iy) == Color.TRANSPARENT) {
						continue;
					}

					double Ux = (double)ix * UVunitX;
					double Uy = (double)iy * UVunitY;
					double Vx = Ux + UVunitX;
					double Vy = Uy + UVunitY;

					double paddingx = UVunitX * 0.05;
					double paddingy = UVunitY * 0.05;

					Uy = 1 - Uy;
					Vy = 1 - Vy;

					Ux += paddingx;
					Uy -= paddingy;
					Vx -= paddingx;
					Vy += paddingy;
					{
						// top
						float[][] arrss =  faces[0];
						for (float[] values : arrss) {
							double vx = x + values[0] * modelUnit;
							double vy = y + values[1] * modelUnit;
							double vz = z + values[2] * modelUnit;
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
				        vCount += 4;
					}
					{
						// down
						float[][] arrss =  faces[1];
						for (float[] values : arrss) {
							double vx = x + values[0] * modelUnit;
							double vy = y + values[1] * modelUnit;
							double vz = z + values[2] * modelUnit;
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
				        vCount += 4;
					}
					
					if (isTransparent(bitmap, ix - 1, iy)) {
						// x-
						float[][] arrss =  faces[3];
						for (float[] values : arrss) {
							double vx = x + values[0] * modelUnit;
							double vy = y + values[1] * modelUnit;
							double vz = z + values[2] * modelUnit;
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
				        vCount += 4;
					}
					if (isTransparent(bitmap, ix + 1, iy)) {
						// x+
						float[][] arrss =  faces[2];
						for (float[] values : arrss) {
							double vx = x + values[0] * modelUnit;
							double vy = y + values[1] * modelUnit;
							double vz = z + values[2] * modelUnit;
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
				        vCount += 4;
					}
					if (isTransparent(bitmap, ix , iy-1)) {
						// y-
						float[][] arrss =  faces[5];
						for (float[] values : arrss) {
							double vx = x + values[0] * modelUnit;
							double vy = y + values[1] * modelUnit;
							double vz = z + values[2] * modelUnit;
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
				        vCount += 4;
					}
					if (isTransparent(bitmap, ix, iy+1)) {
						// y+
						float[][] arrss =  faces[4];
						for (float[] values : arrss) {
							double vx = x + values[0] * modelUnit;
							double vy = y + values[1] * modelUnit;
							double vz = z + values[2] * modelUnit;
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
				        vCount += 4;
					}
					

				}
			}
            Bitmap scaledbitmap = ModelSaver.getScaledBitmap(bitmap);
			ModelSaver.saveBitmap(scaledbitmap, Paths.get(outputPath, bitmapName + "_out.png").toString());
			stream.close();
		} catch (Exception e) {e.printStackTrace();}
	}
	public static boolean isTransparent(Bitmap bmap, int x, int y) {
		if (x >= bmap.getWidth() || x < 0) {
			return true;
		}
		if (y >= bmap.getHeight() || y < 0) {
			return true;
		}
		if (bmap.getPixel(x, y) == Color.TRANSPARENT) {
			return true;
		}
		return false;
	}
}
