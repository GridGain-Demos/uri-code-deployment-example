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

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.IgniteServices;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeJobResultPolicy;
import org.apache.ignite.compute.ComputeTask;
import org.apache.ignite.resources.IgniteInstanceResource;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Example ComputeTask.
 */
public class RedeployServiceComputeTask implements ComputeTask<String, Long> {
    /** */
    @Override
    public Map<? extends ComputeJob, ClusterNode> map(List<ClusterNode> list, String arg) throws IgniteException {
        return Collections.singletonMap(new RedeployService(arg), list.iterator().next());
    }

    /** */
    @Override
    public ComputeJobResultPolicy result(ComputeJobResult computeJobResult, List<ComputeJobResult> list) throws IgniteException {
        return ComputeJobResultPolicy.REDUCE;
    }

    /** */
    @Override
    public Long reduce(List<ComputeJobResult> results) throws IgniteException {
        return results.iterator().next().getData();
    }

    /**
     * Example internal ComputeJob.
     */
    private class RedeployService implements ComputeJob {
        /** */
        private String cacheName;

        /** */
        @IgniteInstanceResource
        private Ignite ignite;

        /** */
        public RedeployService(String arg) {
            this.cacheName = arg;
        }

        /** */
        @Override public void cancel() {
            // No-op.
        }

        /** */
        @Override public Long execute() {
            IgniteServices svcs = ignite.services();

            svcs.cancel("mySingleton");

            svcs.deployClusterSingleton("mySingleton", new TestService("myCache"));

            return 0L;
        }
    }
}
