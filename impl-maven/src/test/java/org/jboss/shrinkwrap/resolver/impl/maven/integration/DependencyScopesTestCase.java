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

package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.util.List;

import junit.framework.Assert;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenArtifactInfo;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.AcceptScopesStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;
import org.junit.Test;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class DependencyScopesTestCase {
    private static final ScopeType[] SCOPES = new ScopeType[]{ScopeType.COMPILE, ScopeType.PROVIDED, ScopeType.RUNTIME};
    private static final SingleScopedStrategy SCOPED_STRATEGY = new SingleScopedStrategy(SCOPES);
    private static final String SETTINGS_XML = "target/settings/profiles/settings.xml";

    @Test
    public void testProvidedDependency() throws Exception {
        final String coordinates = "org.jboss.xnio:xnio-api:3.1.0.Beta7";

        final MavenStrategyStage mss = Maven.configureResolver().fromFile(SETTINGS_XML).resolve(coordinates);
        final MavenFormatStage mfs = mss.using(SCOPED_STRATEGY);
        final MavenResolvedArtifact info = mfs.asSingleResolvedArtifact();

        Assert.assertNotNull(info);
        MavenArtifactInfo[] dependencies = info.getDependencies();
        Assert.assertNotNull(dependencies);
        // http://search.maven.org/remotecontent?filepath=org/jboss/xnio/xnio-api/3.1.0.Beta7/xnio-api-3.1.0.Beta7.pom
        // there should be org.jboss.logging:jboss-logging
        Assert.assertTrue(dependencies.length > 0);
    }

    private static class SingleScopedStrategy implements MavenResolutionStrategy {
        private final MavenResolutionFilter[] EMPTY_CHAIN = new MavenResolutionFilter[]{};

        private MavenResolutionFilter[] filters;

        SingleScopedStrategy(final ScopeType... scopes) {
            final AcceptScopesStrategy scopesStrategy = new AcceptScopesStrategy(scopes);
            final MavenResolutionFilter[] scopesFilters = scopesStrategy.getResolutionFilters();
            filters = new MavenResolutionFilter[scopesFilters.length];
            for (int i = 0; i < filters.length; i++) {
                final int index = i;
                filters[i] = new MavenResolutionFilter() {
                    public boolean accepts(MavenDependency dependency, List<MavenDependency> dependenciesForResolution) {
//                        return scopesFilters[index].accepts(dependency, dependenciesForResolution) && NonTransitiveFilter.INSTANCE.accepts(dependency, dependenciesForResolution);
                        return true;
                    }
                };
            }
        }

        public MavenResolutionFilter[] getPreResolutionFilters() {
            return EMPTY_CHAIN;
        }

        public MavenResolutionFilter[] getResolutionFilters() {
//            return filters;
            return EMPTY_CHAIN;
        }
    }
}
