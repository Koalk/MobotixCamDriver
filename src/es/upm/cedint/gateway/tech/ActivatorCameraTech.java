package es.upm.cedint.gateway.tech;

import java.awt.Dimension;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.ipcam.IpCamAuth;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;

import es.upm.cedint.gateway.api.dao.DeviceDAO;
import es.upm.cedint.gateway.api.dao.MeasureDAO;
import es.upm.cedint.gateway.api.dao.MessageDAO;
import es.upm.cedint.gateway.api.dao.ParameterDAO;
import es.upm.cedint.gateway.api.dao.ParameterTypeDAO;
import es.upm.cedint.gateway.api.manager.TechManager;
import es.upm.cedint.gateway.api.model.tech.Device;
import es.upm.cedint.gateway.api.model.tech.Parameter;
import es.upm.cedint.gateway.api.model.tech.ParameterAccessType;
import es.upm.cedint.gateway.api.model.tech.ParameterReadOnly;
import es.upm.cedint.gateway.tech.device.MobotixCamIp;

public class ActivatorCameraTech implements BundleActivator{

	private Logger logger = LoggerFactory.getLogger(ActivatorCameraTech.class);

	public TechManager techManager;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void start(BundleContext context) throws Exception {
		String method = "CameraDriver void start(BundleContext context)";
		logger.debug(method + " Init" );

		// Buscamos la implementación de la interfaz de tecnología para añadirla en el gateway.
		ServiceReference techServicerRefence = context.getServiceReference(TechManager.class.getName());
		ServiceReference deviceDAOServiceReference = context.getServiceReference(DeviceDAO.class.getName());
		DeviceDAO deviceDAO = (DeviceDAO) context.getService(deviceDAOServiceReference);

		ServiceReference measureDAOServiceReference = context.getServiceReference(MeasureDAO.class.getName());
		MeasureDAO measureDAO = (MeasureDAO) context.getService(measureDAOServiceReference);

		ServiceReference messageDAOServiceReference = context.getServiceReference(MessageDAO.class.getName());
		MessageDAO messageDAO = (MessageDAO) context.getService(messageDAOServiceReference);

		ServiceReference parameterDAOServiceReference = context.getServiceReference(ParameterDAO.class.getName());
		ParameterDAO parameterDAO = (ParameterDAO) context.getService(parameterDAOServiceReference);

		ServiceReference parameterTypeDAOServiceReference = context.getServiceReference(ParameterTypeDAO.class.getName());
		ParameterTypeDAO parameterTypeDAO = (ParameterTypeDAO) context.getService(parameterTypeDAOServiceReference);

		techManager = (TechManager) context.getService(techServicerRefence);
		
		Webcam.setDriver(new IpCamDriver());
		
		MobotixCamIp ipCam = new MobotixCamIp("Showroom Camera", "192.168.0.187");
		ipCam.setAuth(new IpCamAuth("user", "cedinT12"));
		ipCam.setResolution(new Dimension(640,480));
		IpCamDeviceRegistry.register(ipCam);

		Device showRoomCam = new Device();
		Parameter videoURL = new ParameterReadOnly();
		videoURL.addParameterAccessType(ParameterAccessType.Request);
		videoURL.setName("Video URL");
		videoURL.setValue(ipCam.getURL().toString());
		showRoomCam.addParameter(videoURL); 

		logger.debug(method + " Arrancado Driver" );
	}

	public void stop(BundleContext arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	//	public static void main(String[] args){
	//		
	//		
	//		Webcam.setDriver(new IpCamDriver());
	//
	//		System.out.println("Configuring Cam");
	//		IpCamDevice ipCam = new MobotixCamIp("Showroom Mobotix", "192.168.0.187");
	//		ipCam.setAuth(new IpCamAuth("user", "cedinT12"));
	//		System.out.println("Cam configured with url: "+ipCam.getURL());
	//		ipCam.setResolution(new Dimension(640,480));
	//		
	//		System.out.println("Adding cam to driver");
	//		IpCamDriver driver = new IpCamDriver();
	////		driver.register(ipCam);
	//		Webcam.setDriver(driver);
	//		IpCamDeviceRegistry.register(ipCam);
	//		
	//		System.out.println("Creating panel");
	//		WebcamPanel panel = new WebcamPanel(Webcam.getDefault());
	//		panel.setFPS(30);
	//		
	//		System.out.println("Launching JFrame");
	//		JFrame f = new JFrame("Testing Mobotix Ip Driver");
	//		f.add(panel);
	////		panel.start();
	//		f.pack();
	//		f.setVisible(true);
	//		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//		
	//		int threshold = WebcamMotionDetector.DEFAULT_THREASHOLD*2;
	//		
	//		WebcamMotionDetector detector = new WebcamMotionDetector(Webcam.getDefault(),threshold, 1000);
	//		detector.setInterval(600);
	//		detector.start();
	//		
	//		while(true){
	//			if(detector.isMotion())
	//				System.out.println("****** MOTION DETECTED!! ******");
	//			try {
	//				Thread.sleep(2000);
	//			} catch (InterruptedException e) {
	//	e.printStackTrace();
	//			}
	//		}
	//		
	//	}
}
