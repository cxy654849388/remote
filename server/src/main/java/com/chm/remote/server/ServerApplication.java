package com.chm.remote.server;

import com.chm.remote.server.netty.EchoServer;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author caihongming
 * @version v1.0
 * @title ServerApplication
 * @package com.chm.remote.server
 * @since 2019-10-31
 * description 服务端启动入口
 **/

@SpringBootApplication
public class ServerApplication implements CommandLineRunner {

  @Value("${netty.port}")
  private int port;

  @Autowired
  private EchoServer echoServer;

  public static void main(String[] args) {
    SpringApplication.run(ServerApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    ChannelFuture future = echoServer.start(port);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> echoServer.destroy()));
    //服务端管道关闭的监听器并同步阻塞,直到channel关闭,线程才会往下执行,结束进程
    future.channel().closeFuture().syncUninterruptibly();
  }
}
