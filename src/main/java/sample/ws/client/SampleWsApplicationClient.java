/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package sample.ws.client;

import java.io.File;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Profiles;

import sample.ws.service.Hello;


@SpringBootApplication(proxyBeanMethods = false)
public class SampleWsApplicationClient {
    public static void main(String[] args) throws Exception {
       try (ConfigurableApplicationContext context = 
               new SpringApplicationBuilder(SampleWsApplicationClient.class)
                   .web(WebApplicationType.NONE)
                   .run(args)) {
           final Hello client = context.getBean(Hello.class);
           System.out.println(client.sayHello("Client"));
           
           if (context.getEnvironment().acceptsProfiles(Profiles.of("capture"))) {
               DumpingClassLoaderCapturer capturer = context.getBean(DumpingClassLoaderCapturer.class);
               capturer.dumpTo(new File(args[0]));
           }
       }
    }
}

