package org.wso2.carbon.identity.extended.recovery.store;

import org.wso2.carbon.identity.base.IdentityException;
import org.wso2.carbon.identity.mgt.IdentityMgtConfig;
import org.wso2.carbon.identity.mgt.dto.UserRecoveryDataDO;
import org.wso2.carbon.identity.mgt.store.JDBCUserRecoveryDataStore;
import org.wso2.carbon.identity.mgt.store.RegistryRecoveryDataStore;
import org.wso2.carbon.identity.mgt.store.UserRecoveryDataStore;

public class HybridUserRecoveryDataStore implements UserRecoveryDataStore {

    private boolean isJDBCPrioritise = false;
    private RegistryRecoveryDataStore registryRecoveryDataStore = new RegistryRecoveryDataStore();
    private JDBCUserRecoveryDataStore jdbcUserRecoveryDataStore = new JDBCUserRecoveryDataStore();

    @Override
    public void store(UserRecoveryDataDO recoveryDataDO) throws IdentityException {

        if (isJDBCPrioritise) {
            jdbcUserRecoveryDataStore.store(recoveryDataDO);
        } else {
            registryRecoveryDataStore.store(recoveryDataDO);
        }
    }

    @Override
    public void store(UserRecoveryDataDO[] recoveryDataDOs) throws IdentityException {

        if (isJDBCPrioritise) {
            jdbcUserRecoveryDataStore.store(recoveryDataDOs);
        } else {
            registryRecoveryDataStore.store(recoveryDataDOs);
        }
    }

    @Override
    public UserRecoveryDataDO load(String code) throws IdentityException {

        if (isJDBCPrioritise) {
            UserRecoveryDataDO userRecoveryDataDO = jdbcUserRecoveryDataStore.load(code);
            if (userRecoveryDataDO == null) {
                userRecoveryDataDO = registryRecoveryDataStore.load(code);
            }
            return userRecoveryDataDO;
        } else {
            UserRecoveryDataDO userRecoveryDataDO = registryRecoveryDataStore.load(code);
            if (userRecoveryDataDO == null) {
                userRecoveryDataDO = jdbcUserRecoveryDataStore.load(code);
            }
            return userRecoveryDataDO;
        }
    }

    @Override
    public void invalidate(String code) throws IdentityException {

        if (isJDBCPrioritise) {
            jdbcUserRecoveryDataStore.invalidate(code);
        } else {
            registryRecoveryDataStore.invalidate(code);
        }
    }

    @Override
    public void invalidate(UserRecoveryDataDO recoveryDataDO) throws IdentityException {

        if (isJDBCPrioritise) {
            jdbcUserRecoveryDataStore.invalidate(recoveryDataDO);
        } else {
            registryRecoveryDataStore.invalidate(recoveryDataDO);
        }
    }

    @Override
    public void invalidate(String userId, int tenantId) throws IdentityException {

        if (isJDBCPrioritise) {
            jdbcUserRecoveryDataStore.invalidate(userId, tenantId);
        } else {
            registryRecoveryDataStore.invalidate(userId, tenantId);
        }
    }

    @Override
    public UserRecoveryDataDO[] load(String userName, int tenantId) throws IdentityException {

        if (isJDBCPrioritise) {
            return jdbcUserRecoveryDataStore.load(userName, tenantId);
        } else {
            return registryRecoveryDataStore.load(userName, tenantId);
        }
    }
}
