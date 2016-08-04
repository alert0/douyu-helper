package com.dy.bulletscreen.app;

import com.dy.bulletscreen.client.DyBulletScreenClient;
import com.dy.bulletscreen.msg.ToolSub;
import com.dy.bulletscreen.utils.KeepAlive;
import com.dy.bulletscreen.utils.KeepGetMsg;

/**
 * @summary: 弹幕Demo程序启动类 
 * @author: FerroD     
 * @date:   2016-3-12   
 * @version V1.0
 */
public class DyBulletScreenApplication
{
	//设置需要访问的房间ID信息
//	private static final int roomId = 67373;
//	
//	//弹幕池分组号，海量模式使用-9999
//	private static final int groupId = -9999;
//	
	
	
	
	public static  void create(int roomId,int groupid, ToolSub path){
		 DyBulletScreenClient danmuClient = DyBulletScreenClient.create();
		 danmuClient.setToolSub(path);
		 while(true ) {
	            
	        	if(!danmuClient.getReadyFlag() ) {
	            	//保持弹幕服务器心跳
	        		
	        		danmuClient.init(roomId, groupid) ;
	                KeepAlive keepAlive = new KeepAlive(danmuClient);
	                keepAlive.start();
	                
	                //获取弹幕服务器发送的所有信息
	                KeepGetMsg keepGetMsg = new KeepGetMsg(danmuClient);
	                keepGetMsg.start();
	                
	                
	                try {
						Thread.sleep(5000L);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}

	        }
	}
}
	
//	
//	public static void main(String[] args)
//	{
//		//初始化弹幕Client
//        DyBulletScreenClient danmuClient = DyBulletScreenClient.create();
//        //设置需要连接和访问的房间ID，以及弹幕池分组号
//       
//        
//        while(true ) {
//            
//        	if(!danmuClient.getReadyFlag() ) {
//            	//保持弹幕服务器心跳
//        		
//        		danmuClient.init(roomId, groupId) ;
//                KeepAlive keepAlive = new KeepAlive(danmuClient);
//                keepAlive.start();
//                
//                //获取弹幕服务器发送的所有信息
//                KeepGetMsg keepGetMsg = new KeepGetMsg(danmuClient);
//                keepGetMsg.start();
//                
//                
//                try {
//					Thread.sleep(5000L);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//        	}
//
//        }
//        
//
//	}
//}