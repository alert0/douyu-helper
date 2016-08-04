package io.github.hengyunabc.douyuhelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dy.bulletscreen.app.DyBulletScreenApplication;
import com.dy.bulletscreen.msg.ToolSub;
import com.dy.bulletscreen.utils.ClientDanmu;

import ch.qos.logback.core.net.server.Client;

@Service
public class VideoDownloader {
	static final Logger logger = LoggerFactory.getLogger(VideoDownloader.class);

	ExecutorService executorService = Executors.newCachedThreadPool();
	CloseableHttpClient httpclient;

	@Autowired
	Manager manager;

	@PostConstruct
	public void init() {
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5 * 1000)
				.setConnectionRequestTimeout(5 * 1000).setSocketTimeout(5 * 1000).build();
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

		httpclient = HttpClients.custom().setConnectionManager(connManager).setDefaultRequestConfig(requestConfig)
				.build();
	}

	@PreDestroy
	public void destory() throws IOException {
		httpclient.close();
		executorService.shutdown();
	}

	public boolean isDownloading(String room) {
		return false;
	}

	public void addDownloadTask(final String room, final String url) {
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				// 先尝试获取下载的许可
				if (manager.getDownloadPermit(room) == false) {
					return;
				}

				long downloadedSize = 0;
				logger.info("开始下载房间：{}, url:{}", room, url);
				System.out.println("开始下载房间：" + room + "，url: " + url);
				ToolSub sub = null;
				try (CloseableHttpResponse response = httpclient.execute(new HttpGet(url))) {

					logger.info(response.getStatusLine().getStatusCode() + "");
					if (response.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = response.getEntity();
						if (entity != null) {
							InputStream inputStream = entity.getContent();
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd^hh-mm-ss");
							Paths.get("video", room).toFile().mkdirs();
							Path path = Paths.get("video", room, simpleDateFormat.format(new Date()) + ".flv");
							// Path path2 = Paths.get("video", room,
							// simpleDateFormat.format(new Date())+".ass" );
							File flvFile = path.toFile();

							// DyBulletScreenApplication.create(Integer.valueOf(room),-9999,
							// flvFile.getPath().replace("flv", "ass") );
							
							 sub = new ToolSub(flvFile.getPath().replace("flv", "ass") );

							new ClientDanmu(Integer.valueOf(room), -9999, sub).start();

							try (FileOutputStream outstream = new FileOutputStream(flvFile);) {
								byte[] buffer = new byte[128 * 1024];
								int size = 0;

								Date last = new Date();
								while ((size = inputStream.read(buffer)) != -1) {
									outstream.write(buffer, 0, size);
									downloadedSize += size;
									Date now = new Date();
									if (((now.getTime() - last.getTime()) / 1000) >= 5) {
										logger.info("开始下载房间：{}, 已下载大小:{}m", room, downloadedSize/ (1024* 1024) );
										System.out.println("正在下载房间：" + room + "，已下载大小: " + downloadedSize /(1024* 1024)+"m");
										last = now;
									}
								}
							}
						}
					}
				} catch (Throwable t) {
					logger.error("房间下载出错！room:" + room, t);
					System.out.println("下载房间出错！：" + room + "，url: " + url);
				} finally {
					manager.returnDownloadPermit(room);
					logger.info("结束下载房间：{}, url:{}", room, url);
					System.out.println("结束下载房间：" + room + "，url: " + url);
					try {
						sub.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

}
