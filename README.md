 ares  is a high performance ,light game server framework based on tcp net protocol, use Netty as network.It can be used not only for game servers but others any logic server based tcp net protocol.

## include 3 modules
      Common : protocol files and other game configs resoures, 
      Core : core interfaces and  msg dispatcher by msg Id
      transport-springboot-starter: network based on Netty
      dal : data access layer , redis cluster , mongoDB
## game server practice
      gateway : game proxy server
      game  : game  logic server
      world :  world map resource manage
      client : work as game client

## build
    1. clone the code project
    2. cd  ares/   ; mvn clean ; mvn package
    3. java -jar game.jar  java -jar gateway.jar  java -jar world.jar