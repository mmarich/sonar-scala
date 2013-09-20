/*
 * Sonar Scala Plugin
 * Copyright (C) 2011 Felix Müller
 * felix.mueller.berlin@googlemail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.scala.cobertura;

import java.util.Arrays;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.plugins.cobertura.api.CoberturaUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CoberturaMavenPluginHandlerTest {
  CoberturaMavenPluginHandler handler;
  Settings settings;

  @Before
  public void before() {
    settings = new Settings();
    handler = new CoberturaMavenPluginHandler(settings);
  }

  @Test
  public void pluginDefinition() {
    assertFalse(handler.isFixedVersion());
    assertEquals("org.codehaus.mojo", handler.getGroupId());
    assertEquals("cobertura-maven-plugin", handler.getArtifactId());
    assertEquals("2.5.1", handler.getVersion());;
    assertArrayEquals(new String[] {"cobertura"}, handler.getGoals());
  }

  @Test
  public void setCoberturaExclusions() {
    Project project = mock(Project.class);
    when(project.getPom()).thenReturn(new MavenProject());
    when(project.getExclusionPatterns()).thenReturn(new String[] { "**/Foo.scala", "com/*Test.*", "com/*" });

    MavenPlugin coberturaPlugin = new MavenPlugin(CoberturaUtils.COBERTURA_GROUP_ID, CoberturaUtils.COBERTURA_ARTIFACT_ID, null);
    handler.configure(project, coberturaPlugin);

    List<String> excludeParams = Arrays.asList(coberturaPlugin.getParameters("instrumentation/excludes/exclude"));
    assertEquals(3, excludeParams.size());
    assertTrue(excludeParams.contains("**/Foo.class"));
    assertTrue(excludeParams.contains("com/*Test.*"));
    assertTrue(excludeParams.contains("com/*.class"));
  }
}
