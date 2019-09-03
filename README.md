# 在Spring boot 2 微服务中使用 GRPC

## 关于RPC
RPC（Remote Procedure Call）—远程过程调用，它是一种通过网络从远程计算机程序上请求服务，而不需要了解底层网络技术的协议。RPC协议假定某些传输协议的存在，如TCP或UDP，为通信程序之间携带信息数据。在OSI网络通信模型中，RPC跨越了传输层和应用层。RPC使得开发包括网络分布式多程序在内的应用程序更加容易。

## gRPC
gRPC是Google开源的通用高性能RPC框架，它支持的是使用Protocol Buffers来编写Service定义，支持较多语言扩平台并且拥有强大的二进制序列化工具集。与文章《RPC框架实践之：Apache Thrift》一文中实践的另一种通用RPC框架Thrift能通过Generator自动生成对应语言的Service接口类似，gRPC也能自动地生成Server和Client的Service存根(Stub)，我们只需要一个命令就能快速搭建起RPC运行环境。

## 微服务和 RPC
在`微服务`大行其道的今天，服务间通讯其实是个比较大的问题，`REST`调用及测试都很方便，RPC就显得有点繁琐，但是RPC的效率是毋庸置疑的，所以建议在多系统之间的内部调用采用RPC。对外提供的服务，Rest更加合适。
                                             
## 如果何用
- 在一个 .proto 文件内定义服务。
- 用 protocol buffer 编译器生成服务器和客户端代码。
- 使用 gRPC 的 Java API 为你的服务实现一个简单的客户端和服务器。

## Demo

创建一个Spring boot 2项目，选择`Web Starter`和`Lombok`就可以了。然后加入对`grpc-spring-boot-starter`的依赖。

```xml
<dependency>
    <groupId>io.github.lognet</groupId>
    <artifactId>grpc-spring-boot-starter</artifactId>
    <version>3.4.1</version>
</dependency>
```

编译需要修改，因为编译时需要先`.proto`文件进行代码生成，生成我们想要的java代码文件。生成的文件会在target下的generated-source下的protobuf文件夹下。编译配置如下：

```xml
  <build>
        <extensions>
            <extension>
                <groupId>kr.motd.maven</groupId>
                <artifactId>os-maven-plugin</artifactId>
                <version>${os-maven-plugin.version}</version>
            </extension>
        </extensions>
        <plugins>
            <plugin>
                <groupId>org.xolstice.maven.plugins</groupId>
                <artifactId>protobuf-maven-plugin</artifactId>
                <version>${protobuf-maven-plugin.version}</version>
                <configuration>
                    <protocArtifact>
                        com.google.protobuf:protoc:3.9.1:exe:${os.detected.classifier}
                    </protocArtifact>
                    <pluginId>grpc-java</pluginId>
                    <pluginArtifact>
                        io.grpc:protoc-gen-grpc-java:1.23.0:exe:${os.detected.classifier}
                    </pluginArtifact>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>compile-custom</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

添加三个`.proto`文件，第一个是`Member.proto`文件，作为一个复合对象，作为参数使用。

```proto
syntax = "proto3";
option java_multiple_files = true;
package com.example.grpcdemo.grpc;

message Member {
    string username = 1;
    string password = 2;
    string info = 3;
}
```
然后就是Service文件，`MemberListSerbice.proto`文件中定义了，request和respone的内容，还有服务输入输出的message。这里需要引入之前定义的`Member.proto`
```proto
syntax = "proto3";
option java_multiple_files = true;
package com.example.grpcdemo.grpc;
import "Member.proto";

message MemberListRequest {
    int32 page = 1;
    int32 per_page = 2;
}

message MemberListResponse {
    repeated Member member = 1;
}

service MemberListService {
    rpc member_list(MemberListRequest) returns (MemberListResponse);
}
```
这里另一个Service详见源代码。下面添加一些本项目所需的RPC服务处理代码，先编译项目，省城所需java文件，然后就可以开始写RPC服务处理代码，需要从生成的抽象类继承。
```java
@GRpcService
public class MemberLoginServiceImpl extends MemberLoginServiceGrpc.MemberLoginServiceImplBase {

    @Override
    public void memberLogin(MemberLoginRequest request, StreamObserver<MemberLoginResponse> responseObserver) {

        String token = "";
        if(request.getMember().getPassword().equals("123456")){
            token = "success";
        }

        MemberLoginResponse response = MemberLoginResponse
                .newBuilder()
                .setToken(token)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
```
还需要一个Java类来定义客户端连接RPC所需的Bean
```java
@Configuration
public class GrpcConfig {
    @Bean
    ManagedChannel channel(@Value("${app-config.grpc-server-name}") String name,
                           @Value("${app-config.grpc-server-port}") Integer port){
        return ManagedChannelBuilder.forAddress(name, port)
                .usePlaintext()
                .build();
    }

    @Bean
    MemberListServiceGrpc.MemberListServiceBlockingStub memberListServiceBlockingStub(ManagedChannel channel){
        return MemberListServiceGrpc.newBlockingStub(channel);
    }

    @Bean
    MemberLoginServiceGrpc.MemberLoginServiceBlockingStub memberLoginServiceStub(ManagedChannel channel){
        return MemberLoginServiceGrpc.newBlockingStub(channel);
    }
}
```
最后还有一个测试用的Controller，浏览器访问controller中的rest服务，rest服务连接rpc去访问真实服务，这里服务端和客户端并没有分开，实际controller应该在真正的客户端项目中。这里只是一个demo
```java

```

## 资源
- http://doc.oschina.net/grpc?t=60134
- https://developers.google.com/protocol-buffers/docs/proto3
- https://www.baeldung.com/grpc-introduction