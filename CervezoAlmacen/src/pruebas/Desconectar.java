package pruebas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Desconectar {
	/* the default framework is embedded */
	private String framework = "embedded";
	private String protocol = "jdbc:derby:";

 public static void main(String[] args) {
	new Desconectar().go(args);
}	
	void go(String[] args) {
		/* parse the arguments to determine which framework is desired */
		parseArguments(args);

		System.out.println("SimpleApp starting in " + framework + " mode");

		/*
		 * We will be using Statement and PreparedStatement objects for
		 * executing SQL. These objects, as well as Connections and ResultSets,
		 * are resources that should be released explicitly after use, hence the
		 * try-catch-finally pattern used below. We are storing the Statement
		 * and Prepared statement object references in an array list for
		 * convenience.
		 */
		Connection conn = null;
		ArrayList<Statement> statements = new ArrayList<Statement>(); // list of
																		// Statements,
																		// PreparedStatements
		PreparedStatement psInsert;
		PreparedStatement psUpdate;
		Statement s;
		ResultSet rs = null;
		try {
			Properties props = new Properties(); // connection properties
			// providing a user name and password is optional in the embedded
			// and derbyclient frameworks
			props.put("user", "user1");
			props.put("password", "user1");

			/*
			 * By default, the schema APP will be used when no username is
			 * provided. Otherwise, the schema name is the same as the user name
			 * (in this case "user1" or USER1.)
			 *
			 * Note that user authentication is off by default, meaning that any
			 * user can connect to your database using any password. To enable
			 * authentication, see the Derby Developer's Guide.
			 */

			String dbName = "CervecitaDB"; // the name of the database

			/*
			 * This connection specifies create=true in the connection URL to
			 * cause the database to be created when connecting for the first
			 * time. To remove the database, remove the directory derbyDB (the
			 * same as the database name) and its contents.
			 *
			 * The directory derbyDB will be created under the directory that
			 * the system property derby.system.home points to, or the current
			 * directory (user.dir) if derby.system.home is not set.
			 */
			conn = DriverManager.getConnection(protocol + dbName + ";create=false", props);

			System.out.println("Connected to  database " + dbName);

			// We want to control transactions manually. Autocommit is on by
			// default in JDBC.
			conn.setAutoCommit(false);

			/*
			 * Creating a statement object that we can use for running various
			 * SQL statements commands against the database.
			 */
			s = conn.createStatement();
			statements.add(s);


			// TODO  Usar esto para check que las tablas existen ?? 
			

/*			List<String> tablasOld = Tools.listTables(s);

			if(!tablasOld.contains("BREWERY")){
			}

			if(!tablasOld.contains("CERVEZA")){
			}
			
			if(!tablasOld.contains("ENTRADAS")){
				
			}
*/			
			
			
			

			 ResultSet pepito= s.executeQuery("SELECT * FROM brewery");
			
			 ResultSetMetaData rsmd = pepito.getMetaData();
			 int columnsNumber = rsmd.getColumnCount();
			 while (pepito.next()) {
			 for (int i = 1; i <= columnsNumber; i++) {
			 if (i > 1) System.out.print(", ");
			 String columnValue = pepito.getString(i);
			 System.out.print(columnValue + " " + rsmd.getColumnName(i));
			 }
			 System.out.println("");
			 }


			 // datos prueba cerveza
			 


				System.out.println("cervezas");
				
				 pepito= s.executeQuery("SELECT * FROM cerveza");
					
				 rsmd = pepito.getMetaData();
				 columnsNumber = rsmd.getColumnCount();
				 while (pepito.next()) {
				 for (int i = 1; i <= columnsNumber; i++) {
				 if (i > 1) System.out.print(", ");
				 String columnValue = pepito.getString(i);
				 System.out.print(columnValue + " " + rsmd.getColumnName(i));
				 }
				 System.out.println("");
				 }


				 
				 // TODO  Esto debe ser el cierre, deberia hacer otra para ceerrar
				 
				 
			if (framework.equals("embedded")) {
				try {
					// the shutdown=true attribute shuts down Derby
					DriverManager.getConnection("jdbc:derby:;shutdown=true");

					// To shut down a specific database only, but keep the
					// engine running (for example for connecting to other
					// databases), specify a database in the connection URL:
					// DriverManager.getConnection("jdbc:derby:" + dbName +
					// ";shutdown=true");
				} catch (SQLException se) {
					if (((se.getErrorCode() == 50000) && ("XJ015".equals(se.getSQLState())))) {
						// we got the expected exception
						System.out.println("Derby shut down normally");
						// Note that for single database shutdown, the expected
						// SQL state is "08006", and the error code is 45000.
					} else {
						// if the error code or SQLState is different, we have
						// an unexpected exception (shutdown failed)
						System.err.println("Derby did not shut down normally");
						printSQLException(se);
					}
				}
			}
		} catch (SQLException sqle) {
			printSQLException(sqle);
		} finally {
			// release all open resources to avoid unnecessary memory usage

			// ResultSet
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException sqle) {
				printSQLException(sqle);
			}

			// Statements and PreparedStatements
			int i = 0;
			while (!statements.isEmpty()) {
				// PreparedStatement extend Statement
				Statement st = (Statement) statements.remove(i);
				try {
					if (st != null) {
						st.close();
						st = null;
					}
				} catch (SQLException sqle) {
					printSQLException(sqle);
				}
			}

			// Connection
			try {
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException sqle) {
				printSQLException(sqle);
			}
		}
	}

	/**
	 * Reports a data verification failure to System.err with the given message.
	 *
	 * @param message
	 *            A message describing what failed.
	 */
	private void reportFailure(String message) {
		System.err.println("\nData verification failed:");
		System.err.println('\t' + message);
	}

	/**
	 * Prints details of an SQLException chain to <code>System.err</code>.
	 * Details included are SQL State, Error code, Exception message.
	 *
	 * @param e
	 *            the SQLException from which to print details.
	 */
	public static void printSQLException(SQLException e) {
		// Unwraps the entire exception chain to unveil the real cause of the
		// Exception.
		while (e != null) {
			System.err.println("\n----- SQLException -----");
			System.err.println("  SQL State:  " + e.getSQLState());
			System.err.println("  Error Code: " + e.getErrorCode());
			System.err.println("  Message:    " + e.getMessage());
			// for stack traces, refer to derby.log or uncomment this:
			// e.printStackTrace(System.err);
			e = e.getNextException();
		}
	}

	/**
	 * Parses the arguments given and sets the values of this class's instance
	 * variables accordingly - that is, which framework to use, the name of the
	 * JDBC driver class, and which connection protocol to use. The protocol
	 * should be used as part of the JDBC URL when connecting to Derby.
	 * <p>
	 * If the argument is "embedded" or invalid, this method will not change
	 * anything, meaning that the default values will be used.
	 * </p>
	 * <p>
	 * 
	 * @param args
	 *            JDBC connection framework, either "embedded" or "derbyclient".
	 *            Only the first argument will be considered, the rest will be
	 *            ignored.
	 */
	private void parseArguments(String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("derbyclient")) {
				framework = "derbyclient";
				protocol = "jdbc:derby://localhost:1527/";
			}
		}
	}
}

	
	
	
	
	
	
	
	
	

	

