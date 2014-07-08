package es.upm.cedint.gateway.tech.device;


public class MobotixCamSearcher {
	
	private String ip;
	
	private String ptzUrl;
	
	private String videoUrl;
	
	private String eventsUrl;
	
	private String description;

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the ptzUrl
	 */
	public String getPtzUrl() {
		return ptzUrl;
	}

	/**
	 * @param ptzUrl the ptzUrl to set
	 */
	public void setPtzUrl(String ptzUrl) {
		this.ptzUrl = ptzUrl;
	}

	/**
	 * @return the videoUrl
	 */
	public String getVideoUrl() {
		return videoUrl;
	}

	/**
	 * @param videoUrl the videoUrl to set
	 */
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	/**
	 * @return the eventsUrl
	 */
	public String getEventsUrl() {
		return eventsUrl;
	}

	/**
	 * @param eventsUrl the eventsUrl to set
	 */
	public void setEventsUrl(String eventsUrl) {
		this.eventsUrl = eventsUrl;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	
}
