package eu.ecodex.webadmin.blogic.connector.statistics.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.primefaces.model.chart.PieChartModel;

import eu.domibus.connector.common.db.model.DomibusConnectorAction;
import eu.domibus.connector.common.db.model.DomibusConnectorService;
import eu.ecodex.webadmin.blogic.connector.statistics.IConnectorSummaryService;
import eu.ecodex.webadmin.dao.IDomibusMessageWebAdminDao;
import eu.ecodex.webadmin.dao.IDomibusWebAdminConnectorServiceDao;
import eu.ecodex.webadmin.dao.IDomibusWebAdminUserDao;

public class ConnectorSummaryServiceImpl implements IConnectorSummaryService, Serializable {

    private static final long serialVersionUID = 4855196930128932326L;

    private String outgoingMessagesCount = "";
    private String incomingMessagesCount = "";
    private List<String> serviceList;
    private PieChartModel pieModelMessageSummary;
    private PieChartModel pieModelServiceSummary;
    private IDomibusMessageWebAdminDao domibusMessageWebAdminDao;
    private IDomibusWebAdminConnectorServiceDao domibusWebAdminConnectorServiceDao;
    
    @PostConstruct
    public void init(){
    	serviceList = new ArrayList<String>();
    	List<DomibusConnectorService> resultList = domibusWebAdminConnectorServiceDao.getServiceList();
    	for (DomibusConnectorService domibusConnectorService : resultList) {
			serviceList.add(domibusConnectorService.getService());
		}
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.ecodex.webadmin.blogic.impl.IReportingServiceManager#
     * getNationalMessageCount()
     */
    @Override
    public void generateMessageSummary() {
        Long resultOutgoing = domibusMessageWebAdminDao.countOutgoingMessages();
        outgoingMessagesCount = resultOutgoing.toString();

        Long resultIncoming = domibusMessageWebAdminDao.countIncomingMessages();
        incomingMessagesCount = resultIncoming.toString();
        pieModelMessageSummary = new PieChartModel();

        pieModelMessageSummary.set("Incoming Messages", resultIncoming);
        pieModelMessageSummary.set("Outgoing Messages", resultOutgoing);
        pieModelServiceSummary = new PieChartModel();
        for (String service : serviceList) {
        	 HashMap<String, Long> serviceList = domibusMessageWebAdminDao.countService(service);


             
             pieModelServiceSummary.set(service, serviceList.get(service));
             
		}
        
        HashMap<String, Long> serviceList = domibusMessageWebAdminDao.countUndefinedService();
        if (serviceList.containsKey("Undefined")){
        	pieModelServiceSummary.set("Undefined", serviceList.get("Undefined"));	
        }
        

       

    }

    public String getOutgoingMessagesCount() {
        return outgoingMessagesCount;
    }

    public void setOutgoingMessagesCount(String outgoingMessagesCount) {
        this.outgoingMessagesCount = outgoingMessagesCount;
    }

    public String getIncomingMessagesCount() {
        return incomingMessagesCount;
    }

    public void setIncomingMessagesCount(String incomingMessagesCount) {
        this.incomingMessagesCount = incomingMessagesCount;
    }

    public PieChartModel getPieModelMessageSummary() {
        return pieModelMessageSummary;
    }

    public void setPieModelMessageSummary(PieChartModel pieModelMessageSummary) {
        this.pieModelMessageSummary = pieModelMessageSummary;
    }

    public PieChartModel getPieModelServiceSummary() {
        return pieModelServiceSummary;
    }

    public void setPieModelServiceSummary(PieChartModel pieModelServiceSummary) {
        this.pieModelServiceSummary = pieModelServiceSummary;
    }

	public IDomibusMessageWebAdminDao getDomibusMessageWebAdminDao() {
		return domibusMessageWebAdminDao;
	}

	public void setDomibusMessageWebAdminDao(
			IDomibusMessageWebAdminDao domibusMessageWebAdminDao) {
		this.domibusMessageWebAdminDao = domibusMessageWebAdminDao;
	}

	public IDomibusWebAdminConnectorServiceDao getDomibusWebAdminConnectorServiceDao() {
		return domibusWebAdminConnectorServiceDao;
	}

	public void setDomibusWebAdminConnectorServiceDao(
			IDomibusWebAdminConnectorServiceDao domibusWebAdminConnectorServiceDao) {
		this.domibusWebAdminConnectorServiceDao = domibusWebAdminConnectorServiceDao;
	}

}
