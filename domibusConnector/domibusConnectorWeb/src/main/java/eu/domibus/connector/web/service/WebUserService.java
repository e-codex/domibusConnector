package eu.domibus.connector.web.service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.domibus.connector.persistence.service.web.DomibusConnectorWebUserPersistenceService;
import eu.domibus.connector.web.dto.WebUser;

@Service("webUserService")
public class WebUserService {

	protected final static Logger LOGGER = LoggerFactory.getLogger(WebUserService.class);
	
	private DomibusConnectorWebUserPersistenceService persistenceService;
	
	@Autowired
	public void setPersistenceService(DomibusConnectorWebUserPersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	public WebUserService() {
		// TODO Auto-generated constructor stub
	}
	
	public List<WebUser> getAllUsers(){
		return persistenceService.listAllUsers();
	}
	
	@Transactional(readOnly = false, value = "transactionManager")
	public boolean resetUserPassword(WebUser user, String newInitialPassword) {
		LOGGER.debug("resetUserPassword called for user [{}] with new initial Password [{}]", user.getUsername(), newInitialPassword);
		
		WebUser resettedUser = null;
		try {
			resettedUser = persistenceService.resetUserPassword(user, newInitialPassword);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resettedUser!=null;
	}
	
	@Transactional(readOnly = false, value = "transactionManager")
	public boolean createNewUser(WebUser newUser) {
		WebUser user = null;
		try {
			user = persistenceService.createNewUser(newUser);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			
			e.printStackTrace();
		}
		return user!=null;
	}
	
	@Transactional(readOnly = false, value = "transactionManager")
	public boolean saveUser(WebUser user) {
		
		WebUser updated = persistenceService.updateUser(user);
		return updated!=null;
	}

}
