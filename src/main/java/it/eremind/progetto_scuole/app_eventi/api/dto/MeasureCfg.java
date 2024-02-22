package it.eremind.progetto_scuole.app_eventi.api.dto;

public class MeasureCfg {
	
	private Double minValidHr;
	private Double maxValidHr;
	
	private Integer hrPerMin;
	private Integer ppiPerMin;
	private Integer minPpiListSize;


	public Double getMinValidHr() {
		return minValidHr;
	}

	public void setMinValidHr(Double minValidHr) {
		this.minValidHr = minValidHr;
	}

	public Double getMaxValidHr() {
		return maxValidHr;
	}

	public void setMaxValidHr(Double maxValidHr) {
		this.maxValidHr = maxValidHr;
	}

	public Integer getHrPerMin() {
		return hrPerMin;
	}

	public void setHrPerMin(Integer hrPerMin) {
		this.hrPerMin = hrPerMin;
	}

	public Integer getPpiPerMin() {
		return ppiPerMin;
	}

	public void setPpiPerMin(Integer ppiPerMin) {
		this.ppiPerMin = ppiPerMin;
	}

	public Integer getMinPpiListSize() {
		return minPpiListSize;
	}

	public void setMinPpiListSize(Integer minPpiListSize) {
		this.minPpiListSize = minPpiListSize;
	}

}
