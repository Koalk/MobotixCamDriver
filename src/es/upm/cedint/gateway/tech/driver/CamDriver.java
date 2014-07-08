package es.upm.cedint.gateway.tech.driver;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.ipcam.IpCamAuth;
import com.github.sarxos.webcam.ds.ipcam.IpCamDevice;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;

import es.upm.cedint.gateway.api.manager.Driver;
import es.upm.cedint.gateway.api.manager.TechManager;
import es.upm.cedint.gateway.api.model.tech.ConfigProperty;
import es.upm.cedint.gateway.api.model.tech.ConfigType;
import es.upm.cedint.gateway.api.model.tech.Configuration;
import es.upm.cedint.gateway.api.model.tech.Device;
import es.upm.cedint.gateway.api.model.tech.DeviceType;
import es.upm.cedint.gateway.api.model.tech.Parameter;
import es.upm.cedint.gateway.api.model.tech.Technology;
import es.upm.cedint.gateway.api.model.user.Message;
import es.upm.cedint.gateway.api.model.user.MessageType;
import es.upm.cedint.gateway.tech.device.CameraDevice;

public class CamDriver implements Driver{

	private String idDriver;
	private String nameDriver;
	private String descriptionDriver;
	private List<Device> deviceList;
	private Map<DeviceType,Configuration> deviceConfigurationTypes;
	private Technology technology;
	private Configuration technologyConfiguration;
	private String versionDriver;
	final private boolean selfDiscoveryAvailable=false;
	final private boolean subscriptionAvailable=true;
	final protected String dbLocation="jdbc:mysql://127.0.0.1:3306/cameraTech";
	final protected String user="root";
	final protected String pass="13579..m";

	private TechManager techManager;

	private Logger logger;

	public CamDriver (){
		this.logger = LoggerFactory.getLogger(CamDriver.class);
		Webcam.setDriver(new IpCamDriver());

		if (!checkDB()){
			setUpDB();
		}
		else{
			this.deviceList = retrieveDBDevices();
			this.deviceConfigurationTypes = retrieveDBDevicesTypes();
		}
	}

	public CamDriver (String nameDriver, String descriptionDriver, Technology tech){
		this();
		this.nameDriver = nameDriver;
		this.descriptionDriver = descriptionDriver;
		this.technology = tech;
	}

	private Connection getConnection(){
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(dbLocation,user,pass);
		} catch (ClassNotFoundException e) {
			logger.error("Error setting up the database driver for CameraDriver: {}",e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}

	private void setUpDB() {
		Statement stmt = null;
		try {
			Connection conn = getConnection();
			stmt = conn.createStatement();
			String query = "CREATE TABLE device (" +
					"id VARCHAR(255) NOT NULL, " +
					"name VARCHAR(255)," +
					"ipAddr VARCHAR(255) NOT NULL," +
					"user VARCHAR(255)," +
					"pass VARCHAR(255)," +
					"type VARCHAR(255)," +
					"PRIMARY KEY (id)" +
//					"FOREIGN KEY (type) references dTId from deviceTypes" +
					");" +
					"CREATE TABLE deviceType (" +
					"dTId VARCHAR(255) NOT NULL," +
					"dTName VARCHAR(255)," +
					"dTDescription VARCHAR(255)," +
					"confId VARCHAR(255)," +
					"configType VARCHAR(255)," +
					"PRIMARY KEY (dTId)" +
					");" +
					"CREATE TABLE configProperty (" +
					"confPropertyId VARCHAR(255) NOT NULL," +
					"name VARCHAR(255)," +
					"type VARCHAR(255)," +
					"description VARCHAR(255)," +
					"validation VARCHAR(255)," +
					"value VARCHAR(255)," +
					"confId VARCHAR(255) NOT NULL," +
					"PRIMARY KEY (confPropertyId)," +
					"FOREIGN KEY (confId) REFERENCES confId FROM deviceType" +
					");";
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			logger.error("Error with JDBC for CameraDriver: {}",e);
		}
	}

	private boolean checkDB() {
		DatabaseMetaData metaData;
		boolean res = false;
		try {	
			Connection conn = getConnection();
			metaData = conn.getMetaData();
			ResultSet devicesTable = metaData.getTables(null, null, "device", null);
			ResultSet deviceTypesTable = metaData.getTables(null, null, "deviceType", null);
			if(devicesTable.first() && deviceTypesTable.first()){
				res = true;
			}
		} catch (SQLException e) {
			logger.error("Error with JDBC for CameraDriver: {}",e);
		}
		return res;
	}
	private Map<DeviceType, Configuration> retrieveDBDevicesTypes() {
		Map<DeviceType, Configuration> res = new HashMap<DeviceType, Configuration>();
		Statement stmt = null;
		try {
			Connection conn = getConnection();
			stmt = conn.createStatement();
			String query = "SELECT * FROM deviceType;";
			ResultSet deviceTypes = stmt.executeQuery(query);
			while (deviceTypes.next()){
				DeviceType auxDT = new DeviceType();
				Configuration conf = new Configuration();
				auxDT.setId(deviceTypes.getString("dTId"));
				auxDT.setName(deviceTypes.getString("dTName"));
				auxDT.setDescription(deviceTypes.getString("dTDescription"));
				conf.setId(deviceTypes.getString("confId"));
				List<ConfigProperty> props = new ArrayList<ConfigProperty>();
				Connection innerConn = getConnection();
				String propsQuery = "SELECT * FROM configProperty" +
						"WHERE confId = ?;";
				PreparedStatement pstmt = innerConn.prepareStatement(propsQuery);
				pstmt.setString( 1, conf.getId()); 
				ResultSet properties = pstmt.executeQuery();
				ConfigProperty confProp;
				while (properties.next()){
					 confProp = new ConfigProperty();
					 confProp.setId(properties.getString("confPropertyId"));
					 confProp.setName(properties.getString("name"));
					 confProp.setType(properties.getString("type"));
					 confProp.setDescription(properties.getString("description"));
					 confProp.setValidation(properties.getString("validation"));
					 confProp.setValue(properties.getString("value"));
				}
				conf.setProperties(props);
				conf.setType(ConfigType.valueOf(deviceTypes.getString("configType")));
				res.put(auxDT, conf);
			}
		} catch (SQLException e) {
			logger.error("Error with JDBC for CameraDriver: {}",e);
		} 
		return res;
	}

	private List<Device> retrieveDBDevices() {
		List<Device> res = new ArrayList<Device>();
		Statement stmt = null;
		try {
			Connection conn = getConnection();
			stmt = conn.createStatement();
			String query = "SELECT * FROM device;";
			ResultSet devices = stmt.executeQuery(query);
			do{
				CameraDevice auxDev = new CameraDevice();
				auxDev.setId(devices.getString("id"));
				Class cameraType = Class.forName(devices.getString("type"));
				Constructor camConstructor = cameraType.getConstructor(new Class[] {String.class, String.class});
				IpCamDevice camera = (IpCamDevice) camConstructor.newInstance(devices.getString("name"),devices.getString("ipAddr"));
				camera.setAuth(new IpCamAuth(devices.getString("user"), devices.getString("pass")));
				IpCamDeviceRegistry.register(camera);
				for (Webcam cam : Webcam.getWebcams()){
					if (cam.getDevice().equals(camera))
						auxDev.setCamera(cam);
				}
				res.add(auxDev);
			}
			while (!devices.isLast());
		} catch (SQLException e) {
			logger.error("Error with JDBC for CameraDriver: {}",e);
		} catch (ClassNotFoundException e) {
			logger.error("Error trying to get the class for camera: {}", e);
		} catch (InstantiationException e) {
			logger.error("Error trying to create an instance for camera: {}", e);
		} catch (IllegalAccessException e) {
			logger.error("Error trying to access the class for camera: {}", e);
		} catch (NoSuchMethodException e) {
			logger.error("Error trying to use the constructor for camera: {}", e);
		} catch (Exception e) {
			logger.error("Error retrieving devices from database: {}", e);
		}
		return res;
	}

	public void run() {
		String method = "public void run()";
		this.logger.debug(method + " Starting CamDriver.");



		this.logger.debug(method + " CamDriver stopped.");
	}

	public Message configureDevice(String deviceId, Configuration conf) {
		boolean found = false;
		for(Device d : this.deviceList)
			if(d.getId().equals(deviceId)){
				d.setConfiguration(conf);
				found = true;
			}
		Message res = new Message();
		res.setCreationDate(Calendar.getInstance());
		if(found){
			res.setMessageType(MessageType.Success);
			res.setDescription("Configuration set on device correctly.");
		}
		else{
			res.setMessageType(MessageType.Error);
			res.setDescription("Error setting configuration on device.");
		}
		return res;
	}

	public boolean contains(String idParameter) {
		boolean res = false;
		for (Device d : this.deviceList)
			for (Parameter p : d.getParameterList())
				if (p.getId().equals(idParameter))
					res = true;
		return res;
	}

	public String getDescriptionDriver() {
		return this.descriptionDriver;
	}

	public Device getDeviceByType(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Device> getDeviceList() {
		return this.deviceList;
	}

	public Configuration getDeviceTypeConfiguration(String typeId) {
		Configuration res = new Configuration();
		for(DeviceType dT : this.deviceConfigurationTypes.keySet())
			if(dT.getId().equals(typeId))
				res = this.deviceConfigurationTypes.get(dT);
		return res;
	}

	public String getIdDriver() {
		return this.idDriver;
	}

	public String getNameDriver() {
		return this.nameDriver;
	}

	public Technology getTechnology() {
		return this.technology;
	}

	public Configuration getTechnologyConfiguration() {
		return this.technologyConfiguration;
	}

	public String getVersionDriver() {
		return this.versionDriver;
	}

	public Message initSelfDiscovery() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isSelfDiscoveryAvailable() {
		return this.selfDiscoveryAvailable;
	}

	public boolean isSubscriptionAvailable() {
		return this.subscriptionAvailable;
	}

	public boolean isUpdating() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setParameterPhysicalValue(String parameterId, String value) {
		// TODO Auto-generated method stub

	}

	public void setTechManager(TechManager techManager) {
		this.techManager = techManager;
	}

	public void setTechnology(Technology tech) {
		this.technology = tech;
	}

	public Message setTechnologyConfiguration(Configuration confTech) {
		this.technologyConfiguration = confTech;
		return null;
	}

	public Message updateParameter(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Message updateParameter(String arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTechManger(TechManager tM){
		this.techManager = tM;
	}

	@Override
	public List<Configuration> getConfigurationTypes() {
		List<Configuration> res = new ArrayList<Configuration>();
		for (Entry<DeviceType, Configuration> entry  : this.deviceConfigurationTypes.entrySet())
			res.add(entry.getValue());
		return res;
	}

	@Override
	public List<DeviceType> getDeviceTypesList() {
		List<DeviceType> res = new ArrayList<DeviceType>();
		for (DeviceType entry : this.deviceConfigurationTypes.keySet())
			res.add(entry);
		return res;
	}

}
