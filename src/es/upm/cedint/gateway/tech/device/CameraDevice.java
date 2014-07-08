package es.upm.cedint.gateway.tech.device;

import java.util.HashSet;

import com.github.sarxos.webcam.Webcam;

import es.upm.cedint.gateway.api.model.tech.Device;
import es.upm.cedint.gateway.api.model.tech.Parameter;
import es.upm.cedint.gateway.api.model.tech.ParameterAccessType;
import es.upm.cedint.gateway.api.model.tech.ParameterDataType;
import es.upm.cedint.gateway.api.model.tech.ParameterReadOnly;
import es.upm.cedint.gateway.api.model.tech.ParameterType;

public class CameraDevice extends Device {

	private Webcam camera;
	private Parameter videoAddress;
	private boolean active;
	
	public CameraDevice(Webcam camera){
		this.camera = camera;
		this.videoAddress = new ParameterReadOnly();
		HashSet<ParameterAccessType> accessType = new HashSet<ParameterAccessType>();
		accessType.add(ParameterAccessType.Request);
		this.videoAddress.setParameterAccessTypeSet(accessType);
		this.videoAddress.setParameterDataType(ParameterDataType.String);
		ParameterType pType = new ParameterType();
		pType.setName("videoAddress");
		this.videoAddress.setParameterType(pType);
		
		this.active = false;
	}
	
	public CameraDevice (){
		this.videoAddress = new ParameterReadOnly();
		HashSet<ParameterAccessType> accessType = new HashSet<ParameterAccessType>();
		accessType.add(ParameterAccessType.Request);
		this.videoAddress.setParameterAccessTypeSet(accessType);
		this.videoAddress.setParameterDataType(ParameterDataType.String);
		ParameterType pType = new ParameterType();
		pType.setName("videoAddress");
		this.videoAddress.setParameterType(pType);
		this.active = false;
	}

	/**
	 * @return the camera
	 */
	public Webcam getCamera() {
		return camera;
	}

	/**
	 * @param camera the camera to set
	 */
	public void setCamera(Webcam camera) {
		this.camera = camera;
	}

	/**
	 * @return the address
	 */
	public Parameter getVideoAddress() {
		return this.videoAddress;
	}

	/**
	 * @param address the address to set
	 */
	public void setVideoAddress(Parameter videoAddress) {
		this.videoAddress = videoAddress;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
}
