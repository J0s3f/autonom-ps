package uibk.autonom.ps.selflocalisation;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.TwiMaster;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * This is the thread on which all the IOIO activity happens. It will be
 * run every time the application is resumed and aborted when it is
 * paused. The method setup() will be called right after a connection
 * with the IOIO has been established (which might happen several
 * times!). Then, loop() will be called repetitively until the IOIO gets
 * disconnected.
 */
public class Looper extends BaseIOIOLooper implements OnSeekBarChangeListener
{

	/**
	 * Called every time a connection with IOIO has been
	 * established. Typically used to open pins.
	 * 
	 * @throws ConnectionLostException
	 *                 When IOIO connection is lost.
	 * 
	 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#setup()
	 */
	@Override
	protected void setup() throws ConnectionLostException
	{

	}

	/**
	 * Called repetitively while the IOIO is connected.
	 * 
	 * @throws ConnectionLostException
	 *                 When IOIO connection is lost.
	 * 
	 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#loop()
	 */
	@Override
	public void loop() throws ConnectionLostException
	{
		
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2)
	{
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0)
	{
		// TODO Auto-generated method stub
		
	}
}