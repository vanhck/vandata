import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Jonas on 23.06.2017.
 *
 * @author Jonas Kett
 * @version 1.0
 */
public class FileSender {
    private final static String prefix = "[File Sender] ";

    private static void printLine(String toPrint) {
        System.out.println(prefix + toPrint);
    }

    public static void sendFile(String filePath) {
        File file = new File(filePath);
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

        HttpPost httppost = new HttpPost("http://localhost:8080/file");
        MultipartEntity mpEntity = new MultipartEntity();

        ContentBody cbFile = new FileBody(file);
        mpEntity.addPart("file", cbFile);


        httppost.setEntity(mpEntity);
        printLine("executing request " + httppost.getRequestLine());
        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity resEntity = response.getEntity();

        printLine("executed request with status line: " +response.getStatusLine());
        if (resEntity != null) {
            try {
                printLine("received response: " + EntityUtils.toString(resEntity));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (resEntity != null) {
            try {
                resEntity.consumeContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        httpclient.getConnectionManager().shutdown();
    }
}