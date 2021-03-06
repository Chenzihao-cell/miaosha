package czihao.miaosha.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class HttpUtil {
	public static void main(String[] args) throws Exception{
		
		for(int i=0;i<10;i++) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					try {
						for(int i=0;i<10;i++) {
							URL url = new URL("http://192.168.220.130/index.html");
							HttpURLConnection conn = (HttpURLConnection)url.openConnection();
							InputStream in = conn.getInputStream();
							ByteArrayOutputStream bout  = new ByteArrayOutputStream();
							byte[] buff = new byte[1024];
							int len = 0;
							while((len = in.read(buff)) >= 0) {
								bout.write(buff, 0, len);
							}
							in.close();
							bout.close();
							System.out.println(bout.toString(StandardCharsets.UTF_8));
							Thread.sleep(3000);
						}
					}catch(Exception e) {
						
					}
				}
			});
			t.start();
		}
	}
}
