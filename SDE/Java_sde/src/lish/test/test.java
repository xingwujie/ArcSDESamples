package lish.test;
import com.esri.sde.sdk.client.*;
import com.esri.sde.sdk.client.SeTable.*;
import java.util.Vector;
public class test {
	
	private static SeConnection conn = null;

	private static String server = "3c4e53a46eaf4e3";
	private static String instance = "5151";
	private static String database = "orcl";
	private static String username = "sde";
	private static String password = "liulin";
	//获得ArcSDE连接
	private static SeConnection getConn() {
		if (conn == null) {
			try {
				conn = new SeConnection(server, instance, database, username,
						password);
			} catch (SeException ex) {
				ex.printStackTrace();
			}
			
		}
	    
		return conn;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		GetArcSDEInfo(); 
	}
	//ArcSDE管理
	public static void GetArcSDEInfo()
	{
		try 
		{ 
			SeInstance instance = new SeInstance(server, "5353");
			SeInstance.SeInstanceStatus status = instance.getStatus(); 
			System.out.println("连接数："+status.getNumConnections());
			System.out.println("可以连接："+status.isAccepting()); 
			
			System.out.println("------------------------------------------------");
			
			SeInstance.SeInstanceConfiguration config = instance.getConfiguration();
			System.out.println("最大连接数："+config.getMaxConnections());
			
			System.out.println("------------------------------------------------");
			
			SeInstance.SeInstanceStats[] stats=instance.getStats();
			for (int i=0;i<stats.length;i++)
			{
				System.out.println("操作数："+stats[i].getOperationCount());
			}
			
			System.out.println("------------------------------------------------");
			
			SeInstance.SeInstanceUsers[] users=instance.getUsers();
			 for(int j=0;j<users.length;j++)
			 {
				 System.out.println("用户名："+users[j].getUserName());
				 System.out.println("系统名："+users[j].getSysName());
				 System.out.println("服务器开始时间："+users[j].getServerStartTime());
				 System.out.println("服务器PID："+users[j].getServerPid());
				 System.out.println("*****************************");
			 }
			 
			 System.out.println("------------------------------------------------");
			 System.out.println("系统名："+instance.getServerName());
			 System.out.println("------------------------------------------------");
			 
			 System.out.println("------------------------------------------------");
			 SeInstance.SeInstanceTableLocks[] tablelocks=instance.getTableLocks();
			 for(int i=0;i<tablelocks.length;i++)
			 {
				 System.out.println("表级别锁类型："+tablelocks[i].getLockType());
				 System.out.println("表级别锁PID："+tablelocks[i].getPid());
				 System.out.println("表级别锁注册ID："+tablelocks[i].getRegistrationId());
				 System.out.println("*****************************");
			 }

			 System.out.println("------------------------------------------------");
			 
		}
			
		catch (SeException e) { e.printStackTrace(); }
	}
	//空间条件查询
	public static void SpatialQuery()
	{
		try {
			SeConnection conn = getConn();
			SeLayer layer = new SeLayer(conn,"quxian","SHAPE");

			SeTable table = new SeTable(conn, "quxian");
			SeColumnDefinition[] tableDef = table.describe();
			String[] cols = new String[tableDef.length];
			for (int j = 0; j < cols.length; j++)
			{
				cols[j] = tableDef[j].getName();
			}
			SeCoordinateReference cr = layer.getCoordRef();
			SeFilter[] filters = new SeFilter[1];
			SeShape shape = new SeShape(cr); 
			shape.generateFromText("POLYGON((125.091 44.324,125.070 43.428,125.887 44.027,125.091 44.324))");
			
			//SDEPoint pt = new SDEPoint(125.091,44.324);
			//shape.generatePoint(1, new SDEPoint[] { pt });
			SeFilter filter = new SeShapeFilter(layer.getName(), layer
					.getSpatialColumn(), shape, SeShapeFilter.METHOD_ENVP);
			filters[0] = filter;

			SeSqlConstruct sqlCons = new SeSqlConstruct(layer.getName());
			SeQuery query = new SeQuery(conn, cols, sqlCons);
			query.prepareQuery();
			query.setSpatialConstraints(SeQuery.SE_OPTIMIZE, false,filters);
			query.execute();
				SeRow row =query.fetch();
				while(row!=null)
				{
					System.out.println(row.getObject(1).toString());
					
					row =query.fetch();
				}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	//属性条件查询 
	public static void AttributeQuery()
	{
		try {
			SeConnection conn = getConn();
			SeTable table = new SeTable(conn, "quxian");
			SeColumnDefinition[] tableDef = table.describe();
			String[] cols = new String[tableDef.length];
			for (int j = 0; j < cols.length; j++)
			{
				cols[j] = tableDef[j].getName();
			}
			SeSqlConstruct sqlCons = new SeSqlConstruct("quxian");
			sqlCons.setWhere("CITY='长春市'");
			SeQuery query = new SeQuery(conn, cols, sqlCons);
			
			query.prepareQuery(cols,sqlCons);
				query.execute();
				SeRow row =query.fetch();
				while(row!=null)
				{
					System.out.println(row.getObject(1).toString());
					System.out.println(row.getObject(4).toString());
					row =query.fetch();
				}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	//普通查询
	public static void CommonQuery()
	{
		try {
			SeConnection conn = getConn();
			SeTable table = new SeTable(conn, "conn");
			SeColumnDefinition[] tableDef = table.describe();
			String[] cols = new String[tableDef.length];
			for (int j = 0; j < cols.length; j++)
			{
				cols[j] = tableDef[j].getName();
			}
			SeSqlConstruct sqlCons = new SeSqlConstruct("conn");
			SeQuery query = new SeQuery(conn, cols, sqlCons);

				SeQueryInfo queryInfo = new SeQueryInfo();
				queryInfo.setQueryType(SeQueryInfo.SE_QUERYTYPE_ATTRIBUTE_FIRST);
				queryInfo.setColumns(cols);
				queryInfo.setConstruct(sqlCons);
				query.prepareQueryInfo(queryInfo);
				query.execute();
				SeRow row =query.fetch();
				while(row!=null)
				{
					System.out.println(row.getObject(0).toString());
					row =query.fetch();
				}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	//获得ArcSDE版本信息
	public static void GetVersion()
	{
		SeConnection conn =getConn();
		SeRelease release=conn.getRelease();
		System.out.println(release.getBugFix());
		System.out.println(release.getDesc());
		System.out.println(release.getRelease());
		System.out.println(release.getMajor());
		System.out.println(release.getMinor());
		
	}
}	