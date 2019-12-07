## Hybrid recovery data persistence processor for both JDBC and Registry
### Introduction / Use case.
This is a hybrid persistence processor to store confirmation codes in either Registry
or in the JDBC stores as configured. When loading the persisted codes for validation purposes, 
this will check in both the places. But the priority will be given as configured.

This will provide an smoother migration from Registry to JDBC without loosing any
codes and vice versa. 

### Applicable product versions.
WSO2 Identity Server 5.7.0

### How to use.
Follow below steps to use this log appender.
1. Build the project. ```mvn clean install```
2. Copy the JAR file into **<IS_HOME>/repository/components/lib** directory.
3. Open the file **<IS_HOME>/repository/conf/identity/identity-mgt.properties** file in a text editor and change
the recovery data processor's value as below.
```Identity.Mgt.User.Recovery.Data.Store=org.wso2.carbon.identity.extended.recovery.store.HybridUserRecoveryDataStore```
4. Add the below line to the same file.
```Identity.Mgt.User.Recovery.Data.Store.isJDBCPrioritized=true```
(This will give priority to JDBC when storing/loading the confirmation codes. If you prefer to have the Registry instead,
please leave this ``false``)
5. Restart the server.

### Persistence locations
1. Registry - ```/_system/config/repository/components/org.wso2.carbon.identity.mgt/data``` registry path.
2. JDBC - ```IDN_IDENTITY_META_DATA``` table.
