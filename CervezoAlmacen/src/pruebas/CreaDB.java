package pruebas;

/*

Derby - Class SimpleApp

Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

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

public class CreaDB {
	/* the default framework is embedded */
	private String framework = "embedded";
	private String protocol = "jdbc:derby:";

	/**
	 * <p>
	 * Starts the demo by creating a new instance of this class and running the
	 * <code>go()</code> method.
	 * </p>
	 * <p>
	 * When you run this application, you may give one of the following
	 * arguments:
	 * <ul>
	 * <li><code>embedded</code> - default, if none specified. Will use Derby's
	 * embedded driver. This driver is included in the derby.jar file.</li>
	 * <li><code>derbyclient</code> - will use the Derby client driver to access
	 * the Derby Network Server. This driver is included in the derbyclient.jar
	 * file.</li>
	 * </ul>
	 * <p>
	 * When you are using a client/server framework, the network server must
	 * already be running when trying to obtain client connections to Derby.
	 * This demo program will will try to connect to a network server on this
	 * host (the localhost), see the <code>protocol</code> instance variable.
	 * </p>
	 * <p>
	 * When running this demo, you must include the correct driver in the
	 * classpath of the JVM. See <a href="example.html">example.html</a> for
	 * details.
	 * </p>
	 * 
	 * @param args
	 *            This program accepts one optional argument specifying which
	 *            connection framework (JDBC driver) to use (see above). The
	 *            default is to use the embedded JDBC driver.
	 */
	public static void main(String[] args) {
		new CreaDB().go(args);
		System.out.println("SimpleApp finished");
	}

	/**
	 * <p>
	 * Starts the actual demo activities. This includes creating a database by
	 * making a connection to Derby (automatically loading the driver), creating
	 * a table in the database, and inserting, updating and retrieving some
	 * data. Some of the retrieved data is then verified (compared) against the
	 * expected results. Finally, the table is deleted and, if the embedded
	 * framework is used, the database is shut down.
	 * </p>
	 * <p>
	 * Generally, when using a client/server framework, other clients may be (or
	 * want to be) connected to the database, so you should be careful about
	 * doing shutdown unless you know that no one else needs to access the
	 * database until it is rebooted. That is why this demo will not shut down
	 * the database unless it is running Derby embedded.
	 * </p>
	 *
	 * @param args
	 *            - Optional argument specifying which framework or JDBC driver
	 *            to use to connect to Derby. Default is the embedded framework,
	 *            see the <code>main()</code> method for details.
	 * @see #main(String[])
	 */
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
			conn = DriverManager.getConnection(protocol + dbName + ";create=true", props);

			System.out.println("Connected to and created database " + dbName);

			// We want to control transactions manually. Autocommit is on by
			// default in JDBC.
			conn.setAutoCommit(false);

			/*
			 * Creating a statement object that we can use for running various
			 * SQL statements commands against the database.
			 */
			s = conn.createStatement();
			statements.add(s);

			// s.execute("SELECT * FROM location");

			// En primer lugar comprobamos que las tablas esten creadas, y si no
			// las creamos

			List<String> tablasOld = Tools.listTables(s);

			if(!tablasOld.contains("BREWERY")){
			
			// Creamos la primera tabla
			 s.execute("create table brewery("
			 + "id_brewer int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
			 + "name_brewer varchar(60),"
			 + "description varchar(120),"
			 + "CONSTRAINT primary_key2 PRIMARY KEY (id_brewer)"
			 +")");

			conn.commit();	
			}

			if(!tablasOld.contains("CERVEZA")){
				s.execute("create table cerveza("
						+"id_cerveza int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
						+"nombre varchar(60), "
						+"id_brewer int, "
						+"tipo varchar(60), "
						+"ABV decimal(4,2), "
						+"IBU decimal(3,0), "
						+"notas varchar(120),"
						+ "CONSTRAINT primary_key PRIMARY KEY (id_cerveza),"
						+ "CONSTRAINT fk1 FOREIGN KEY (id_brewer) REFERENCES brewery(id_brewer) "
						+")");
				
			conn.commit();	
			}
			
			if(!tablasOld.contains("ENTRADAS")){
				System.out.println("una");
				s.execute("create table entradas("
						+"id_entrada int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
						+"id_cerveza int, "
						+"lote varchar(30),"
						+"stock int, "
						+"caducidad date, "
						+"notas varchar(120), "
						+ "CONSTRAINT primary_key3 PRIMARY KEY (id_entrada),"
						+ "CONSTRAINT fk2 FOREIGN KEY (id_cerveza) REFERENCES cerveza(id_cerveza) "
						+")");
						
				System.out.println("cero");
			conn.commit();			
						
				
			}
			
			
			
			
			// Metemos los datos de prueba

			s.execute("insert into brewery (name_brewer,description) VALUES('chimay','mola')");
			s.execute("insert into brewery (name_brewer,description) VALUES('san miguel','no mola')");

			// los sacamos

			conn.commit();

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
			 
				s.execute("insert into cerveza (id_brewer,nombre,tipo,abv,ibu,notas) VALUES(1,'blanca','tripple',8,80.2,'esta esta rica')");
				s.execute("insert into cerveza (id_brewer,nombre,tipo,abv,notas) VALUES(2,'premium','lagger',5,'esta no')");


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

			 

			// System.out.println("acabe");

			// ResultSet cosa= s.executeQuery("SELECT * FROM location");
			//
			// while (cosa.next()) {
			// String lastName = cosa.getString("addr");
			// System.out.println(lastName + "\n");
			// }
			//

			// // Let's update some rows as well...
			//
			// // parameter 1 and 3 are num (int), parameter 2 is addr (varchar)
			// psUpdate = conn.prepareStatement(
			// "update location set num=?, addr=? where num=?");
			// statements.add(psUpdate);
			//
			// psUpdate.setInt(1, 180);
			// psUpdate.setString(2, "Grand Ave.");
			// psUpdate.setInt(3, 1956);
			// psUpdate.executeUpdate();
			// System.out.println("Updated 1956 Webster to 180 Grand");
			//
			// psUpdate.setInt(1, 300);
			// psUpdate.setString(2, "Lakeshore Ave.");
			// psUpdate.setInt(3, 180);
			// psUpdate.executeUpdate();
			// System.out.println("Updated 180 Grand to 300 Lakeshore");
			//
			//
			// /*
			// We select the rows and verify the results.
			// */
			// rs = s.executeQuery(
			// "SELECT num, addr FROM location ORDER BY num");
			//
			// /* we expect the first returned column to be an integer (num),
			// * and second to be a String (addr). Rows are sorted by street
			// * number (num).
			// *
			// * Normally, it is best to use a pattern of
			// * while(rs.next()) {
			// * // do something with the result set
			// * }
			// * to process all returned rows, but we are only expecting two
			// rows
			// * this time, and want the verification code to be easy to
			// * comprehend, so we use a different pattern.
			// */
			//
			// int number; // street number retrieved from the database
			// boolean failure = false;
			// if (!rs.next())
			// {
			// failure = true;
			// reportFailure("No rows in ResultSet");
			// }
			//
			// if ((number = rs.getInt(1)) != 300)
			// {
			// failure = true;
			// reportFailure(
			// "Wrong row returned, expected num=300, got " + number);
			// }
			//
			// if (!rs.next())
			// {
			// failure = true;
			// reportFailure("Too few rows");
			// }
			//
			// if ((number = rs.getInt(1)) != 1910)
			// {
			// failure = true;
			// reportFailure(
			// "Wrong row returned, expected num=1910, got " + number);
			// }
			//
			// if (rs.next())
			// {
			// failure = true;
			// reportFailure("Too many rows");
			// }
			//
			// if (!failure) {
			// System.out.println("Verified the rows");
			// }
			//
			// // delete the table
			// s.execute("drop table location");
			// System.out.println("Dropped table location");
			//
			// /*
			// We commit the transaction. Any changes will be persisted to
			// the database now.
			// */
			// conn.commit();
			// System.out.println("Committed the transaction");

			/*
			 * In embedded mode, an application should shut down the database.
			 * If the application fails to shut down the database, Derby will
			 * not perform a checkpoint when the JVM shuts down. This means that
			 * it will take longer to boot (connect to) the database the next
			 * time, because Derby needs to perform a recovery operation.
			 *
			 * It is also possible to shut down the Derby system/engine, which
			 * automatically shuts down all booted databases.
			 *
			 * Explicitly shutting down the database or the Derby engine with
			 * the connection URL is preferred. This style of shutdown will
			 * always throw an SQLException.
			 *
			 * Not shutting down when in a client environment, see method
			 * Javadoc.
			 */

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
