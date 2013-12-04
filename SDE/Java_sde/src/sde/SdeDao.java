package sde;

import com.esri.sde.sdk.client.SDEPoint;
import com.esri.sde.sdk.client.SeColumnDefinition;
import com.esri.sde.sdk.client.SeConnection;
import com.esri.sde.sdk.client.SeCoordinateReference;
import com.esri.sde.sdk.client.SeException;
import com.esri.sde.sdk.client.SeFilter;
import com.esri.sde.sdk.client.SeInsert;
import com.esri.sde.sdk.client.SeLayer;
import com.esri.sde.sdk.client.SeQuery;
import com.esri.sde.sdk.client.SeRow;
import com.esri.sde.sdk.client.SeShape;
import com.esri.sde.sdk.client.SeShapeFilter;
import com.esri.sde.sdk.client.SeSqlConstruct;
import com.esri.sde.sdk.client.SeTable;
import com.esri.sde.sdk.geom.SeCoordRef;
import com.esri.sde.sdk.geom.SeGeometryException;
import com.esri.sde.sdk.geom.SePoint;

public class SdeDao {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
//			distance();
			SeConnection conn = SdeConnection.getSeConnection();
//			if(conn!=null){
//				insert(conn,"taxi_point_cloud",118.90, 30.23);
//			}
			String tableName = "taxi_point_cloud";
			String spatialColumn = "shape";
			
			SeCoordinateReference cr = getLayerSeCoordinateReference(conn,tableName);
			double lon=118.9;
			double lat=31.23;
			double distance=LatLngUtil.m2gps(1000);
			SeFilter[] filters = generateFilters(cr, lon, lat, distance, tableName, spatialColumn);
			
			spatialQuery(conn,tableName,filters);
			
		} catch (SeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void print(Object obj){
		System.out.println(obj);
	}
	/**
	 * 计算距离
	 * @throws SeException
	 * @throws SeGeometryException
	 */
	public static void distance() throws SeException, SeGeometryException{
		SeCoordRef cr = new SeCoordRef();
		SePoint point = new SePoint(cr,0,0);
		
		SePoint point2 = new SePoint(cr,1,2);
		double distance = point2.distance(point);
		
		System.out.println(distance);
	}
	/**
	 * 计算距离
	 * @throws SeException
	 * @throws SeGeometryException
	 */
	public static void distance(SeCoordinateReference cr, double lon1, double lat1, double lon2, double lat2) throws SeException, SeGeometryException{
		
		SeShape shape1 = generateSeShape(cr, lon1, lat1);
		
		SeShape shape2 = generateSeShape(cr, lon2, lat2);
        
//		double distance = shape1.distance(shape2);
//		
//		SePoint point = new SePoint(cr,113.977,30.656);
//		
//		SePoint point2 = new SePoint(cr,114.709,30.362);
//		double distance = point2.distance(point);
//		
//		System.out.println(distance);
	}
	
	/**
	 * @param conn
	 * @param layerName, FOR INTANCE: "taxi_point_cloud"
	 * @throws SeException 
	 */
	public static void insert(SeConnection conn, String layerName, double lon, double lat) throws SeException{
		
		SeLayer layer = new SeLayer( conn, layerName, "SHAPE" );
		
		String[] cols = new String[3];

        cols[0] = new String("lon");
        cols[1] = new String("lat");

        cols[2] = layer.getSpatialColumn();
        
        

//        2.使用当前的连接句柄Connection conn,创建一个SeInsert对象.
//        	这将建立一个从客户端到服务器的Insert数据流(Stream)。
//        	然后根据表或图层的列名来插入数据，设置SeInsert对象为可写模式(setWriteMode)。

        SeInsert insert = new SeInsert(conn);

        insert.intoTable(layer.getName(),cols);

        insert.setWriteMode(true);

		//3.获取SeInsert对象中将要添加的SeRow对象。
		//	然后设置要插入的数据，这一步用到SeRow.set*方法。
		//	利用第一步中确定的索引值来作为set*方法中的columnPosition参数。

        SeRow row = insert.getRowToSet();
        
        row.setDouble(0, lon);
        row.setDouble(1, lat);
        
        SeCoordinateReference scr = layer.getCoordRef();
        SeShape shape = generateSeShape(scr, lon,lat);
        row.setShape(2,shape);

        //4.调用SeInsert对象的execute()方法，将前面设置好的SeRow对象插入到图层中。
        //最后关闭Insert数据流(Stream)结束这次插入的操作。如果不再使用到连接句柄Connection conn，也顺便把连接关闭。

        insert.execute();

        insert.close();
	}
	/**
	 * 
	 * @param scr
	 * @param lon
	 * @param lat
	 * @return SeShape对象，具有明确的坐标参考系
	 * @throws SeException
	 */
	public static SeShape generateSeShape(SeCoordinateReference scr, double lon, double lat) throws SeException{
		SeShape shape = new SeShape(scr);
        SDEPoint[] pnts = new SDEPoint[1];
        pnts[0] = new SDEPoint(lon, lat);
        shape.generatePoint(1,pnts);
		return shape;
	}
	/**
	 * 生成缓冲区对象
	 * @param scr
	 * @param lon
	 * @param lat
	 * @param distance
	 * @return
	 * @throws SeException
	 */
	public static SeShape generateBufferSeShape(SeCoordinateReference scr, double lon, double lat, double distance) throws SeException{
		SeShape shape = generateSeShape(scr, lon, lat);
		SeShape buffer = shape.generateBuffer(distance, 5000);
		return buffer;
	}
	
	/**
	 * 生成空间过滤器
	 * @param shape
	 * @param layerName
	 * @param spatialColumn
	 * @return
	 */
	public static SeFilter[] generateFilters(SeShape shape, String layerName, String spatialColumn){
		
		SeFilter[] filters = new SeFilter[1];
		
		SeFilter filter = new SeShapeFilter(layerName, spatialColumn, shape, SeShapeFilter.METHOD_ENVP);
		filters[0] = filter;
		
		return filters;		
	}
	
	/**
	 * 生成空间过滤器
	 * @param scr
	 * @param lon
	 * @param lat
	 * @param distance
	 * @param layerName
	 * @param spatialColumn
	 * @return
	 */
	public static SeFilter[] generateFilters(SeCoordinateReference scr, double lon, double lat, double distance, String layerName, String spatialColumn){
		
		SeFilter[] filters = null;
		SeShape shape;
		try {
			shape = generateBufferSeShape(scr, lon, lat, distance);
			filters = generateFilters(shape,layerName,spatialColumn);
		} catch (SeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return filters;		
	}
	
	/**
	 * 获取图层的空间坐标系对象
	 * @param conn
	 * @param tableName
	 * @return SeCoordinateReference(空间坐标系)对象
	 * @throws SeException
	 */
	public static SeCoordinateReference getLayerSeCoordinateReference(SeConnection conn,String tableName) throws SeException{
		SeLayer layer = new SeLayer(conn,tableName,"SHAPE");
		return layer.getCoordRef();
	}

	/**
	 * 空间条件查询
	 * @param conn
	 * @param tableName
	 * @param filters
	 */
	public static void spatialQuery(SeConnection conn,String tableName,SeFilter[] filters)
	{
		try {
			
			if(conn==null){
				System.err.print("\n连接对象获取失败\n");
				return ;
			}
			SeLayer layer = new SeLayer(conn,tableName,"SHAPE");

			SeTable table = new SeTable(conn, tableName);
			SeColumnDefinition[] tableDef = table.describe();
			String[] cols = new String[tableDef.length];
			for (int j = 0; j < cols.length; j++)
			{
				cols[j] = tableDef[j].getName();
			}

			SeSqlConstruct sqlCons = new SeSqlConstruct(layer.getName());
			SeQuery query = new SeQuery(conn, cols, sqlCons);
			query.prepareQuery();
			query.setSpatialConstraints(SeQuery.SE_OPTIMIZE, false,filters);
			query.execute();
			
			SeRow row =query.fetch();
			while(row!=null)
			{
				System.out.println(row.getObject(0).toString());
				
				row =query.fetch();
			}
			
			query.close();
		}
		catch (Exception ex) {
			
			ex.printStackTrace();
		}
	}
}
