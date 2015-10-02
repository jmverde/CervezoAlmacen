package pruebas;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class pruebando {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub

		
		Conectar cosa = new pruebas.Conectar();
		
		Statement s = cosa.conecta();
		
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


		
	}

}
