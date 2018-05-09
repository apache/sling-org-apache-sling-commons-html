/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.commons.html.it;

import org.apache.sling.testing.paxexam.TestSupport;
import org.ops4j.pax.exam.Option;

import static org.apache.sling.testing.paxexam.SlingOptions.scr;
import static org.apache.sling.testing.paxexam.SlingVersionResolver.SLING_GROUP_ID;
import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import org.ops4j.pax.exam.util.PathUtils;

public abstract class HtmlTestSupport extends TestSupport {

    public Option baseConfiguration() {
        return composite(
            super.baseConfiguration(),
            // Sling Commons HTML
            testBundle("bundle.filename"),
            mavenBundle().groupId(SLING_GROUP_ID).artifactId("org.apache.sling.commons.osgi").versionAsInProject(),
            scr(),
            // testing
            mavenBundle().groupId("org.apache.servicemix.bundles").artifactId("org.apache.servicemix.bundles.hamcrest").versionAsInProject(),
            mavenBundle().groupId("org.apache.commons").artifactId("commons-lang3").versionAsInProject(),
            junitBundles(),
            logging()
        );
    }

    protected Option logging() {
        final String filename = String.format("file:%s/src/test/resources/logback.xml", PathUtils.getBaseDir());
        return composite(
            systemProperty("logback.configurationFile").value(filename),
            mavenBundle().groupId("org.slf4j").artifactId("slf4j-api").version("1.7.21"),
            mavenBundle().groupId("org.slf4j").artifactId("jcl-over-slf4j").version("1.7.21"),
            mavenBundle().groupId("ch.qos.logback").artifactId("logback-core").version("1.1.7"),
            mavenBundle().groupId("ch.qos.logback").artifactId("logback-classic").version("1.1.7")
        );
    }
}
