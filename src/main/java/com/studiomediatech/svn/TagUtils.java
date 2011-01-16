package com.studiomediatech.svn;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class TagUtils {

  public static Collection<Tag> filterUniqueMajorMinors(Collection<Tag> tags) {
    Collection<Tag> result = new TreeSet<Tag>();
    Set<String> alreadyAdded = new HashSet<String>();
    for (Tag t : tags) {
      String key = t.getMajorVersion() + "." + t.getMinorVersion();
      if (!alreadyAdded.contains(key)) {
        result.add(t);
        alreadyAdded.add(key);
      }
    }
    return result;
  }

  public static Collection<Tag> filterByMajorMinor(Collection<Tag> tags, int major, int minor) {
    Collection<Tag> result = new TreeSet<Tag>();
    for (Tag t : tags) {
      boolean hasSameMajor = t.getMajorVersion() == major;
      boolean hasSameMinor = t.getMinorVersion() == minor;
      if (hasSameMajor && hasSameMinor) {
        result.add(t);
      }
    }
    return result;
  }
}
