package com.studiomediatech.svn;

import java.util.Collection;
import java.util.TreeSet;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TagTest {

  @Test(expected = IllegalArgumentException.class)
  public void ensureNullTagsAreNotAllowed() {
    new Tag(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void ensureEmptyTagsAreNotAllowed() {
    Tag tag = new Tag("");
    tag.getName();
  }

  @Test(expected = IllegalArgumentException.class)
  public void ensureBadFormatIsDetected() {
    Tag tag = new Tag("123-fobar-123");
    tag.getName();
  }

  @Test
  public void parsesTagWithNameAndMajorMinorPatchVersion() {
    Tag tag = new Tag("simple-1.2.3");
    assertEquals("simple", tag.getName());
    assertEquals(1, tag.getMajorVersion());
    assertEquals(2, tag.getMinorVersion());
    assertEquals(3, tag.getPatchVersion());
  }

  @Test
  public void parsesTagWithDashInNameAndMajorMinorPatchVersion() {
    Tag tag = new Tag("simple-name-1.2.3");
    assertEquals("simple-name", tag.getName());
    assertEquals(1, tag.getMajorVersion());
    assertEquals(2, tag.getMinorVersion());
    assertEquals(3, tag.getPatchVersion());
  }

  @Test
  public void orderByHighestMajor() {
    Collection<Tag> tags = new TreeSet<Tag>();
    Tag t1 = new Tag("name-1.0.0");
    Tag t2 = new Tag("name-2.0.0");
    Tag t3 = new Tag("name-3.0.0");
    tags.add(t3);
    tags.add(t1);
    tags.add(t2);
    assertArrayEquals(new Tag[]{ t1, t2, t3 }, tags.toArray());
  }

  @Test
  public void orderByHighestMajorAndMinor() {
    Collection<Tag> tags = new TreeSet<Tag>();
    Tag t10 = new Tag("name-1.0.0");
    Tag t20 = new Tag("name-2.0.0");
    Tag t21 = new Tag("name-2.1.0");
    Tag t22 = new Tag("name-2.2.0");
    Tag t30 = new Tag("name-3.0.0");
    tags.add(t30);
    tags.add(t22);
    tags.add(t21);
    tags.add(t20);
    tags.add(t10);
    assertArrayEquals(new Tag[]{ t10, t20, t21, t22, t30 }, tags.toArray());
  }

  @Test
  public void orderByHighestMajorMinorAndPatch() {
    Collection<Tag> tags = new TreeSet<Tag>();
    Tag t10 = new Tag("name-1.0.0");
    Tag t20 = new Tag("name-2.0.0");
    Tag t210 = new Tag("name-2.1.0");
    Tag t211 = new Tag("name-2.1.1");
    Tag t212 = new Tag("name-2.1.2");
    Tag t22 = new Tag("name-2.2.0");
    Tag t30 = new Tag("name-3.0.0");
    tags.add(t30);
    tags.add(t22);
    tags.add(t212);
    tags.add(t211);
    tags.add(t210);
    tags.add(t20);
    tags.add(t10);
    assertArrayEquals(new Tag[]{ t10, t20, t210, t211, t212, t22, t30 }, tags.toArray());
  }
}
