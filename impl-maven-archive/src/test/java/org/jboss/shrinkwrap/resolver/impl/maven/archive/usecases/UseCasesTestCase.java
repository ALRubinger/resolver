/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.impl.maven.archive.usecases;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.formatprocessor.FileFormatProcessor;
import org.jboss.shrinkwrap.resolver.api.formatprocessor.InputStreamFormatProcessor;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.ResolvedArtifactMetadata;
import org.jboss.shrinkwrap.resolver.api.maven.archive.ArchiveFormatProcessor;
import org.jboss.shrinkwrap.resolver.api.maven.archive.MavenArchiveResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.archive.ShrinkWrapMaven;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencyExclusion;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
@Ignore
// TODO
// This won't actually run until we provide the implementation, though we DO want to test compilation and observe the
// API grammars in action for each use case
public class UseCasesTestCase {

    /**
     * Use case 1:
     *
     * Resolve a single artifact without transitive dependencies as Archive<?>
     */
    @Test
    public void singleArtifactAsArchive() {

        @SuppressWarnings("unused")
        final JavaArchive longhand = Resolvers.use(MavenArchiveResolverSystem.class).resolve("G:A:V")
            .withoutTransitivity().asSingle(JavaArchive.class);

        @SuppressWarnings("unused")
        final JavaArchive shorthand = ShrinkWrapMaven.resolver().resolve("G:A:V").withoutTransitivity()
            .asSingle(JavaArchive.class);
    }

    /**
     * Use case 2:
     *
     * Resolve a single artifact without transitive dependencies as File
     */
    @Test
    public void singleArtifactAsFile() {

        @SuppressWarnings("unused")
        final File longhand = Resolvers.use(MavenResolverSystem.class).resolve("groupId:artifactId:version")
            .withoutTransitivity().asSingleFile();

        @SuppressWarnings("unused")
        final File shortcut = Maven.resolver().resolve("groupId:artifactId:version").withoutTransitivity()
            .asSingleFile();
    }

    /**
     * Use case 3:
     *
     * Resolve a single artifact without transitive dependencies, using version from a POM file
     */
    @Test
    public void singleArtifactWithPomFile() {

        @SuppressWarnings("unused")
        final File longhand = Resolvers.use(MavenResolverSystem.class).loadPomFromFile("/path/to/file").resolve("G:A")
            .withoutTransitivity().asSingleFile();

        @SuppressWarnings("unused")
        final File shorthand = Maven.resolver().loadPomFromFile("/path/to/pom").resolve("G:A").withoutTransitivity()
            .asSingleFile();

        @SuppressWarnings("unused")
        final File fromEnvironment = Maven.configureResolverViaPlugin().resolve("G:A").withoutTransitivity()
            .asSingleFile();
    }

    /**
     * Use case 4:
     *
     * Resolve two or more artifacts without transitive dependencies
     */
    @Test
    public void multipleArtifacts() {

        final MavenDependency dep1 = MavenDependencies.createDependency("GAV", null, false);
        final MavenDependency dep2 = MavenDependencies.createDependency("GAV2", null, false);
        @SuppressWarnings("unused")
        final File[] longhandWithDependencyBuilders = Resolvers.use(MavenResolverSystem.class)
            .addDependencies(dep1, dep2).resolve().withoutTransitivity().asFile();

        @SuppressWarnings("unused")
        final File[] longhand = Resolvers.use(MavenResolverSystem.class).resolve("G:A:V", "G2:A2:V2")
            .withoutTransitivity().asFile();

        @SuppressWarnings("unused")
        final File[] shorthand = Maven.resolver().resolve("G:A:V", "G2:A2:V2").withoutTransitivity().asFile();

        @SuppressWarnings("unused")
        final File[] resolvedFiles = Maven.resolver().addDependencies(dep1, dep2).resolve().withoutTransitivity()
            .asFile();

        @SuppressWarnings("unused")
        final File[] analagous1 = Maven.resolver().resolve("org.jboss:jboss-something:1.0.0", "junit:junit:4.10")
            .withoutTransitivity().asFile();

        // DependencyResolvers.use(MavenDependencyResolver.class).artifact("G:A:V").artifact("G:B:V")
        // .resolveAsFiles(new StrictFilter());
        //
        // // or
        //
        // DependencyResolvers.use(MavenDependencyResolver.class).artifacts("G:A:V", "G:B:V").resolveAsFiles(new
        // StrictFilter());
        //
        // // or
        //
        // DependencyResolvers.use(MavenDependencyShortcut.class).resolveAsFiles("G:A:V", "G:B:V");
        //
        // // or
        //
        // Maven.resolveAsFiles("G:A:V", "G:B:V");
    }

    /**
     * Use case 5:
     *
     * Resolve an artifact with transitive dependencies
     */
    @Test
    public void transitiveArtifact() {

        @SuppressWarnings("unused")
        final File[] longhand = Resolvers.use(MavenResolverSystem.class).resolve("G:A:V").withTransitivity().asFile();

        @SuppressWarnings("unused")
        final File[] shorthand = Maven.resolver().resolve("G:A:V").withTransitivity().asFile();
    }

    /**
     * Use case 6:
     *
     * Resolve an artifact with transitive dependencies using extra exclusion
     */
    @Test
    public void transitiveArtifactExtraExclusion() {

        final MavenDependencyExclusion exclusion = MavenDependencies.createExclusion("GA");
        final MavenDependency dependency = MavenDependencies.createDependency("GAV", null, false, exclusion);

        @SuppressWarnings("unused")
        final File[] longhand = Resolvers.use(MavenResolverSystem.class).addDependency(dependency).resolve()
            .withTransitivity().asFile();

        @SuppressWarnings("unused")
        final File[] shorthand = Maven.resolver().addDependency(dependency).resolve().withTransitivity().asFile();
    }

    /**
     * Use case 7:
     *
     * Resolve artifacts with transitive dependencies using extra exclusions
     */
    @Test
    public void transitiveArtifactsExtraExclusions() {

        final MavenDependencyExclusion exclusion = MavenDependencies.createExclusion("GA");
        final MavenDependencyExclusion exclusion2 = MavenDependencies.createExclusion("GA");
        final MavenDependency dependency = MavenDependencies
            .createDependency("GAV", null, false, exclusion, exclusion2);

        @SuppressWarnings("unused")
        final File[] shorthand = Maven.resolver().addDependency(dependency).resolve().withTransitivity().asFile();
    }

    /**
     * Use case 8:
     *
     * Resolve an artifact with transitive dependencies, using pom for version
     */
    @Test
    public void transitiveArtifactWithPom() {

        @SuppressWarnings("unused")
        final File[] longhand = Resolvers.use(MavenResolverSystem.class).loadPomFromFile("path/to/pom").resolve("G:A")
            .withTransitivity().asFile();

        @SuppressWarnings("unused")
        final File[] shorthand = Maven.resolver().loadPomFromFile("path/to/pom").resolve("G:A").withTransitivity()
            .asFile();

        @SuppressWarnings("unused")
        final File[] fromPlugin = Maven.configureResolverViaPlugin().resolve("G:A").withTransitivity().asFile();
    }

    /**
     * Use case 9:
     *
     * Import the same dependencies as Maven would do.
     */
    @Test
    public void mimickMavenDependencies() {

        @SuppressWarnings("unused")
        final File[] longhand = Resolvers.use(MavenResolverSystem.class).loadPomFromFile("/path/to/pom")
            .importRuntimeDependencies().asFile();

        Assert.fail("API BROKEN HERE");

        // @SuppressWarnings("unused")
        // final JavaArchive[] shorthand = MavenArchive.resolver().configureFromPom("/path/to/pom")
        // .importDefinedDependencies().as(JavaArchive.class);

        // @SuppressWarnings("unused")
        // final JavaArchive[] environment = MavenArchive.resolver().configureFromPlugin().importDefinedDependencies()
        // .as(JavaArchive.class);

        // TODO Does the above account for scopes?

        // TODO
        // DependencyResolvers.use(MavenDependencyResolver.class).loadSettings("settings.xml").loadEffectivePom("pom.xml")
        // .importAnyDependencies(new ScopeFilter("compile", "runtime", "")).resolveAsFiles();
        //
        // // or using ShrinkWrap Maven plugin and current Maven execution
        //
        // DependencyResolvers.use(MavenDependencyResolver.class).configureFrom(MavenConfigurationTypes.ENVIRONMENT)
        // .importAnyDependencies(new ScopeFilter("compile", "runtime", "")).resolveAsFiles();
        //
        // // or using MavenImporter, which does a bit different thing
        //
        // ShrinkWrap.create(MavenImporter.class).loadSettings("settings.xml").loadEffectivePom("pom.xml")
        // .importAnyDependencies(new ScopeFilter("compile", "runtime", ""));
    }

    /**
     * Use case 10:
     *
     * Import test dependencies and exclude G:A:V
     */
    @Test
    public void importTestDependenciesWithExtraExclusion() {

        // TODO
        // DependencyResolvers.use(MavenDependencyResolver.class).loadEffectivePom("pom.xml")
        // .importTestDependencies(new ExclusionFilter("G:A")).resolveAsFiles();
        //
        // // or
        //
        // DependencyResolvers.use(MavenDependencyResolver.class).loadEffectivePom("pom.xml").importTestDependencies()
        // .resolveAsFiles(new ExclusionFilter("G:A:V"));
        //
        // // or
        // // note this would not work if G:A:V is a transitive dependency!
        //
        // DependencyResolvers.use(MavenDependencyResolver.class).loadEffectivePom("pom.xml")
        // .importAnyDependencies(new CombinedFilter(new ScopeFilter("test"), new ExclusionFilter("G:A:V")))
        // .resolveAsFiles();
    }

    /**
     * Use case 11:
     *
     * Import test dependencies and exclude arquillian/shrinkwrap/container (SHRINKRES-30)
     */
    @Test
    public void importTestDependenciesWithArquillianExclusions() {
        // TODO
        // solution 1 = enumerate within previous use case
        // solution 2 = write a GroupExclusionFilter, note that MavenDependency has no getter for groupId!
        // solution 3 = move shrinkwrap/arquillian/container to a distinct profile, then exclude it

        /*
         * ALR Note: Karel's Solution 2 above looks like the most likely candidate; this isn't really a core feature of
         * SWR, but we go need to define an easy way for users to write group exclusions such that another level can
         * define SW, SWR, SWD, ARQ etc and exclude in one go.
         */
    }

    /**
     * Use case 12:
     *
     * Import a dependency using different classloader (SHRINKRES-26)
     */
    @Test
    public void bootstrapShrinResWithDifferentClassloader() {

        final ClassLoader myCl = new URLClassLoader(new URL[] {});
        @SuppressWarnings("unused")
        final File file = Resolvers.use(MavenResolverSystem.class, myCl).resolve("G:A:V").withoutTransitivity()
            .asSingleFile();
    }

    /**
     * Use case 13:
     *
     * Do the same as Maven would do
     */
    @Test
    public void mimickMaven() {

        @SuppressWarnings("unused")
        final File[] longhand = Resolvers.use(MavenResolverSystem.class).loadPomFromFile("/path/to/pom")
            .importRuntimeDependencies().asFile();

        Assert.fail("API broken here");

        // @SuppressWarnings("unused")
        // final JavaArchive[] shorthand = MavenArchive.resolver().configureFromPom("/path/to/pom")
        // .importDefinedDependencies().as(JavaArchive.class);

        // TODO Does this above fulfill this use case?

        // TODO
        // ShrinkWrap
        // .create(WebArchive.class)
        // .addClasses(Class.class)
        // .addAsResource("resources")
        // .addAsLibraries(
        // DependencyResolvers.use(MavenDependencyResolver.class).loadEffectivePom("pom.xml")
        // .importAnyDependencies(new ScopeFilter("compile", "", "runtime")).resolveAsFiles());
        //
        // // or
        // // note current implementation is expecting mvn package to be run first (SHRINKRES-18)
        //
        // ShrinkWrap.create(MavenImporter.class).loadEffectivePom("pom.xml").importBuildOutput();
        //
        // // note usage of ENVIRONMENT configuration is not possible
    }

    /**
     * Use Case 14: Expose dependency information
     *
     * SHRINKRES-27
     */
    @Test
    @SuppressWarnings("unused")
    public void dependencyInfo() {
        final ResolvedArtifactMetadata longhand = Resolvers.use(MavenResolverSystem.class).resolve("G:A:V")
            .withoutTransitivity().asSingleResolvedArtifactMetadata();

        final ResolvedArtifactMetadata shortcut = Maven.resolver().resolve("G:A:V").withoutTransitivity()
            .asSingleResolvedArtifactMetadata();
        final MavenCoordinate coordinate = shortcut.getCoordinate();
        final String groupId = coordinate.getGroupId();
        final String artifactId = coordinate.getArtifactId();
        final String version = coordinate.getVersion();
        final String resolvedVersion = shortcut.getResolvedVersion();
        final String type = coordinate.getType().toString();
        final boolean isSnapshot = shortcut.isSnapshotVersion();
        final String classifier = coordinate.getClassifier();
        final File file = shortcut.getArtifact(FileFormatProcessor.INSTANCE);
        final File file2 = shortcut.getArtifactAsFile();
        final InputStream in = shortcut.getArtifact(InputStreamFormatProcessor.INSTANCE);
        final InputStream in2 = shortcut.getArtifactAsInputStream();
        final JavaArchive archive = shortcut.getArtifact(new ArchiveFormatProcessor<JavaArchive>(JavaArchive.class));
    }

    /**
     * Use case 15:
     *
     * Resolve offline SHRINKRES-45
     */
    @Test
    public void offline() {
        Maven.resolver().resolve("groupId:artifactId:version").offline().withoutTransitivity().asSingleFile();
    }

    /**
     * Use case 16: Clear configuration. Settings = "settings.xml". Load from POM: "pom.xml"
     *
     * SHRINKRES-60 SHRINKRES-51
     */
    public void configure() {
        Resolvers.configure(ConfigurableMavenResolverSystem.class).fromFile(new File("somepath")).resolve("GAV")
            .withoutTransitivity().asFile();
        Resolvers.use(ConfigurableMavenResolverSystem.class).configureViaPlugin();
        Maven.configureResolver().fromFile("~/.m2/settings.xml").resolve("GAV").withoutTransitivity().asFile();
        Maven.configureResolver().fromClassloaderResource("settings.xml").resolve("GAV").withoutTransitivity().asFile();
        Maven.configureResolver().fromClassloaderResource("settings.xml").loadPomFromFile((File) null).resolve("GA")
            .withoutTransitivity().asFile();
        @SuppressWarnings("unused")
        final JavaArchive archive = ShrinkWrapMaven.configureResolver().fromClassloaderResource("settings.xml")
            .resolve("GAV").withoutTransitivity().asSingle(JavaArchive.class);
        Maven.configureResolverViaPlugin().resolve("GA").withoutTransitivity().asSingleFile();

    }
}
