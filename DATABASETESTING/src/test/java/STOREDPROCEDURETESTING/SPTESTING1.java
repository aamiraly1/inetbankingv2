package STOREDPROCEDURETESTING;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SPTESTING1 {
	
	Connection con=null;
	ResultSet rs1;
	ResultSet rs2;
	Statement stmt=null;
	CallableStatement cstmt;
	CallableStatement cstmt1;
	@BeforeClass
	
	void setup1() throws SQLException
	{
		con=DriverManager.getConnection("jdbc:mysql://localhost:3306/classicmodels","root","Krisallen123");
		
	}
	
	@AfterClass
	
	void teardown1() throws SQLException
	{
		con.close();
	}
	
	@Test
	void StoredPT2() throws SQLException
	{
		cstmt=con.prepareCall("{CALL SELECTALLCUSTOMERSBYCITY(?)}");
		cstmt.setString(1, "Singapore");
		rs1=cstmt.executeQuery();
		
		stmt=con.createStatement();
		rs2=stmt.executeQuery("SELECT *FROM Customers WHERE city='Singapore'");
		
		Assert.assertEquals(compareresult(rs1,rs2),true);
	}
	
	public boolean compareresult(ResultSet resultset1, ResultSet resultset2) throws SQLException
	{
		while(resultset1.next())
		{
			resultset2.next();
			int count=resultset1.getMetaData().getColumnCount();
			for(int i=0; i<=count; i++)
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
