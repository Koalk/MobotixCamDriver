package es.upm.cedint.gateway.tech.device;

import java.net.MalformedURLException;
import java.net.URL;

import com.github.sarxos.webcam.WebcamException;
import com.github.sarxos.webcam.ds.ipcam.IpCamDevice;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;

public class MobotixCamIp extends IpCamDevice {

	private URL base;

	public MobotixCamIp(String name, String url) {
		this(name, toURL(url));
	}

	public MobotixCamIp(String name, URL base){
		super(name, null, IpCamMode.PULL);
		this.base = base;
	}

	public MobotixCamIp(String name, URL url, IpCamMode pull) {
		super(name,url,pull);
	}
	
	@Override
	public URL getURL() {
//		String url = String.format("%s%s",base,"cgi-bin/faststream.jpg?error=picture&preview&stream=full");
		String url = String.format("%s%s",base,"/record/current.jpg");
//		if (this.getResolutions()!=null)
//			url.concat(String.format("&size", this.getResolution().height,this.getResolution().width));
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new WebcamException(String.format("Incorrect URL %s", url), e);
		}
	}	
	
	public boolean motionDetector(){
		
		return true;
	}
}