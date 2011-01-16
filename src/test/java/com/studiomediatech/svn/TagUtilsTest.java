package com.studiomediatech.svn;

import java.util.Collection;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class TagUtilsTest {

  private Collection<Tag> tags;
  private Tag t100;
  private Tag t101;
  private Tag t102;
  private Tag t110;
  private Tag t111;
  private Tag t112;
  private Tag t200;
  private Tag t201;
  private Tag t202;

  @Before
  public void setUp() {
    this.tags = new TreeSet<Tag>();

    this.t100 = new Tag("name-1.0.0");
    this.t101 = new Tag("name-1.0.1");
    this.t102 = new Tag("name-1.0.2");
    this.t110 = new Tag("name-1.1.0");
    this.t111 = new Tag("name-1.1.1");
    this.t112 = new Tag("name-1.1.2");
    this.t200 = new Tag("name-2.0.0");
    this.t201 = new Tag("name-2.0.1");
    this.t202 = new Tag("name-2.0.2");

    this.tags.add(this.t100);
    this.tags.add(this.t101);
    this.tags.add(this.t102);

    this.tags.add(this.t110);
    this.tags.add(this.t111);
    this.tags.add(this.t112);

    this.tags.add(this.t200);
    this.tags.add(this.t201);
    this.tags.add(this.t202);
  }

  @Test
  public void testFilterMajorMinor() {
    assertArrayEquals(new Tag[]{ this.t100, this.t110, this.t200 },
                      TagUtils.filterUniqueMajorMinors(this.tags).toArray());
  }

  @Test
  public void testFilterPatchByMajorMinor() {
    assertArrayEquals(new Tag[]{ this.t100, this.t101, this.t102 },
                      TagUtils.filterByMajorMinor(this.tags, 1, 0).toArray());
    assertArrayEquals(new Tag[]{ this.t110, this.t111, this.t112 },
                      TagUtils.filterByMajorMinor(this.tags, 1, 1).toArray());
    assertArrayEquals(new Tag[]{ this.t200, this.t201, this.t202 },
                      TagUtils.filterByMajorMinor(this.tags, 2, 0).toArray());
  }
}
