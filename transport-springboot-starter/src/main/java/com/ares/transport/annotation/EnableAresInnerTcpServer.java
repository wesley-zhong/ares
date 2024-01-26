package com.ares.transport.annotation;


import com.ares.transport.surpport.AresInnerTcpServerConfiguration;
import com.ares.transport.surpport.AresTcpServerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AresInnerTcpServerConfiguration.class)
public @interface EnableAresInnerTcpServer {
}
