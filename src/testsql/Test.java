package testsql;

import java.sql.*;

public class Test {
	
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  // jdbc driver name
    static final String DB_URL = 
    		"jdbc:mysql://localhost:3306/DATABASE_HOMEWORK?useSSL=false&serverTimezone=UTC";
    // url = jdbc:mysql://$IP:$PORT/$DATABASENAME?useSSL=false&serverTimezone=UTC (for connector-java-8.0)
 
    static final String USER = "root";	// mysql username
    static final String PASS = "";		// mysql password
 
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try{
            // register jdbc driver
            Class.forName(JDBC_DRIVER);
        
            // open connection
            System.out.println("Connecting database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
        
            // execute query
            System.out.println(" create statement object...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT SNO, SNAME, SADDRESS FROM SUPPLIER";
            ResultSet rs = stmt.executeQuery(sql);
        
            // show query response
            while(rs.next()){
                // fetch by field
                int sno  = rs.getInt("SNO");
                String sname = rs.getString("SNAME");
                String saddress = rs.getString("SADDRESS");
    
                // print data
                System.out.print("SNO: " + sno);
                System.out.print(", Name: " + sname);
                System.out.print(", Address: " + saddress);
                System.out.print("\n");
            }
            // close
            rs.close();
            stmt.close();
            conn.close();
        }catch(SQLException se){
            // handle jdbc error
            se.printStackTrace();
        }catch(Exception e){
            // handle Class.forName error
            e.printStackTrace();
        }finally{
            // free resource
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }// do nothing
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }
}
