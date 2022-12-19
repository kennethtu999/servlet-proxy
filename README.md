# servlet-proxy，所有遠端請求都透過Tomcat轉送，略過COR的設定

使用方式：
1. 取得編輯好的ProxyServlet.class
  - 可以在stable目錄
  - 也可以編譯後在target/classes目錄
2. 將ProxyServlet.class放入 /apache-tomcat-10/webapps/ROOT/WEB-INF/classes 目錄內
3.  /apache-tomcat-10/webapps/ROOT/WEB-INF/web.xml 新增以下設定

  <servlet>
    <servlet-name>proxyservlet</servlet-name>
    <servlet-class>ProxyServlet</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>proxyservlet</servlet-name>
    <url-pattern>/api/sparemote/*</url-pattern>
  </servlet-mapping>

4. 重新啟動tomcat
  - EX: `./shutdown.sh && ./startup.sh`

