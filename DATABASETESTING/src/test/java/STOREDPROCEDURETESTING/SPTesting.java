package STOREDPROCEDURETESTING;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SPTesting {
	
	private static final boolean True = false;
	private static final boolean False = false;
	Connection con=null;
	Statement stmt=null;
	ResultSet rs;
	ResultSet rs1;
	ResultSet rs2;
	Statement stmt1=null;
	CallableStatement cstmt;
	@BeforeClass
	void setup() throws SQLException
	{
		con=DriverManager.getConnection("jdbc:mysql://localhost:3306/classicmodels","root","Krisallen123");
	}
	@AfterClass
	
    void teardown() throws SQLException
    {
    	con.close();
    }

	
//	void test_storedProceduresExists() throws SQLException
//	{
//		stmt=con.createStatement();
//		rs=stmt.executeQuery("SHOW PROCEDURE STATUS WHERE Name='SELECTALLCUSTOMERS'");
//		rs.next();
//		Assert.assertEquals(rs.getString("Name"),"SELECTALLCUSTOMERS");
		
//	}
	//@Test(priority=1)
	void StoredPT2() throws SQLException
	{
		cstmt=con.prepareCall("{call SELECTALLCUSTOMERSBYCITYANDPIN(?,?)}");
		cstmt.setString(1, "Singapore");
		cstmt.setString(2, "079903");
		rs1=cstmt.executeQuery();
		
		stmt=con.createStatement();
		rs2=stmt.executeQuery("SELECT *FROM Customers WHERE city='Singapore' AND postalcode='079903'");
		
		Assert.assertEquals(compareresult(rs1,rs2),true);
	}
	
	//@Test (priority=1)
	
	void shippingtime101() throws SQLException
	{
		cstmt=con.prepareCall("{call get_order_by_customer(?,?,?,?,?)}");
		cstmt.setInt(1, 141);
		cstmt.registerOutParameter(2,Types.INTEGER);
		cstmt.registerOutParameter(3,Types.INTEGER);
		cstmt.registerOutParameter(4,Types.INTEGER);
		cstmt.registerOutParameter(5,Types.INTEGER);
		
		cstmt.executeQuery();
		int shipped=cstmt.getInt(2);
		int cancelled=cstmt.getInt(3);
		int disputed=cstmt.getInt(4);
		int resolved=cstmt.getInt(5);
		
		//System.out.println(shipped+" "+cancelled+" "+disputed+" "+resolved);
		
		stmt=con.createStatement();
		rs=stmt.executeQuery("select (select count(*) as 'shipped' from orders where customernumber = 141 and status = 'SHIPPED') as SHIPPED, (select count(*) as 'cancelled' from orders where customernumber = 141 and status = 'CANCELLED') as CANCELLED,(select count(*) as 'disputed' from orders where customernumber = 141 and status = 'disputed') as DISPUTED,(select count(*) as 'resolved' from orders where customernumber = 141 and status = 'resolved') as RESOLVED");
		rs.next();
		int exp_shipped=rs.getInt("shipped");
		int exp_cancelled=rs.getInt("cancelled");
		int exp_disputed=rs.getInt("disputed");
		int exp_resolved=rs.getInt("resolved");
		
		if(shipped==exp_shipped && cancelled==exp_cancelled && disputed==exp_disputed && resolved==exp_resolved) 
			Assert.assertTrue(true);
		else
			Assert.assertTrue(false);
		
	}
	
	@Test(priority=1)
	void shippingtime10101() throws SQLException
	{
		cstmt=con.prepareCall("{call shippingtime101(?,?)}");
		cstmt.setInt(1, 112);
		cstmt.registerOutParameter(2, Types.VARCHAR);
		cstmt.executeQuery();
		
		String shipping=cstmt.getString(2);
		
		stmt=con.createStatement();
		rs1=stmt.executeQuery("SELECT country, CASE when country = 'USA' THEN '2 DAYS SHIPPING' when country = 'CANADA' THEN '3 DAYS SHIPPING' else '5 DAYS SHIPPING' END AS SHIPPINGTIME FROM customers where customerNUMBER = 112");
		rs1.next();
		
		String exp_shipping=rs1.getString("ShippingTime");
		
		Assert.assertEquals(shipping,exp_shipping);
		
		//if(shipping==exp_shipping)
		//	Assert.assertTrue(True);
		//else
		//	Assert.assertTrue(False);
	}
		
}
