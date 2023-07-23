package com.ares.transport.annotation;


import com.ares.transport.surpport.AresRpcServerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AresRpcServerConfiguration.class)
public @interface EnableAresTcpServer {
}
