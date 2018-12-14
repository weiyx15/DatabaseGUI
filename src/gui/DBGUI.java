package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class DBGUI extends JFrame {

	private static final long serialVersionUID = 1L;	// default serial version UID
	private Vector<String> t_title = new Vector<String>();		// data title for dataModel
	private Vector<Vector<String>> t_data = new Vector<Vector<String>>();	// data list for dataModel
	DefaultTableModel dataModel = new DefaultTableModel(); 		// dataModel for table_viewer
	private JLabel label_query_item = new JLabel("Query item: ");
	private JLabel label_query_content = new JLabel("Query content: ");
	private final String[] str_query_item = {"查询1", "查询2", "查询3", "查询4", "查询5"};
	private JComboBox<String> combo_query_item = new JComboBox<String>(str_query_item);
	private JComboBox<String> combo_query_content = new JComboBox<String>();
	private JButton btn_query = new JButton("Query!");
	private JTable table_viewer = new JTable(dataModel);
	private Connection conn = null;
	
	public static void main(String args[])
	{
		DBGUI myapp = new DBGUI();
        myapp.setMinimumSize(new Dimension(750, 500));
        myapp.setVisible(true);
        myapp.setResizable(false);
	}
	
	public DBGUI()
	{
		super("Database Query GUI by weiyuxuan");
		
		// top: ComboBoxes and button
		JPanel control_panel = new JPanel();
		control_panel.setLayout(new FlowLayout());
		control_panel.add(label_query_item);
		control_panel.add(combo_query_item);
		control_panel.add(label_query_content);
		control_panel.add(combo_query_content);
		control_panel.add(btn_query);
		
		// Medium: Static text field
		JPanel text_panel = new JPanel();
		String[] texts = {"查询1：给出为工程(Query content)提供零件的全部供应商名",
						  "查询2：给出使用供应商(Query content)所供零件的全部工程名",
						  "查询3：给出使用颜色(Query content)零件的工程名",
						  "查询4：给出住在某地(Query content)而为不在该地、且不使用红色零件的工程提供零件的供应商名",
						  "查询5：给出使用供应商(Query content)供应的全部零件的工程名"	
		};
		StringBuilder sb = new StringBuilder();	// use StringBuilder to concatenate strings
		sb.append("<html>");				// use html in JLabel
		for (String text : texts)
		{
			sb.append(text);
			sb.append("<br>");
		}
		sb.append("</html>");
		text_panel.add(new JLabel(sb.toString()));
		
		// Bottom: Table viewer
		JPanel table_panel = new JPanel();
		table_panel.add(table_viewer.getTableHeader());
		table_panel.add(new JScrollPane(table_viewer));
		
		// Whole
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		container.add(control_panel);
		container.add(text_panel);
		container.add(table_panel);
		
		this.add(container);
		
		connectDB();
		
		// close sql connection when window closed
		this.addWindowListener(new WindowAdapter( ) {
			public void windowClosing(WindowEvent e)
			{
				try {
					if (conn != null)
					{
						conn.close();
					}
				} catch (SQLException se)
				{
					se.printStackTrace();
				}
				System.exit(0);
			}
		});
		
		setActionListener();
	}
	
	private void connectDB()
	{
		final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  // jdbc driver name
	    final String DB_URL = 
	    		"jdbc:mysql://localhost:3306/DATABASE_HOMEWORK?useSSL=false&serverTimezone=UTC";
	    final String USER = "root";
	    final String PASSWORD = "";
	    try {
		    Class.forName(JDBC_DRIVER);
		    conn = DriverManager.getConnection(DB_URL,USER, PASSWORD);
	    }
	    catch (SQLException se)  {
	    	System.out.println("SQLException encountered when creating connection");
	    	se.printStackTrace();
	    }
	    catch (ClassNotFoundException cnfe)
	    {
	    	cnfe.printStackTrace();
	    }
	}
	
	private void setActionListener()
	{
		combo_query_item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String sql = "SELECT PNO FROM PROJECT";
				String key;
				combo_query_content.removeAllItems();
				switch(combo_query_item.getSelectedIndex())
				{
				case 0:
					System.out.println("查询1");
					sql = "SELECT DISTINCT PNO FROM PROJECT";
					break;
				case 1:
					System.out.println("查询2");
					sql = "SELECT DISTINCT SNO FROM SUPPLIER";
					break;
				case 2:
					System.out.println("查询3");
					sql = "SELECT DISTINCT COLOR FROM COMPONENT";
					break;
				case 3:
					System.out.println("查询4");
					sql = "SELECT DISTINCT SADDRESS FROM SUPPLIER";
					break;
				case 4:
					System.out.println("查询5");
					sql = "SELECT DISTINCT SNO FROM SUPPLIER";
					break;
				default:
					System.out.println("查询1");
					sql = "SELECT DISTINCT PNO FROM PROJECT";
					break;
				}
				try {
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next())
					{
						key = rs.getString(1);	// 1 refers to the first column
						combo_query_content.addItem(key);
					}
					rs.close();
					stmt.close();
				} catch (SQLException e_stmt)
				{
					System.out.println("SQLException encountered when using SQL Statement");
					e_stmt.printStackTrace();
				}
			}
		});
		
		btn_query.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int line = 0;
				String sql = "SELECT PNO FROM PROJECT";
				String key = (String) combo_query_content.getSelectedItem();
				switch(combo_query_item.getSelectedIndex())
				{
				case 0:
					sql = "SELECT DISTINCT SNAME "
							+ "FROM SUPPLIER, SUPPLY "
							+ "WHERE SUPPLIER.SNO=SUPPLY.SNO AND SUPPLY.PNO=" + key;
					break;
				case 1:
					sql = " SELECT DISTINCT PNAME "
							+ "FROM PROJECT,SUPPLY "
							+ "WHERE PROJECT.PNO=SUPPLY.PNO AND SUPPLY.SNO=" + key;
					break;
				case 2:
					sql = "SELECT DISTINCT PNAME "
							+ "FROM PROJECT,SUPPLY,COMPONENT "
							+ "WHERE PROJECT.PNO=SUPPLY.PNO AND COMPONENT.CNO=SUPPLY.CNO AND COLOR="
							+ "\"" + key + "\"";
					break;
				case 3:
					sql = "SELECT SNAME FROM SUPPLIER WHERE SADDRESS="
							+ "\"" + key + "\"" + " AND SNO IN " + 
							"(SELECT SNO FROM SUPPLY WHERE PNO IN " + 
							"(SELECT PNO FROM PROJECT WHERE PADDRESS!="
							+ "\"" + key + "\"" 
							+ " AND NOT EXISTS "
							+ "(SELECT * FROM SUPPLY, COMPONENT "
							+ "WHERE PROJECT.PNO=SUPPLY.PNO AND COMPONENT.CNO=SUPPLY.CNO AND COLOR=\"red\")));";
					break;
				case 4:
					sql = "SELECT PNAME FROM PROJECT WHERE NOT EXISTS " + 
							"(SELECT CNO FROM SUPPLY WHERE SNO=" + key + " AND NOT EXISTS " + 
							"(SELECT * FROM SUPPLY WHERE SUPPLY.PNO=PROJECT.PNO));";
					break;
				default:
					sql = "SELECT DISTINCT SNAME "
							+ "FROM SUPPLIER, SUPPLY "
							+ "WHERE SUPPLIER.SNO=SUPPLY.SNO AND SUPPLY.PNO=" + key;
					break;
				}
				try {
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					t_title.clear();
					t_title.add(rs.getMetaData().getColumnName(1));
					t_data.clear();
					while (rs.next())
					{
						key = rs.getString(1);	// 1 refers to the first column
						t_data.add(new Vector<String>());
						t_data.get(line++).add(key);
					}
					rs.close();
					stmt.close();
					System.out.println(t_title);
					System.out.println(t_data);
					dataModel.setDataVector(t_data, t_title);	// refresh dataModel
				} catch (SQLException e_stmt)
				{
					System.out.println("SQLException encountered when using SQL Statement");
					e_stmt.printStackTrace();
				}
			}
		});
	}
	
}
