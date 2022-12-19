import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//http://127.0.0.1:8080/api/sparemote
//http://155.248.162.153/api/sparemote
public class ProxyServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        
        try {
            //String forwardingAddress = "http://155.248.162.153/" + request.getRequestURI();
            String forwardingAddress = "https://func-mgt-dxp-b2e-ixsptesting.azurewebsites.net/" + request.getRequestURI();
            String data = getBody(request);
            System.err.println("REMOTE URI: " + forwardingAddress);
            System.err.println("REQ DATA: " + data);

            response.setContentType("application/json");
            response.setHeader("remote", "azure");
            PrintWriter writer = response.getWriter();
            writer.write(getResponseFromBackend(forwardingAddress, data));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getResponseFromBackend(String addr, String postData) {
        try {
            URL url = new URL(addr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("env", "testing");
            conn.setRequestProperty("Content-Length", Integer.toString(postData.length()));
            conn.setUseCaches(false);

            try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
                dos.writeBytes(postData);
            }

            StringBuilder content = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    content.append(line);
                    //System.out.println(line);
                }
            }
            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }

    }

    public static String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
    
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }
    
        body = stringBuilder.toString();
        return body;
    }
}