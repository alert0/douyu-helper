package com.dy.bulletscreen.utils;

import com.dy.bulletscreen.client.DyBulletScreenClient;
import com.dy.bulletscreen.msg.ToolSub;

/**
 * @Summary: 服务器心跳保持线程
 * @author: FerroD     
 * @date:   2016-3-12   
 * @version V1.0
 */
public class ClientDanmu extends Thread {
	
	
	int roomId;int groupid; ToolSub path;
	
	
	public ClientDanmu(int roomId,int groupid,ToolSub path){
		this.roomId =roomId;
		this.groupid =groupid;
		this.path = path;
	}
	
	
    @Override
    public void run()
    {
    	 DyBulletScreenClient danmuClient = DyBulletScreenClient.create();
		 danmuClient.setToolSub(path);
		 while(true) {
	            
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
