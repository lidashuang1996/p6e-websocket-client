import club.p6e.websocket.client.Callback;
import club.p6e.websocket.client.Client;
import club.p6e.websocket.client.Config;
import club.p6e.websocket.client.P6eWebSocketClientApplication;
import io.netty.buffer.ByteBuf;

/**
 * @author lidashuang
 * @version 1.0
 */
public class Test {

    public static void main(String[] args) {
//        P6eWebSocketClientApplication.initThreadPool();
        P6eWebSocketClientApplication.connector().connect(new Config("wss://danmuproxy.douyu.com:8504/"),
                new Callback() {
            @Override
            public void onOpen(Client client) {

            }

            @Override
            public void onClose(Client client) {

            }

            @Override
            public void onError(Client client, Throwable throwable) {

            }

            @Override
            public void onMessageText(Client client, String message) {

            }

            @Override
            public void onMessageBinary(Client client, ByteBuf message) {

            }

            @Override
            public void onMessagePong(Client client, ByteBuf message) {

            }

            @Override
            public void onMessagePing(Client client, ByteBuf message) {

            }

            @Override
            public void onMessageContinuation(Client client, ByteBuf message) {

            }
        }, false);
    }

}
