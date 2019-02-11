package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.LoggerHelper;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

public abstract class AbstractConnectionListener implements ConnectionListener {
    protected static ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private String ON_CREATE_CONNECTION_FORMAT = "[onCreateConnection]%s, %s";
    private String ON_CREATE_CONNECTION_FAILED_FORMAT = "[onCreateConnectionFailed]%s, %s";
    private String ON_RELEASE_CONNECTION_FORMAT = "[onReleaseConnection]%s, %s";
    private String ON_ABANDON_CONNECTION_FORMAT = "[onAbandonConnection]%s, %s";
    private String ON_BORROW_IDLE_CONNECTION_FORMAT = "[onBorrowIdleConnection]%s, %s";
    private String ON_BORROW_IDLE_CONNECTION_FAILED_FORMAT = "[onBorrowIdleConnectionFailed]%s, %s";
    private String ON_GET_CONNECTION_FORMAT = "[OnGetConnection]%s, total size: %d, busy size: %d, idle size: %d, wait size: %d";
    private String ON_GET_CONNECTION_FAILED_FORMAT = "[onGetConnectionFailed]%s, %s";

    public void onGetConnection(String poolDesc, int size, int busySize, int idleSize, int waitSize, long startTime) {
        LOGGER.info(String.format(this.ON_GET_CONNECTION_FORMAT, poolDesc, size, busySize, idleSize, waitSize));
    }

    public void onGetConnectionFailed(String poolDesc, int size, int busySize, int idleSize, int waitSize, Exception ex, long startTime) {
        if (ex != null) {
            LOGGER.error(String.format(this.ON_GET_CONNECTION_FAILED_FORMAT, poolDesc, size, busySize, idleSize, waitSize), ex);
        }
    }

    @Override
    public void onCreateConnection(String poolDesc, Connection connection, long startTime) {
        if (connection == null)
            return;

        doOnCreateConnection(poolDesc, connection, startTime);
    }

    protected void doOnCreateConnection(String poolDesc, Connection connection, long startTime) {
        logInfo(ON_CREATE_CONNECTION_FORMAT, poolDesc, connection);
    }

    @Override
    public void onCreateConnectionFailed(String poolDesc, String connDesc, Throwable exception, long startTime) {
        if (exception == null)
            return;

        doOnCreateConnectionFailed(poolDesc, connDesc, exception, startTime);
    }

    protected void doOnCreateConnectionFailed(String poolDesc, String connDesc, Throwable exception, long startTime) {
        logError(ON_CREATE_CONNECTION_FAILED_FORMAT, poolDesc, connDesc, exception);
    }

    @Override
    public void onReleaseConnection(String poolDesc, Connection connection) {
        if (connection != null) {
            doOnReleaseConnection(poolDesc, connection);
        }
    }

    protected void doOnReleaseConnection(String poolDesc, Connection connection) {
        logInfo(ON_RELEASE_CONNECTION_FORMAT, poolDesc, connection);
    }

    @Override
    public void onAbandonConnection(String poolDesc, Connection connection) {
        if (connection != null)
            doOnAbandonConnection(poolDesc, connection);
    }

    protected void doOnAbandonConnection(String poolDesc, Connection connection) {
        logInfo(ON_ABANDON_CONNECTION_FORMAT, poolDesc, connection);
    }

    public void onBorrowIdleConnection(String poolDesc, Connection connection, long startTime) {
        if (connection != null) {
            this.doOnBorrowIdleConnection(poolDesc, connection, startTime);
        }

    }

    public void onBorrowIdleConnectionFailed(String poolDesc, String connDesc, Exception e, long startTime) {
        if (e != null) {
            this.doOnBorrowIdleConnectionFailed(poolDesc, connDesc, e, startTime);
        }

    }

    protected void doOnBorrowIdleConnection(String poolDesc, Connection connection, long startTime) {
        this.logInfo(this.ON_BORROW_IDLE_CONNECTION_FORMAT, poolDesc, connection);
    }

    protected void doOnBorrowIdleConnectionFailed(String poolDesc, String connDesc, Exception e, long startTime) {
        this.logError(this.ON_BORROW_IDLE_CONNECTION_FAILED_FORMAT, poolDesc, connDesc, e);
    }

    private void logInfo(String format, String poolDesc, Connection connection) {
        String connectionUrl = getConnectionUrl(connection);
        String msg = String.format(format, poolDesc, connectionUrl);
        LOGGER.info(msg);
    }

    private void logError(String format, String poolDesc, String connectionUrl, Throwable exception) {
        String msg = String.format(format, poolDesc, connectionUrl);
        LOGGER.error(msg, exception);
    }

    protected String getConnectionUrl(Connection connection) {
        String url = "";
        if (connection == null)
            return url;

        try {
            DatabaseMetaData metaData = connection.getMetaData();
            if (metaData == null)
                return url;

            url = LoggerHelper.getSimplifiedDBUrl(metaData.getURL());
        } catch (Throwable e) {
            return url;
        }

        return url;
    }

    public static void setILogger(ILogger logger) {
        AbstractConnectionListener.LOGGER = logger;
    }

}
