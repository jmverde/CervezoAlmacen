package pruebas;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Tools {

	
	/**
	 * Se le pasa un statment de una conexion Sql y devuelve un list con los nombres de las tablas creadas
	 * por el usuario, no las del sistema
	 * @param s
	 * @return
	 * @throws SQLException
	 */
	static List<String> listTables(Statement s) throws SQLException {

		List<String> nombres = new ArrayList<String>();

		// ResultSet pepito= s.executeQuery("select * from SYS.SYSTABLES");

		ResultSet rs = s.executeQuery("select TABLENAME from SYS.SYSTABLES where TABLETYPE='T'");

		while (rs.next()) {
			nombres.add(rs.getString(1));
		}

		return nombres;

		/*
		 * eso seria para ver todo lo que hay ResultSetMetaData rsmd =
		 * pepito.getMetaData(); int columnsNumber = rsmd.getColumnCount();
		 * while (pepito.next()) { for (int i = 1; i <= columnsNumber; i++) { if
		 * (i > 1) System.out.print(",  "); String columnValue =
		 * pepito.getString(i); System.out.print(columnValue + " " +
		 * rsmd.getColumnName(i)); } System.out.println(""); }
		 */
	}
}
