package series;


import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.Arrays;

public class SeriesDatabase {
	Connection conn;
	
	public SeriesDatabase() {
		// Abrimos la conexion
		openConnection();
		
		
	}
	
    

	public boolean openConnection() {
			try {
				if (conn == null || conn.isClosed()) {

				// Cargar el driver
				String drv = "com.mysql.cj.jdbc.Driver";
				try {
					Class.forName(drv);
				} catch (ClassNotFoundException e) {
					System.err.println("Error al iniciar el Driver:");
					System.err.println(e.getMessage());
					return false;
				}
//	System.out.println("Driver Cargado");
				//Realizamos conexion
				String addr = "127.0.0.1:3306";
				String db = "series";
				String user = "series_user";
				String pass = "series_pass";
				String url = "jdbc:mysql://"+addr+"/"+db;
				
				try {
					conn = DriverManager.getConnection(url,user,pass);
				} catch (SQLException e) {
					System.err.println("Error al conectar:");
					System.err.println(e.getMessage());
					return false;
				}
				return true;
				}
			} catch (SQLException e) {
				System.err.println("Error al comprobar conexion:");
				System.err.println(e.getMessage());
				return false;
			}
			
//	System.out.println("Sesion Iniciada");
			return false;
		
		
		}

	public boolean closeConnection() {
		try {
			if(conn != null)
				//Se cierra conexion
				conn.close();
		} catch (SQLException e) {
			System.err.println("Error al cerrar sesion:");
			System.out.println(e.getMessage());
			return false;
		}
		//System.out.println("Sesion Cerrada");
		return true;
	}

	public boolean createTableCapitulo() {
		try {
			//Comprabacion de que existe conexion
			if (conn == null || conn.isClosed() ) {
				openConnection();
			}
		} catch (SQLException e) {
			System.err.println("Error al comprobar conexion:");
			System.err.println(e.getMessage());
			return false;
		}
		// Comando sql para crear capitulo
		String tabla = 	"CREATE TABLE capitulo (" +
									"n_orden INT," +
									"titulo VARCHAR(100)," +
									"fecha_estreno DATE," +
									"duracion INT," +
									"id_serie INT,"+
									"n_temporada INT," +
									"PRIMARY KEY(n_orden, id_serie, n_temporada),"+
									"FOREIGN KEY (id_serie, n_temporada) REFERENCES temporada (id_serie, n_temporada)"+
									"ON DELETE CASCADE ON UPDATE CASCADE" +
									");"; 
		Statement st;
			try {
				//llamadas para realizarlo en bd
				st = conn.createStatement();
				st.executeUpdate(tabla);
				st.close();
			} catch (SQLException e) {
				System.err.println("Error al crear la tabla Capitulo:");
				System.err.println(e.getMessage());
				return false;
			}

	
		return true;
	}

	public boolean createTableValora() {
		try {
			//Comprabacion de que existe conexion
			if (conn == null || conn.isClosed() ) {
				openConnection();
			}
		} catch (SQLException e) {
			System.err.println("Error al comprobar conexion:");
			System.err.println(e.getMessage());
			return false;
		}
		// Comando sql para crear valora
		String tabla = 	"CREATE TABLE valora (" +
									"fecha DATE," +
									"valor INT," +
									"id_usuario INT," +
									"n_orden INT," +
									"id_serie INT," +
									"n_temporada INT," +
									"PRIMARY KEY(fecha, id_usuario, n_orden, id_serie, n_temporada),"+
									"FOREIGN KEY (id_serie) REFERENCES serie (id_serie) "+ 
									"    ON DELETE CASCADE ON UPDATE CASCADE,"+
									"FOREIGN KEY (n_orden, id_serie, n_temporada) REFERENCES capitulo (n_orden, id_serie, n_temporada) "+ 
									"    ON DELETE CASCADE ON UPDATE CASCADE);";
		Statement st;
			try {
				// llamadas para realizarlo en bd
				st = conn.createStatement();
				st.executeUpdate(tabla);
				st.close();
			} catch (SQLException e) {
				System.err.println("Error al crear la tabla Valora:");
				System.err.println(e.getMessage());
				return false;
			}
			
	
		return true;
	}

	public int loadCapitulos(String fileName) {
		try {
			//Comprabacion de que existe conexion
			if (conn == null || conn.isClosed() ) {
				openConnection();
			}
		} catch (SQLException e) {
			System.err.println("Error al comprobar conexion:");
			System.err.println(e.getMessage());
			return 0;
		}
		// Comando sql para insertar datos a capitulo
		String sql = "INSERT INTO capitulo (id_serie,n_temporada,n_orden,fecha_estreno,titulo,duracion) VALUES (?,?,?,?,?,?) ; ";
		//PreparedStatement porque se va a llamar muchas veces
		PreparedStatement pst ;
		try {
			pst= conn.prepareStatement(sql);
		} catch (SQLException e1) {
			System.err.println("Error en PreparedStatement de Capitulos:");
			System.err.println(e1.getMessage());
			return 0;
		}
		int num = 0;	// Num = numero de elementos añadidos
		BufferedReader br = null; 	// BufferedReader para leer el archivo csv
		String line ;		// String line para guardar la linea leida por br
		try {
			br = new BufferedReader(new FileReader(fileName));	 //leemos el archivo
			line = br.readLine();	// metemos en line la primera linea (nombre columnas)
			line = br.readLine();	// apuntamos a los primeros datos
			while (null!=line) {
				
	            String [] fields = line.split(";"); // Metemos en un array todos los datos 
	            line = br.readLine();	// line con los siguientes datos
	            try {
	           
	            	// Introducimos los datos con sus conversiones correspondientes
					pst.setInt(1,  Integer.parseInt(fields[0]));
					pst.setInt(2,  Integer.parseInt(fields[1]));
					pst.setInt(3,  Integer.parseInt(fields[2]));
					pst.setDate(4, Date.valueOf(fields[3]));
					pst.setString(5, fields[4]);
					pst.setInt(6,  Integer.parseInt(fields[5]));
					// Ejecutamos la orden
					int res = pst.executeUpdate();
					// si ha salido bien num++
					if (res == 1) {num++;}else {
						// Si no Borramos la tabla y la creamos de nuevo
						Statement st;
						try {
							// Borrar
							st = conn.createStatement();
							st.executeUpdate("DROP TABLE IF EXIST capitulo");
							st.close();
							// Crear
							createTableCapitulo();
						} catch (SQLException e) {
							System.err.println("Error al insertar datos en la tabla Capitulo: -1");
							System.err.println(e.getMessage());
							return 0;
						}
					}
				} catch (NumberFormatException | SQLException e) {
					System.err.println("Error al insertar datos en la tabla Capitulo: -2");
					System.err.println(e.getMessage());
					return 0;
				}	            
	         }
		} catch (IOException   e) {
			System.err.println("Error al leer el archivo capitulos.csv:");
			System.err.println(e.getMessage());
			return 0;
		}

	try {
		br.close();
	} catch (IOException e) {
		System.err.println("Error al cerrar capitulos.csv:");
		System.err.println(e.getMessage());
		return 0;}
		return num;
	}

	public int loadValoraciones(String fileName) {
	try {
		//Comprabacion de que existe conexion
		if (conn == null || conn.isClosed() ) {
			openConnection();
		}
	} catch (SQLException e) {
		System.err.println("Error al comprobar conexion:");
		System.err.println(e.getMessage());
		return 0;
	}
	// Comando sql para insertar valores en valora
	String sql = "INSERT INTO valora (id_serie,n_temporada,n_orden,id_usuario,fecha,valor) VALUES (?,?,?,?,?,?) ; ";
	// PreparedStatement ya que se van a meter muchos valores
	PreparedStatement pst ;
	try {
		pst= conn.prepareStatement(sql);
	} catch (SQLException e1) {
		System.err.println("Error en PreparedStatement de Valora:");
		System.err.println(e1.getMessage());
		return 0;
	}
	int num = 0; // Num acumula las lineas introducidas
	BufferedReader br = null;		// BufferedReader para leer el csv
	String line ;		// line para guardar lo leido
	try {
		br = new BufferedReader(new FileReader(fileName));	// leemos el documento
		line = br.readLine();	// linea  = columnas
		line = br.readLine();	// linea = dato1;dato2;dato3;...
		while (null!=line) {
			
            String [] fields = line.split(";"); // Quitamos el ; y lo guardamos en un array
            line = br.readLine();	// Pasamos de linea
            try {
            	
            	// Introducimos datos al comando
				pst.setInt(1,  Integer.parseInt(fields[0]));
				pst.setInt(2,  Integer.parseInt(fields[1]));
				pst.setInt(3,  Integer.parseInt(fields[2]));
				pst.setInt(4,  Integer.parseInt(fields[3]));
				pst.setDate(5, Date.valueOf(fields[4]));
				pst.setInt(6,  Integer.parseInt(fields[5]));
				// Ejecutamos el comando
				int res = pst.executeUpdate();
				if (res == 1) {num++;}
				
			} catch (NumberFormatException | SQLException e) {
				System.err.println("Error al insertar datos en la tabla Valora: -2");
				System.err.println(e.getMessage());
				return num;
			}	            
         }
	} catch (IOException   e) {
		System.err.println("Error al leer el archivo capitulos.csv:");
		System.err.println(e.getMessage());
		return 0;
	}

try {
	br.close();
} catch (IOException e) {
	System.err.println("Error al cerrar capitulos.csv:");
	System.err.println(e.getMessage());
	return 0;}
	return num;
}

	public String catalogo() {
		try {
			//Comprabacion de que existe conexion
			if (conn == null || conn.isClosed() ) {
				openConnection();
			}
		} catch (SQLException e) {
			System.err.println("Error al comprobar conexion:");
			System.err.println(e.getMessage());
			return null;
		}
		String resultado = "{"; // String donde se guardara la salida
		// Comando para Selecionar los capitulos
		String caps = "Select n_temporada, n_capitulos From temporada where id_serie=?;";
		// Comando para Selecionar el titulo e id_serie
		String nom = "Select titulo, id_serie From serie;";
		// Se realiza un prepared para completar caps
		PreparedStatement pst = null;
		// Se realiza un Statement para nom
		Statement st = null;
		
		// Se utilizan 2 ya que estan aninados
		try {
			st = conn.createStatement();
			pst = conn.prepareStatement(caps);
			ResultSet rs = st.executeQuery(nom);
			ResultSet rs1 = null;
			
			// Realizamos una primera llamada para cumplir con el formato de salida
			if (rs.next()) {
				resultado = resultado + rs.getString(1)+":[";
				pst.setInt(1, rs.getInt(2));
				rs1 = pst.executeQuery();
				if(rs1.next() ) {
					resultado = resultado+Integer.toString(rs1.getInt(2));
				}
				while(rs1.next()) {
					resultado = resultado+","+rs1.getInt(2);
				}
				
				resultado = resultado + "]";
		}
			
			while(rs.next()) {
				resultado = resultado +","+ rs.getString(1)+":[";
				pst.setInt(1, rs.getInt(2));
				rs1 = pst.executeQuery();
				if(rs1.next()) {
					resultado = resultado+rs1.getInt(2);
				}
				while(rs1.next()) {
					resultado = resultado+","+rs1.getInt(2);
				}
			
				resultado = resultado + "]";
				
			}
			// Cerramos statements y resultsets
			rs1.close();
			pst.close();	
			rs.close();
			st.close();
		} catch (SQLException e) {
			System.err.println("Error en busqueda del catalogo:");
			System.err.println(e.getMessage());
			return null;
		}		
		resultado = resultado + "}";
		return resultado;
	}
	
	public String noHanComentado() {
		try {
			//Comprabacion de que existe conexion
			if (conn == null || conn.isClosed() ) {
				openConnection();
			}
		} catch (SQLException e) {
			System.err.println("Error al comprobar conexion:");
			System.err.println(e.getMessage());
			return null;
		}
		// Comando de busqueda
		String nocom= "Select u.apellido1,u.apellido2,u.nombre,u.id_usuario FROM usuario u LEFT JOIN comenta c ON u.id_usuario = c.id_usuario WHERE c.id_usuario IS NULL order by apellido1 ASC, apellido2 ASC, nombre ASC;";
		String resultado = "["; // String de salida
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(nocom);
			if(rs.next()) {
				resultado = resultado + rs.getString(3)+" "+rs.getString(1)+" "+rs.getString(2);
			}
			while(rs.next()) {
				resultado = resultado + ", "+rs.getString(3)+" "+rs.getString(1)+" "+rs.getString(2);
			}
			// Cerrar instancias
			rs.close();
			st.close();
		} catch (SQLException e) {
			System.err.println("Error al mostrar los usuarios que no han comentado:");
			System.err.println(e.getMessage());
			return null;
		}
		resultado = resultado + "]";
		return resultado;
	}

	public double mediaGenero(String genero) {
		try {
			//Comprabacion de que existe conexion
			if (conn == null || conn.isClosed() ) {
				openConnection();
			}
		} catch (SQLException e) {
			System.err.println("Error al comprobar conexion:");
			System.err.println(e.getMessage());
			return -2.0;
		}
		double res= 0.0; // Res es la variable de salida
		String gen = "Select id_genero FROM genero where descripcion =?;"; // gen es el comando para sacar el id del genero
		// val Comando para hallar la media
		String val = "SELECT AVG(v.valor) FROM valora v INNER JOIN pertenece p ON v.id_serie = p.id_serie WHERE id_genero = ?;";
		int id_gen; // almacenara el id de gen
		PreparedStatement pst ;	// PreparedStatement para rellenar los paramentros
		try {
			pst = conn.prepareStatement(gen);
			pst.setString(1, genero);
			
			ResultSet rs = pst.executeQuery();
			
			// Caso no existe el genero
			if(!rs.next()) {
				System.out.println("Genero introducido no existe:");
				return -1.0;
			}
			
			id_gen = rs.getInt(1);
			pst.close();  // Cerramos para efectuar nuevo comando
			pst = conn.prepareStatement(val);
			pst.setInt(1, id_gen);
			rs.close(); // Cerramos para almacenar nueva tabla
			rs  = pst.executeQuery();
			rs.next();
			res = rs.getDouble(1);	// Obtenemos la media
			
			// Cerrar instancias
			rs.close();
			pst.close();
			
			// Tratamiento de error
		} catch (SQLException e) {
			System.err.println("Error al calcular la valoracion media del genero:");
			System.err.println(e.getMessage());
			return -2.0;
		}

		return res;
	}
	
	public double duracionMedia(String idioma) {
		try {
			//Comprabacion de que existe conexion
			if (conn == null || conn.isClosed() ) {
				openConnection();
			}
		} catch (SQLException e) {
			System.err.println("Error al comprobar conexion:");
			System.err.println(e.getMessage());
			return -2.0;
		}
		
		PreparedStatement pst;
		String id = "Select id_serie From serie WHERE idioma = ? LIMIT 1;";	//Comando para obtener la id de la serie
		String dur = 	"select avg(duracion) From capitulo c left JOIN valora v USING(n_orden,id_serie,n_temporada)" + 	
								"WHERE v.valor is NULL and id_serie = ?;";	// Comando para hallar la media
		int id_serie;		// almacena el id de serie
		double res = -1.0;	// SALIDA
		try {
			pst = conn.prepareStatement(id);
			pst.setString(1, idioma);
			ResultSet rs = pst.executeQuery();
			
			// Caso el idioma no este en la bd
			if(!rs.next()) {
				System.out.println("No hay capitulos que cumplan estas condiciones");
				return -1.0;
			}
			id_serie = rs.getInt(1);
			rs.close();	// Cerramos para meter nueva tabla
			pst.close();	// Cerramos para meter nuevo comando
			pst = conn.prepareStatement(dur);
			pst.setInt(1, id_serie);
			rs = pst.executeQuery();
			rs.next();
			res = rs.getDouble(1);	// Obtenemos media
			
			// Cerramos instancias
			rs.close();
			pst.close();
			
			//Tratamiento de errores
		} catch (SQLException e) {
			System.err.println("Error al calcular la duracion media:");
			System.err.println(e.getMessage());
			return -2.0;
		}
		
		
		return res;
	}

	public boolean setFoto(String filename) {
		try {
			//Comprabacion de que existe conexion
			if (conn == null || conn.isClosed() ) {
				openConnection();
			}
		} catch (SQLException e) {
			System.err.println("Error al comprobar conexion:");
			System.err.println(e.getMessage());
			return false;
		}
		
		// Comandos para tratar casos especificos
		String cas1 = "Select COUNT(id_usuario) FROM usuario WHERE apellido1 = 'Cabeza'";	// Muchas personas apellidadas Cabeza
		String cas2 = "Select fotografia FROM usuario WHERE apellido1 = 'Cabeza'";	// Ya tiene foto
		
		// Comando para poner foto
		String update = "UPDATE usuario SET fotografia = ? WHERE apellido1 = 'Cabeza';";
		FileInputStream fis = null;
		File file = new File(filename);
		try {
			PreparedStatement pst = conn.prepareStatement(cas1);
			ResultSet rs = pst.executeQuery();
			rs.next();
			
			// CASO 1 (Muchos nombres)
			if(rs.getInt(1)!=1) {
				System.out.println("Existen varios usuarios con el apellido Cabeza");
				return false;
			}
			rs.close();	// nueva tabla
			pst.close();	// nuevo comando
			pst = conn.prepareStatement(cas2);
			rs = pst.executeQuery();
			rs.next();
			
			// Caso 2 (YA tiene foto)
			if(rs.getBlob(1) != null) {
				System.out.println("El usuario cabeza ya tiene foto de perfil");
				return false;
			}
			rs.close();	// nueva tabla
			pst.close();	// nuevo comando
			pst = conn.prepareStatement(update);
			// cargamos foto a FileInputStream
			fis = new FileInputStream(file);
			pst.setBinaryStream(1, fis, (int) file.length());	// Se carga en el comando
			pst.executeUpdate();		// Se ejecuta
			pst.close();
			
			
			// Tratamiento de errores
		} catch (SQLException  e) {
			System.err.println("Error en insertar la imagen:");
			System.err.println(e.getMessage());
			return false;
		} catch (FileNotFoundException e) {
			System.err.println("Error al cargar la imagen:");
			System.err.println(e.getMessage());
			return false;
		}
		
		return true;
	}

}
