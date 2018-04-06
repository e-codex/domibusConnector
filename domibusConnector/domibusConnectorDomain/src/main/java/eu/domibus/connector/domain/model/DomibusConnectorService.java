package eu.domibus.connector.domain.model;

import java.io.Serializable;
import org.springframework.core.style.ToStringCreator;


/**
 * @author riederb
 * @version 1.0
 */
public class DomibusConnectorService implements Serializable {

	private final String service;
	private final String serviceType;

	/**
	 * 
	 * @param service service
	 * @param serviceType serviceType
	 */
	public DomibusConnectorService(final String service, final String serviceType){
	   this.service = service;
	   this.serviceType = serviceType;
	}

	public String getService(){
		return this.service;
	}

	public String getServiceType(){
		return this.serviceType;
	}

    @Override
    public String toString() {
        ToStringCreator builder = new ToStringCreator(this);
        builder.append("service", this.service);
        builder.append("serviceType", this.serviceType);
        return builder.toString();        
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DomibusConnectorService that = (DomibusConnectorService) o;

		return service != null ? service.equals(that.service) : that.service == null;
	}

	@Override
	public int hashCode() {
		return service != null ? service.hashCode() : 0;
	}
}