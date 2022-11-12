package STOREDFUNCTIONTEST;

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

public class SFTESTING {
	
	Connection con=null;
	Statement stmt=null;
	Statement stmt1=null;
	ResultSet rs1;
	ResultSet rs2;
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
	@Test(priority=1)
	
	void Test_ifSFEXIST() throws SQLException
	{
		stmt=con.createStatement();
		rs1=stmt.executeQuery("SHOW FUNCTION STATUS WHERE DB='CLASSICMODELS'");
		rs1.next();
		
		Assert.assertEquals(rs1.getString("Name"),"CustomerLevel");
		
	}
	
	@Test(priority=2)
	
	void Test_customerlvl() throws SQLException
	{
		rs1=con.createStatement().executeQuery("SELECT customername, CUSTOMERLEVEL(CREDITLIMIT) AS customerlevel FROM CUSTOMERS");
		
		rs2=con.createStatement().executeQuery("SELECT customername, CASE WHEN CREDITLIMIT > 50000 THEN 'Platinum' WHEN CREDITLIMIT < 50000 AND CREDITLIMIT > 10000 THEN 'GOLD' WHEN CREDITLIMIT < 10000 THEN 'SILVER' END AS customerlevel from customers");
		
		Assert.assertEquals(compareresult(rs1,rs2),true);
		
	}
	
	@Test(priority=3)
	
	void test_cuslevel() throws SQLException
	{
		cstmt=con.prepareCall("{Call cuslimit(?,?)}");
		cstmt.setInt(1, 131);
		cstmt.registerOutParameter(2, Types.VARCHAR);
		
		cstmt.executeQuery();
		
		String cuslvl=cstmt.getString(2);
		
		rs1=con.createStatement().executeQuery("SELECT customername, CASE WHEN CREDITLIMIT > 50000 THEN 'Platinum' WHEN CREDITLIMIT < 50000 AND CREDITLIMIT > 10000 THEN 'GOLD' WHEN CREDITLIMIT < 10000 THEN 'SILVER' END AS customerlevel from customers where customernumber=131");
		rs1.next();
		
		String exp_cuslvl=rs1.getString("CustomerLevel");
		
		Assert.assertEquals(cuslvl, exp_cuslvl);
	}
	public boolean compareresult(ResultSet resultset1, ResultSet resultset2) throws SQLException
	{
		while(resultset1.next())
		{
			resultset2.next();
			int count=resultset1.getMetaData().getColumnCount();
			for(int i=1; i<=count; i++)
			{
				if(!StringUtils.equals(resultset1.getString(i),resultset2.getString(i)))
				{
					return false;
				}
			}
		}
		return true;
	}

}
