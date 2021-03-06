/*
 * Copyright (C) 2015 Original Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fabric8.kubernetes.pipeline;

import groovy.lang.Binding;
import hudson.Extension;
import io.fabric8.workflow.core.ClassWhiteList;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.jenkinsci.plugins.workflow.cps.GlobalVariable;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Callable;

@Extension
public class KubernetesDSL extends GlobalVariable {

    private static final String KUBERNETES = "kubernetes";

    @Nonnull
    @Override
    public String getName() {
        return KUBERNETES;
    }

    @Nonnull
    @Override
    public Object getValue(CpsScript script) throws Exception {
        Binding binding = script.getBinding();
        Object kubernetes;
        if (binding.hasVariable(getName())) {
            kubernetes = binding.getVariable(getName());
        } else {
            // Note that if this were a method rather than a constructor, we would need to mark it @NonCPS lest it throw CpsCallableInvocation.
            kubernetes = script.getClass().getClassLoader().loadClass("io.fabric8.kubernetes.pipeline.Kubernetes").getConstructor(CpsScript.class).newInstance(script);
            binding.setVariable(getName(), kubernetes);
        }
        return kubernetes;
    }


    @Extension
    public static class PlugiWhiteList extends ClassWhiteList {
        public PlugiWhiteList() throws IOException {
            super(ScriptBytecodeAdapter.class,
                    ArrayList.class, Collection.class, HashMap.class, HashSet.class, Collections.class,
                    Callable.class);
        }
    }
}
