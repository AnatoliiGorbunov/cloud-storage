package ru.geekbrains.cloud.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.cloud.handler.CloudMessageHandler;
import ru.geekbrains.cloud.service.UserNameService;

import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
public class NettyEchoServer {
    public static void main(String[] args) {
        EventLoopGroup auth = new NioEventLoopGroup(1);//Экзекютор серыисы как только их запустим (НиоЭвентЛукГрупп)
        // они будут крутиться в бесконечном цикле
        EventLoopGroup worker = new NioEventLoopGroup();
        UserNameService userNameService = new UserNameService();
        ConcurrentLinkedDeque<ChannelHandlerContext> users = new ConcurrentLinkedDeque<>();
        try {//настройка сервера
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(auth, worker)//передаем воркеров
                    .channel(NioServerSocketChannel.class)//
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(//обработчики событий которые обрабатывают сообщения между клиентом и сервером//паттерн сенс оф респосибилити(цкпочка ответственности)
                                    new ObjectEncoder(),//конвеер обработчиков
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new CloudMessageHandler()
                            );

                        }
                    });

            ChannelFuture future = bootstrap.bind(8189).sync();//sync возвращает некоторое будущее//слушаем порт 8189
            log.debug("Server started...");
            future.channel().closeFuture().sync();//Блокирующая операция//сервер застыл в ожидании событий
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            auth.shutdownGracefully();//когда произожло какое то исключение закрываем воркеры
            worker.shutdownGracefully();
        }
    }
}