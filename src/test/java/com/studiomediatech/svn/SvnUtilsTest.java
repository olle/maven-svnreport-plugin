package com.studiomediatech.svn;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SvnUtilsTest {

  private static final String CONNECTION_URL = "scm:svn:file:///some/repo/project";
  private String repoUrl;
  private String cwd;

  @Before
  public void setUp() {
    this.cwd = System.getProperty("user.dir");
    this.repoUrl = "file://" + this.cwd + "/src/test/resources/repository";
  }

  @Test
  public void cleanUrlFromMavenDefaultConnection() {
    assertEquals("file:///some/repo/project", SvnUtils.makeCleanUrl(CONNECTION_URL));
  }

  @Test
  public void resolveTagsUrlFromPlainTrunkUrl() {
    String expected = this.repoUrl + "/tags";
    assertEquals(expected, SvnUtils.makeTagsUrl(this.repoUrl + "/trunk"));
  }

  @Test
  public void resolveTagsUrlFromTrunkUrlWithExtraName() {
    String expected = this.repoUrl + "/tags";
    assertEquals(expected, SvnUtils.makeTagsUrl(this.repoUrl + "/trunk/project"));
  }

  @Test
  public void resolveTagsUrlFromTrunkUrlWithExtraSlash() {
    String expected = this.repoUrl + "/tags";
    assertEquals(expected, SvnUtils.makeTagsUrl(this.repoUrl + "/trunk/"));
    assertEquals(expected, SvnUtils.makeTagsUrl(this.repoUrl + "/trunk/project/"));
  }

  @Test
  public void testFetchTagList() {
    List<String> tags = SvnUtils.fetchTags(this.repoUrl + "/tags");
    assertNotNull(tags);
    assertEquals(10, tags.size());
  }
}
