package uibk.autonom.ps.selflocalisation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.TwiMaster;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import uibk.autonom.ps.selflocalisation.R;
import uibk.autonom.ps.selflocalisation.colordetector.ColorDetector;
import uibk.autonom.ps.selflocalisation.colordetector.ColorSelector;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends IOIOActivity implements OnTouchListener, CvCameraViewListener2 {
	private static final String DEBUG_TAG = "PS CBT:";
	
	private Mat currentRgba;
	private boolean isColorSelected = false;
	private CameraBridgeViewBase mOpenCvCameraView;
	private ColorDetector colorDetector;
	private ColorSelector colorSelector;
	
	private DigitalOutput led_;
	private PwmOutput servo_;
	private DigitalInput lint_;
	private TwiMaster twi;
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		 @Override
         public void onManagerConnected(int status) {
             switch (status) {
                 case LoaderCallbackInterface.SUCCESS:
                 {
                     Log.i(DEBUG_TAG, "OpenCV loaded successfully");
                     mOpenCvCameraView.enableView();
                     mOpenCvCameraView.setOnTouchListener(MainActivity.this);
                 } break;
                 default:
                 {
                     super.onManagerConnected(status);
                 } break;
             }
         }
    };
	
    @Override
    public void onResume(){
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_view);
		
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.java_camera_view);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        
        colorDetector = new ColorDetector();
	}
	
	 @Override
	 public void onPause(){
	     super.onPause();
	     if (mOpenCvCameraView != null){
	         mOpenCvCameraView.disableView();
	     }
	 }

	 public void onDestroy(){
	     super.onDestroy();
	     if (mOpenCvCameraView != null){
	         mOpenCvCameraView.disableView();
	     }
	 }

	@Override
	public void onCameraViewStarted(int width, int height){
		currentRgba = new Mat(height, width, CvType.CV_8UC4);
		
		int xOffset = (mOpenCvCameraView.getWidth() - width) / 2;
        int yOffset = (mOpenCvCameraView.getHeight() - height) / 2;
        
		colorSelector = new ColorSelector(width, height, xOffset, yOffset);
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		currentRgba = inputFrame.rgba();

		if (isColorSelected) {
			currentRgba = colorDetector.detect(currentRgba);
			
			Point center = colorDetector.getCenterPoint();
			Log.i(DEBUG_TAG, "center Point:" + center);

			//Imgproc.cvtColor(currentRgba, currentRgba, Imgproc.COLOR_RGB2HSV);
			
			Core.rectangle(currentRgba, 
					new Point(center.x - 25, center.y - 25), 
					new Point(center.x + 25, center.y + 25), 
					new Scalar(0, 0, 0, 0));
		}
		
		return currentRgba;
	}
	
	public Mat onCameraFrame_contours(CvCameraViewFrame inputFrame) {
		currentRgba = inputFrame.rgba();
		Mat orgImage = inputFrame.rgba().clone();
		
		if (isColorSelected) {
			currentRgba = colorDetector.detect(currentRgba);
			
			Scalar CONTOUR_COLOR = new Scalar(255, 0, 0, 255);
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			contours.add(colorDetector.getMaxContour());
            Imgproc.drawContours(orgImage, contours, -1, CONTOUR_COLOR);
			
		}
		
		return orgImage;
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		Scalar newColor = null;
		
		newColor = colorSelector.Select(currentRgba, (int)event.getX(), (int)event.getY());
		if(newColor != null){
			colorDetector.setHsvColor(newColor);
			isColorSelected = true;			
		}
		
		return false;
    }

	/**
	 * A method to create our IOIO thread.
	 * 
	 * @see ioio.lib.util.AbstractIOIOActivity#createIOIOThread()
	 */
	@Override
	protected IOIOLooper createIOIOLooper()
	{
		return new Looper();
	}

}
