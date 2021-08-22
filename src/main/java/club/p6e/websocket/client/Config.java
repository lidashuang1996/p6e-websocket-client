package club.p6e.websocket.client;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * @author lidashuang
 * @version 1.0
 */
public class Config {

    /**
     * WebSocket 协议类型
     * @author lidashuang
     * @version 1.0
     */
    public enum Agreement {
        /**
         * WS 协议
         */
        WS,
        /**
         * WSS 协议
         */
        WSS;
    }

    /**
     * WebSocket 版本号
     * @author lidashuang
     * @version 1.0
     */
    public enum Version {
        /**
         * 00 版本
         */
        V00,
        /**
         * 07 版本
         */
        V07,
        /**
         * 08 版本
         */
        V08,
        /**
         * 13 版本
         */
        V13,
        /**
         * 未知版本
         */
        UNKNOWN
    }

    /**
     * 自定义证书
     * @author lidashuang
     * @version 1.0
     */
    public static class Certificate {
        /** 证书路径 */
        private String path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    /**
     * 封装 Cookies 的对象
     * @author LiDaShuang
     * @version 1.0.0
     */
    public static class Cookie {
        /** Cookie Name */
        private final String name;
        /** Cookie Value */
        private final String value;

        /**
         * 构造方法初始化
         * @param name Cookie name
         * @param value Cookie value
         */
        public Cookie(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return name + "=" + value + "; ";
        }
    }

    /** HTTP 请求头中 Cookie 的名称  */
    private static final String COOKIE = "Cookie";

    /**
     * 端口
     */
    private int port;
    /**
     * 主机地址
     */
    private String host;
    /**
     * 协议类型
     */
    private Agreement agreement;
    /**
     * 请求路径
     */
    private String path;
    /**
     * 请求的参数
     */
    private String param;
    /**
     * uri
     */
    private URI uri;
    /**
     * 版本号
     */
    private Version version = Version.V13;
    /**
     * Cookie
     */
    private final List<Cookie> cookies = new ArrayList<>();
    /**
     * http headers
     */
    private final Map<String, Object> httpHeaders = new HashMap<>();
    /**
     * 自定义证书
     */
    private final List<Certificate> certificates = new ArrayList<>();

    /**
     * 构造方法初始化
     */
    public Config() {
        try {
            this.setUri(new URI("ws://127.0.0.1:10000"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构造方法初始化
     * @param url URL 地址
     */
    public Config(String url) {
        try {
            this.setUri(new URI(url));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构造方法初始化
     * @param uri URL 地址
     */
    public Config(URI uri) {
        this.setUri(uri);
    }

    /**
     * 构造方法初始化
     * @param uri URL 地址
     * @param cookies cookies 对象
     */
    public Config(URI uri, Cookie... cookies) {
        this.setUri(uri);
        this.addCookies(cookies);
    }

    /**
     * 构造方法初始化
     * @param url uri URL 地址
     * @param cookies cookies 对象
     */
    public Config(String url, Cookie... cookies) {
        try {
            this.setUri(new URI(url));
            this.addCookies(cookies);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构造方法初始化
     * @param uri uri URL 地址
     * @param httpHeaders http header 对象
     */
    public Config(URI uri, Map<String, Object> httpHeaders) {
        this.setUri(uri);
        this.addHttpHeaders(httpHeaders);
    }

    /**
     * 构造方法初始化
     * @param url uri URL 地址
     * @param httpHeaders http header 对象
     */
    public Config(String url, Map<String, Object> httpHeaders) {
        try {
            this.setUri(new URI(url));
            this.addHttpHeaders(httpHeaders);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构造方法初始化
     * @param uri uri URL 地址
     * @param cookies cookies 对象
     * @param httpHeaders http header 对象
     */
    public Config(URI uri, List<Cookie> cookies, Map<String, Object> httpHeaders) {
        this.setUri(uri);
        this.addHttpHeaders(httpHeaders);
        this.addCookies(cookies);
    }

    /**
     * 构造方法初始化
     * @param url uri URL 地址
     * @param cookies cookies 对象
     * @param httpHeaders http header 对象
     */
    public Config(String url, List<Cookie> cookies, Map<String, Object> httpHeaders) {
        try {
            this.setUri(new URI(url));
            this.addHttpHeaders(httpHeaders);
            this.addCookies(cookies);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPort() {

        return port;
    }

    public void setPort(int port) {
        this.port = port;
        // 刷新 uri
        refreshUri();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
        // 刷新 uri
        refreshUri();
    }

    public Agreement getAgreement() {
        return agreement;
    }

    public void setAgreement(Agreement agreement) {
        this.agreement = agreement;
        // 刷新 uri
        refreshUri();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        // 刷新 uri
        refreshUri();
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
        // 刷新 uri
        refreshUri();
    }

    public void setUri(URI uri) {
        this.uri = uri;
        this.host = uri.getHost();
        this.port =  uri.getPort();
        this.param = uri.getQuery();
        this.path = uri.getRawPath();
        this.agreement = "wss".equals(uri.getScheme()) ? Agreement.WSS : Agreement.WS;
        if (this.getPort() <= 0) {
            this.port = this.getAgreement() == Agreement.WSS ? 443 : 80;
        }
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public Map<String, Object> getHttpHeaders() {
        return httpHeaders;
    }

    public void addCookies(Cookie... cookies) {
        this.cookies.addAll(Arrays.asList(cookies));
    }

    public void addCookies(List<Cookie> cookies) {
        this.cookies.addAll(cookies);
    }

    public void clearCookies() {
        this.cookies.clear();
    }

    public void addHttpHeaders(String key, Object value) {
        this.httpHeaders.put(key, value);
    }

    public void addHttpHeaders(Map<String, Object> httpHeaders) {
        if (httpHeaders != null) {
            this.httpHeaders.putAll(httpHeaders);
        }
    }

    public void delHttpHeaders(String headerName) {
        this.httpHeaders.remove(headerName);
    }

    public void clearHttpHeaders() {
        this.httpHeaders.clear();
    }

    public List<Certificate> getCertificates() {
        return certificates;
    }

    public void addCertificate(Certificate certificate) {
        this.certificates.add(certificate);
    }

    /**
     * 输出 URI 对象
     * @return URI 对象
     */
    public URI uri() {
        return this.uri;
    }

    /**
     * 刷新 URI 对象
     */
    private void refreshUri() {
        try {
            this.uri = new URI(this.uri.getScheme()
                    + "://" + this.uri.getHost()
                    + ":" + this.getPort()
                    + this.uri.getRawPath()
                    + (this.uri.getQuery() == null ? "" : "?" + this.uri.getQuery()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 输出 Web Socket Version 对象
     * @return Web Socket Version 对象
     */
    public WebSocketVersion version() {
        switch (this.version) {
            case V00:
                return WebSocketVersion.V00;
            case V07:
                return WebSocketVersion.V07;
            case V08:
                return WebSocketVersion.V08;
            case V13:
                return WebSocketVersion.V13;
            default:
                return WebSocketVersion.UNKNOWN;
        }
    }

    /**
     * 输出 HTTP Headers 对象
     * @return HTTP Headers 对象
     */
    public HttpHeaders httpHeaders() {
        final StringBuilder cookiesSb = new StringBuilder();
        final HttpHeaders rHttpHeaders = new DefaultHttpHeaders();
        if (httpHeaders.size() > 0) {
            for (final String key : httpHeaders.keySet()) {
                rHttpHeaders.add(key, httpHeaders.get(key));
            }
        }
        if (cookies.size() > 0) {
            for (Cookie cookie : cookies) {
                cookiesSb.append(cookie.toString());
            }
            rHttpHeaders.add(COOKIE, cookiesSb.toString());
        }
        return rHttpHeaders;
    }

    @Override
    public String toString() {
        return "{"
                + "\"port\":"
                + port
                + ",\"host\":\""
                + host + '\"'
                + ",\"agreement\":\""
                + agreement + '\"'
                + ",\"path\":\""
                + path + '\"'
                + ",\"param\":\""
                + param + '\"'
                + ",\"uri\":"
                + uri
                + ",\"version\":"
                + version
                + ",\"cookies\":"
                + cookies
                + ",\"httpHeaders\":"
                + httpHeaders
                + ",\"certificates\":"
                + certificates
                + "}";
    }
}
