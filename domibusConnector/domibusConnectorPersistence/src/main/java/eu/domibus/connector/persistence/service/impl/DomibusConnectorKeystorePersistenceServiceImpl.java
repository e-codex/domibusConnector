package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.DomibusConnectorKeystore;
import eu.ecodex.dc5.util.repository.DC5ConfigItemDao;
import eu.ecodex.dc5.util.model.DC5ConfigItem;
import eu.domibus.connector.persistence.service.DomibusConnectorKeystorePersistenceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.Optional;
import java.util.UUID;

@Service
public class DomibusConnectorKeystorePersistenceServiceImpl implements DomibusConnectorKeystorePersistenceService {

	
	@Autowired
	DC5ConfigItemDao keystoreDao;

	@Override
	@Transactional
	public DomibusConnectorKeystore persistNewKeystore(DomibusConnectorKeystore pKeystore) {
		DC5ConfigItem dbKeystore = new DC5ConfigItem();
		
		String uuid = pKeystore.getUuid();

		if(StringUtils.isEmpty(uuid)) {
			uuid = String.format("%s@%s", UUID.randomUUID(), "dc.keystore.eu");
		}

		dbKeystore.setUuid(uuid);
		dbKeystore.setKeystore(pKeystore.getKeystoreBytes());

//		dbKeystore.setPassword(pKeystore.getPasswordPlain());
		dbKeystore.setDescription(pKeystore.getDescription());
//		dbKeystore.setType(pKeystore.getType());
		
		dbKeystore = keystoreDao.save(dbKeystore);
		
		pKeystore.setUuid(dbKeystore.getUuid());
		pKeystore.setUploaded(dbKeystore.getUploaded());
		
		return pKeystore;
	}
	
	@Override
	@Transactional
	public void updateKeystorePassword(DomibusConnectorKeystore pKeystore, String newKeystorePassword) {
		if (StringUtils.isEmpty(pKeystore.getUuid())) {
            throw new IllegalArgumentException("UUID of keystore must not be null!");
        }
		
		Optional<DC5ConfigItem> dbKeystore = keystoreDao.findByUuid(pKeystore.getUuid());
		if(dbKeystore.isPresent()) {
			keystoreDao.save(dbKeystore.get());
		}else {
			throw new NoResultException(String.format("No keystore with UUID [%s] found in database!", pKeystore.getUuid()));
		}
		
	}

	@Override
	public DomibusConnectorKeystore getKeystoreByUUID(String uuid) {
		throw new RuntimeException("Not implemented yet!?");
	}



}
