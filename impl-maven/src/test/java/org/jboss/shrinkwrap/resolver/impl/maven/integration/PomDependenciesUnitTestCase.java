/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="http://community.jboss.org/people/silenius">Samuel Santos</a>
 */
public class PomDependenciesUnitTestCase {

    @BeforeClass
    public static void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
    }

    @AfterClass
    public static void clearRemoteRepository() {
        System.clearProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);
    }

    /**
     * Tests loading of a POM file with parent not available on local file system
     *
     */
    @Test
    public void parentPomRepositories() {

        File[] files = Resolvers.use(MavenResolverSystem.class).loadPomFromFile("target/poms/test-child.xml")
            .resolve("org.jboss.shrinkwrap.test:test-child:1.0.0").withTransitivity().asFile();

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-child.tree"),
            ScopeType.COMPILE, ScopeType.RUNTIME).validate(files);
    }

    /**
     * Tests loading of a POM file with parent not available on local file system
     *
     */
    @Test
    public void shortcutParentPomRepositories() {

        File[] files = Maven.resolver().loadPomFromFile("target/poms/test-child.xml")
            .resolve("org.jboss.shrinkwrap.test:test-child:1.0.0").withoutTransitivity().asFile();

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-child-shortcut.tree"),
            ScopeType.COMPILE).validate(files);
    }

    /**
     * Tests loading of a POM file with parent available on local file system
     *
     */
    @Test
    public void parentPomRemoteRepositories() {

        File[] files = Resolvers.use(MavenResolverSystem.class).loadPomFromFile("target/poms/test-remote-child.xml")
            .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").withTransitivity().asFile();

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c.tree")).validate(
            files);
    }

    /**
     * Tests loading of a POM file with parent available on local file system
     *
     */
    @Test
    public void shortcutParentPomRemoteRepositories() {

        File[] files = Maven.resolver().loadPomFromFile("target/poms/test-remote-child.xml")
            .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").withoutTransitivity().asFile();

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c-shortcut.tree"))
            .validate(files);
    }

    /**
     * Tests loading of a POM file with parent available on local file system Uses POM to get artifact version
     *
     */
    @Test
    public void artifactVersionRetrievalFromPom() {
        File[] files = Resolvers.use(MavenResolverSystem.class).loadPomFromFile("target/poms/test-remote-child.xml")
            .resolve("org.jboss.shrinkwrap.test:test-deps-c").withTransitivity().asFile();

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c.tree")).validate(
            files);
    }

    /**
     * Tests loading of a POM file with parent available on local file system Uses POM to get artifact version
     *
     */
    @Test
    public void shortcutArtifactVersionRetrievalFromPom() {

        File[] files = Maven.resolver().loadPomFromFile("target/poms/test-remote-child.xml")
            .resolve("org.jboss.shrinkwrap.test:test-deps-c").withoutTransitivity().asFile();

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c-shortcut.tree"))
            .validate(files);
    }

    /**
     * Tests loading of a POM file with parent available on local file system Uses POM to get artifact version
     *
     */
    @Test
    public void shortcutArtifactVersionRetrievalFromPomAsFile() {
        File file = Maven.resolver().loadPomFromFile("target/poms/test-remote-child.xml")
            .resolve("org.jboss.shrinkwrap.test:test-deps-c").withoutTransitivity().asSingleFile();

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c-shortcut.tree"))
            .validate(file);
    }

    /**
     * Tests loading of a POM file with parent available on local file system. However, the artifact version is not used
     * from there, but specified manually
     *
     */
    @Test
    public void artifactVersionRetrievalFromPomOverride() {

        final MavenDependency dependency = MavenDependencies.createDependency(
            "org.jboss.shrinkwrap.test:test-deps-c:2.0.0", null, false);
        File[] files = Resolvers.use(MavenResolverSystem.class).loadPomFromFile("target/poms/test-remote-child.xml")
            .addDependency(dependency).resolve().withTransitivity().asFile();

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c-2.tree")).validate(
            files);
    }

    /**
     * Tests loading of a POM file with parent available on local file system. However, the artifact version is not used
     * from there, but specified manually
     *
     */
    @Test
    public void shortcutArtifactVersionRetrievalFromPomOverride() {

        final MavenDependency dependency = MavenDependencies.createDependency(
            "org.jboss.shrinkwrap.test:test-deps-c:2.0.0", null, false);
        File file = Maven.resolver().loadPomFromFile("target/poms/test-remote-child.xml").addDependency(dependency)
            .resolve().withoutTransitivity().asSingleFile();

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c-2-shortcut.tree"))
            .validate(file);
    }

    /**
     * Tests resolution of dependencies for a POM file with parent on local file system
     *
     */
    @Test
    public void pomBasedDependencies() {

        File[] files = Maven.resolver().loadPomFromFile("target/poms/test-child.xml").importRuntimeDependencies()
            .asFile();

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-child.tree"), false,
            ScopeType.COMPILE, ScopeType.RUNTIME).validate(files);

    }

    /**
     * Tests resolution of dependencies for a POM file without parent on local file system
     *
     */
    @Test
    public void pomRemoteBasedDependencies() {

        File[] files = Maven.resolver().loadPomFromFile("target/poms/test-remote-child.xml")
            .importRuntimeDependencies().asFile();

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-remote-child.tree"),
            false, ScopeType.COMPILE, ScopeType.RUNTIME).validate(files);
    }
}
