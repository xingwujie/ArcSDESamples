package sde;

import com.esri.sde.sdk.client.SeConnection;
import com.esri.sde.sdk.client.SeException;

public class SdeConnection {
	public static void print(Object obj){
		System.out.println(obj);
	}
	public static void getConnectionTest(){
		SeConnection conn = null;
        String server     = "3c4e53a46eaf4e3";
        int instance      = 5151;
        String database   = "orcl";
        String user       = "sde";
        String password   = "liulin";
        try {
            conn = new SeConnection(server, instance, database, user, password);
            print(conn.getSdeDbaName());
        }catch (SeException e) {
            e.printStackTrace();
            System.out.println("failed");
            return ;
        }
        System.out.println("success");
	}
	public static SeConnection getSeConnection(){
		SeConnection conn = null;
        String server     = "3c4e53a46eaf4e3";
        int instance      = 5151;
        String database   = "orcl";
        String user       = "sde";
        String password   = "liulin";
        try {
            conn = new SeConnection(server, instance, database, user, password);
            print(conn.getSdeDbaName());
        }catch (SeException e) {
            e.printStackTrace();
            System.out.println("failed");
            return null;
        }
        System.out.println("success");
        return conn;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		getConnectionTest();
	}

}
