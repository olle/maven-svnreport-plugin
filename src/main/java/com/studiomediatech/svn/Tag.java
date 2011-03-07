package com.studiomediatech.svn;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tag implements Comparable<Tag> {
  private static final Pattern pattern = Pattern.compile("([\\w\\-]+)-(\\d+)\\.(\\d+)\\.(\\d+)(\\.\\d+)?(b|beta|-beta)?",
                                                         Pattern.CASE_INSENSITIVE);

  private final Matcher matcher;

  private final String tag;
  private final String name;
  private final int major;
  private final int minor;
  private final int patch;

  public Tag(String tag) {
    if (tag == null) {
      throw new IllegalArgumentException("tag must not be empty");
    }
    else {
      this.matcher = Tag.pattern.matcher(tag);
      if (this.matcher.matches()) {
        this.tag = tag;
        this.name = this.matcher.group(1);
        this.major = Integer.parseInt(this.matcher.group(2), 10);
        this.minor = Integer.parseInt(this.matcher.group(3), 10);
        this.patch = Integer.parseInt(this.matcher.group(4), 10);
      }
      else {
        throw new IllegalArgumentException("invalid tag format: [" + this.tag + "]");
      }
    }
  }

  public String getName() {
    return this.name;
  }

  public int getMajorVersion() {
    return this.major;
  }

  public int getMinorVersion() {
    return this.minor;
  }

  public int getPatchVersion() {
    return this.patch;
  }

  @Override
  public String toString() {
    return (new StringBuilder(this.name).append("-")
                                        .append(this.major)
                                        .append(".")
                                        .append(this.minor)
                                        .append(".").append(this.patch)).toString();
  }

  @Override
  public int compareTo(Tag other) {
    if (!this.name.equals(other.getName())) {
      return this.name.compareTo(other.getName());
    }
    if (this.major != other.getMajorVersion()) {
      return this.major > other.getMajorVersion() ? 1 : -1;
    }
    if (this.minor != other.getMinorVersion()) {
      return this.minor > other.getMinorVersion() ? 1 : -1;
    }
    if (this.patch != other.getPatchVersion()) {
      return this.patch > other.getPatchVersion() ? 1 : -1;
    }
    return 0;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.tag == null) ? 0 : this.tag.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Tag other = (Tag) obj;
    if (this.tag == null) {
      if (other.tag != null) {
        return false;
      }
    }
    else if (!this.tag.equals(other.tag)) {
      return false;
    }
    return true;
  }
}
