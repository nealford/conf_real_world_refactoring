package com.nealford.art.memento.emotherearth.util;
/**
 * A database connection pool.  This maintains a collection
 * of pre-opened database connections.  Clients request them
 * as needed and return them to the pool when they're finished.
 * This provides a significant performance improvement over
 * explicitly opening database connections as needed.
 */
public class DBPool {
    // Set to false to disable pool debugging.
    static final boolean DEBUG = true;

    // Default values for max number of open handles and timeout
    static final private int defaultMaxConnections = 50; // Limit to 50 handles
    static long defaultTimeout = 60*60*1000; // Close after 1 hour

    // Defaults used when DEBUG==true
    static final private int debugDefaultMaxConnections = 5; // 5 handles
    static long debugDefaultTimeout = 5*60*1000; // Close after 5 minutes

    /**
     * Create a new pool that will open DB connections to the
     * designated JDBC database.
     *
     * @param classname Database Driver class name
     * @param url Connection URL
     *
     * @throws java.sql.SQLException
     */
    public DBPool(String classname, String url) throws java.sql.SQLException {
        try {   // load JDBC driver by name
            Class.forName(classname).newInstance();
        } catch (Exception e) { // Catch any exception from JDBC driver failure
            System.err.println("Failed to load JDBC driver: "+classname
                               +", Exception: "+e);
        }

        maxConnections = defaultMaxConnections;
        timeout = defaultTimeout;
        if (DEBUG) {
            maxConnections = debugDefaultMaxConnections;
            timeout = debugDefaultTimeout;
        }
        totalConnections = 0;
        freeList = new java.util.LinkedList();
        busy = new java.util.HashMap();
        this.url = url;
    }

    // Basic parameters for the connections to be opened.
    private String url;

    private int maxConnections; // Maximum connections to permit in this pool
    private long timeout; // How long to keep a connection open

    // List of available connections is treated as a Queue.
    // Released connections go onto end, connections are pulled from front.
    // This helps ensure that every connection will get expired.
    private java.util.LinkedList freeList; // List of available connections
    private java.util.HashMap busy; // hash of in-use connections.

    private int totalConnections; // Number of currently open connections
    private int pendingConnections; // Number of connections to be opened
    private int maxTotalConnections; // Highest number of open connections seen

    // Get a connection from the pool
    public java.sql.Connection getConnection()
            throws java.sql.SQLException {
        DBPoolConnection connection = null;  //Connection to return
        int closed = 0;

        // Try to get a new connection from the free list,
        // closing any expired connections we find.
        synchronized(freeList) {
            while (connection == null
                   && freeList.size() > 0) {
                connection = (DBPoolConnection)freeList.removeFirst();
                if (connection.expired()) {
                    connection.db.close();  // Close it.
                    closed++;  // Count how many handles I closed
                    connection = null;
                } else if (connection.db.isClosed()) {
                    System.err.println("DBPool: Connection is closed; cleaning up");
                    closed++;
                    connection = null;
                }
            }
        }

        // Update the count in a thread-safe fashion
        if (closed > 0) {
            synchronized(this) {
                totalConnections -= closed;
            }
        }

        // If I didn't find a connection on the free list, open a new one.
        if (connection == null) {
            synchronized(this) {
                // Limit how many simultaneous connections I try to open
                if (pendingConnections > 4
                    || pendingConnections>totalConnections) {
                    throw new java.sql.SQLException
                    ("There are "
                     +pendingConnections
                     +" connection attempts already in progress.");
                }

                if (pendingConnections + totalConnections >= maxConnections) {
                    if (DEBUG) { // If DEBUG, dump stack of every in-use handle
                        synchronized(busy) {
                            java.util.Iterator i = busy.keySet().iterator();
                            while (i.hasNext()) {
                                ((DBPoolConnection)i.next())
                                .debug_stacktrace.printStackTrace();
                            }
                        }
                    }

                    // Note that this can happen in production code if there
                    // are too many simultaneous requests.
                    System.err.println("DBPool is full. There are "
                                       +totalConnections
                                       +" database handles already in use.");

                    /* Special for CMPDOC: Server overloaded. */
                    throw new java.sql.SQLException
                    ("Not enough database handles");
                }
                pendingConnections++;
            }

            try {
                connection = new DBPoolConnection(openNewConnection(),timeout);
            } finally {
                // No matter what, one fewer pending connection.
                synchronized(this) {
                    pendingConnections--;
                }
            }

            synchronized(this) {
                // If succeeded in opening a new connection, we now
                // have one more open connection.
                totalConnections++;

                // Display ongoing statistics about numbers of connections
                if (DEBUG) {
                    if (totalConnections > maxTotalConnections) {
                        maxTotalConnections = totalConnections;
                        System.err.println("Peak open DB Connections: "
                                           +maxTotalConnections);
                    }
                }
            }
        }

        synchronized(busy) {
            busy.put(connection.db,connection);
        }

        // Debugging: capture the current stack trace.
        // This is memory-consuming, so should be disabled in production code
        if (DEBUG) connection.debug_stacktrace = new Throwable();
        return connection.db;
    }

    /**
     * Open a new database connection.  Throw a java.sql.SQLException
     * if the pool size is exceeded or if the connection attempt failed.
     *
     * @return java.sql.Connection connection to the database from the pool
     * @throws java.sql.SQLException
     */
    private java.sql.Connection openNewConnection()
            throws java.sql.SQLException {
        try { // Open a new connection and return that.
            java.sql.Connection connection = java.sql.DriverManager.getConnection(url);
            return connection;
        } catch (java.sql.SQLException e) {
            System.out.println("DBPool.openNewConnection: Failed: "+e);
            throw e;
        }
    }

    /**
     * Release a database connection back into the pool to be reused.
     * This code complains loudly if a connection is returned that
     * wasn't allocated from this pool or is not currently marked
     * as being in use.
     *
     * @param db Connection to be released
     */
    public void release(java.sql.Connection db) {
        // Get full info about this connection.
        DBPoolConnection connection = null;
        synchronized(busy) {
            connection = (DBPoolConnection)busy.remove(db);
        }

        if (connection != null) { // Add to free list
            synchronized(freeList) {
                freeList.addLast(connection);
            }
            return;
        }

        // If we get here, then the db handle wasn't in the pool.
        // This should never happen in production code, so verbose
        // error handling is appropriate, even if DEBUG is false.
        System.err.println((new java.util.Date())+": Error in DBPool");
        System.err.println("Attempt to return a DB handle not in the pool.");
        new Throwable().printStackTrace(); // Stack dump
        throw new Error("Attempt to return a DB handle not in the pool.");
    }
}

/**
 * A utility class that holds a database connection along with
 * some other useful information.
 */
class DBPoolConnection {
    private long expiration;
    java.sql.Connection db;
    public Throwable debug_stacktrace;
    public DBPoolConnection(java.sql.Connection db, long timeout) {
        this.db = db;
        expiration = System.currentTimeMillis() + timeout;
    }
    public boolean expired() {
        return expiration < System.currentTimeMillis();
    }
}








