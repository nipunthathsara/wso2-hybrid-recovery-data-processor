package org.wso2.carbon.identity.extended.recovery.store;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.base.IdentityException;
import org.wso2.carbon.identity.mgt.IdentityMgtConfig;
import org.wso2.carbon.identity.mgt.dto.UserRecoveryDataDO;
import org.wso2.carbon.identity.mgt.store.JDBCUserRecoveryDataStore;
import org.wso2.carbon.identity.mgt.store.RegistryRecoveryDataStore;
import org.wso2.carbon.identity.mgt.store.UserRecoveryDataStore;

public class HybridUserRecoveryDataStore implements UserRecoveryDataStore {

    private static Log log = LogFactory.getLog(HybridUserRecoveryDataStore.class);

    private RegistryRecoveryDataStore registryRecoveryDataStore = new RegistryRecoveryDataStore();
    private JDBCUserRecoveryDataStore jdbcUserRecoveryDataStore = new JDBCUserRecoveryDataStore();

    @Override
    public void store(UserRecoveryDataDO recoveryDataDO) throws IdentityException {

        if (isJDBCPrioritized()) {
            if (log.isDebugEnabled()) {
                log.debug("Storing JDBC recovery data for the user : " + recoveryDataDO.getUserName()
                        + ", (hashed)code : " + DigestUtils.sha256Hex(recoveryDataDO.getCode()));
            }
            jdbcUserRecoveryDataStore.store(recoveryDataDO);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Storing Registry recovery data for the user : " + recoveryDataDO.getUserName()
                        + ", (hashed)code : " + DigestUtils.sha256Hex(recoveryDataDO.getCode()));
            }
            registryRecoveryDataStore.store(recoveryDataDO);
        }
    }

    @Override
    public void store(UserRecoveryDataDO[] recoveryDataDOs) throws IdentityException {

        if (isJDBCPrioritized()) {
            if (log.isDebugEnabled()) {
                log.debug("Storing recovery data in the JDBC store.");
            }
            jdbcUserRecoveryDataStore.store(recoveryDataDOs);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Storing recovery data in the Registry store.");
            }
            registryRecoveryDataStore.store(recoveryDataDOs);
        }
    }

    @Override
    public UserRecoveryDataDO load(String code) throws IdentityException {

        if (isJDBCPrioritized()) {
            if (log.isDebugEnabled()) {
                log.debug("Loading confirmation (hashed)code : " + DigestUtils.sha256Hex(code) + " from the JDBC first.");
            }
            UserRecoveryDataDO userRecoveryDataDO = jdbcUserRecoveryDataStore.load(code);
            if (userRecoveryDataDO == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Loading confirmation (hashed)code : " + DigestUtils.sha256Hex(code)
                            + " from the Registry, as the JDBC failed.");
                }
                userRecoveryDataDO = registryRecoveryDataStore.load(code);
            }
            return userRecoveryDataDO;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Loading confirmation (hashed)code : " + DigestUtils.sha256Hex(code) + " from the Registry first.");
            }
            UserRecoveryDataDO userRecoveryDataDO = registryRecoveryDataStore.load(code);
            if (userRecoveryDataDO == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Loading confirmation (hashed)code : " + DigestUtils.sha256Hex(code)
                            + " from the JDBC, as the Registry failed.");
                }
                userRecoveryDataDO = jdbcUserRecoveryDataStore.load(code);
            }
            return userRecoveryDataDO;
        }
    }

    @Override
    public void invalidate(String code) throws IdentityException {

        if (isJDBCPrioritized()) {
            if (log.isDebugEnabled()) {
                log.debug("Invalidating (hashed)code : " + DigestUtils.sha256Hex(code) + " from the JDBC store.");
            }
            jdbcUserRecoveryDataStore.invalidate(code);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Invalidating (hashed)code : " + DigestUtils.sha256Hex(code) + " from the Registry store.");
            }
            registryRecoveryDataStore.invalidate(code);
        }
    }

    @Override
    public void invalidate(UserRecoveryDataDO recoveryDataDO) throws IdentityException {

        if (isJDBCPrioritized()) {
            if (log.isDebugEnabled()) {
                log.debug("Invalidating recovery data from the JDBC store. username : " + recoveryDataDO.getUserName()
                        + ", (hashed)code : " + DigestUtils.sha256Hex(recoveryDataDO.getCode()));
            }
            jdbcUserRecoveryDataStore.invalidate(recoveryDataDO);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Invalidating recovery data from the Registry store. username : " + recoveryDataDO.getUserName()
                        + ", (hashed)code : " + DigestUtils.sha256Hex(recoveryDataDO.getCode()));
            }
            registryRecoveryDataStore.invalidate(recoveryDataDO);
        }
    }

    @Override
    public void invalidate(String userId, int tenantId) throws IdentityException {

        if (isJDBCPrioritized()) {
            if (log.isDebugEnabled()) {
                log.debug("Invalidating recovery data for the userId : " + userId + ", tenantId : " + tenantId +
                        " from the JDBC store.");
            }
            jdbcUserRecoveryDataStore.invalidate(userId, tenantId);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Invalidating recovery data for the userId : " + userId + ", tenantId : " + tenantId +
                        " from the Registry store.");
            }
            registryRecoveryDataStore.invalidate(userId, tenantId);
        }
    }

    @Override
    public UserRecoveryDataDO[] load(String userName, int tenantId) throws IdentityException {

        if (isJDBCPrioritized()) {
            if (log.isDebugEnabled()) {
                log.debug("Loading recovery data for the username : " + userName + ", tenantId : " + tenantId +
                        " from the JDBC store.");
            }
            return jdbcUserRecoveryDataStore.load(userName, tenantId);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Loading recovery data for the username : " + userName + ", tenantId : " + tenantId +
                        " from the Registry store.");
            }
            return registryRecoveryDataStore.load(userName, tenantId);
        }
    }

    private boolean isJDBCPrioritized() {
        return Boolean.parseBoolean(IdentityMgtConfig.getInstance().getProperty(Constants.IS_JDBC_PRIORITIZED));
    }
}
