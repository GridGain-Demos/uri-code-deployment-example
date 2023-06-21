/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gridgain.service;

import com.gridgain.models.ModelClass;
import org.apache.ignite.Ignite;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;

/**
 * Example Service.
 */
public class TestService implements Service {
    /** */
    private final String cacheName;

    /** */
    @IgniteInstanceResource
    private Ignite ignite;

    /** */
    private boolean cancelled = false;

    /** */
    public TestService(String cacheName) {
        this.cacheName = cacheName;
    }

    /** */
    @Override
    public void cancel(ServiceContext serviceContext) {
        cancelled = true;
    }

    /** */
    @Override
    public void init(ServiceContext serviceContext) throws Exception {

    }

    /** */
    @Override
    public void execute(ServiceContext serviceContext) throws Exception {

        while (!cancelled) {
            ModelClass model = new ModelClass(ignite.cache(cacheName).sizeLong() + 30L);
            System.out.println("Output from service - " + model.getValue());
            Thread.sleep(2000);
        }
    }
}
