package com.studiomediatech.maven.report;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import com.studiomediatech.svn.SvnUtils;
import com.studiomediatech.svn.Tag;
import com.studiomediatech.svn.TagUtils;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.model.Scm;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;

/**
 * <p>
 * Generates subversion report, showing information about the current state of
 * the source code in the SCM repository. The report currently produces:
 * </p>
 * <dl>
 * <dt>Tag tree</dt>
 * <dd>shows a tree view of the current tagged releases, where patch level
 * versions are listed as branches to specific major and minor releases.</dd>
 * </dl>
 * <p>
 * The report output is bound to the site-phase, generating an HTML page, but it
 * also prints the report to the console. To run invoke the reporting directly,
 * use the following:
 * </p>
 * 
 * <pre>
 * mvn ${project.groupId}:${project.artifactId}:${project.version}:svn-report
 * </pre>
 * 
 * @author Olle Törnström <olle@studiomediatech.com>
 * @goal svn-report
 * @phase site
 * @since 1.0.0
 */
public class SvnReport extends AbstractMavenReport {
  public static String SEPARATOR = "------------------------------------------------------------------------";

  /**
   * The project instance to process.
   * 
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  volatile MavenProject project;

  /**
   * Doxia Site Renderer.
   * 
   * @component
   * @required
   * @readonly
   */
  volatile Renderer renderer;

  /**
   * The report output directory.
   * 
   * @parameter expression="${project.build.directory}"
   * @required
   * @readonly
   */
  volatile File outputDirectory;

  final Collection<Tag> tags = new TreeSet<Tag>();
  final List<Collection<Tag>> partitionedTags = new ArrayList<Collection<Tag>>();

  @Override
  public String getOutputName() {
    return "svn-report";
  }

  @Override
  public String getName(Locale locale) {
    return "SVN Report";
  }

  @Override
  public String getDescription(Locale locale) {
    return "Report showing the state of the Subversion repository for this project.";
  }

  @Override
  protected Renderer getSiteRenderer() {
    if (this.renderer == null) {
      throw new IllegalStateException("renderer must not be empty");
    }
    else {
      return this.renderer;
    }
  }

  @Override
  protected String getOutputDirectory() {
    if (this.outputDirectory == null) {
      throw new IllegalStateException("outputDirectory must not be empty");
    }
    try {
      return this.outputDirectory.getAbsolutePath();
    }
    catch (SecurityException e) {
      throw new RuntimeException("unable to resolve absolute path to output directory", e);
    }
  }

  @Override
  protected MavenProject getProject() {
    if (this.project == null) {
      throw new IllegalStateException("project must not be empty");
    }
    else {
      return this.project;
    }
  }

  @Override
  public boolean canGenerateReport() {
    return true;
  }

  @Override
  protected void executeReport(Locale locale) {
    fetchTagList();
    partitionTagsByMajorMinor();
    generatePlainTextReportOutput(getLog(), locale);
    generateHtmlReportOutput(getSink(), locale);
  }

  void fetchTagList() {
    String connection = getScmConnection();
    String svnUrl = SvnUtils.makeCleanUrl(connection);
    String tagsUrl = SvnUtils.makeTagsUrl(svnUrl);
    List<String> tags = SvnUtils.fetchTags(tagsUrl);
    for (String tag : tags) {
      this.tags.add(new Tag(tag));
    }
  }

  private String getScmConnection() {
    if (this.project == null) {
      throw new IllegalStateException("project must not be empty");
    }
    Scm scm = this.project.getScm();
    if (scm == null) {
      throw new IllegalArgumentException("project scm must not be empty");
    }
    String connection = scm.getConnection();
    if (connection == null) {
      throw new IllegalArgumentException("project scm connection must not be empty");
    }
    return connection;
  }

  void partitionTagsByMajorMinor() {
    Collection<Tag> majorMinorTags = TagUtils.filterUniqueMajorMinors(this.tags);
    for (Tag tag : majorMinorTags) {
      int major = tag.getMajorVersion();
      int minor = tag.getMinorVersion();
      Collection<Tag> patchTags = TagUtils.filterByMajorMinor(this.tags, major, minor);
      this.partitionedTags.add(patchTags);
    }
  }

  @SuppressWarnings("unused")
  void generatePlainTextReportOutput(Log log, Locale locale) {
    log.info(SvnReport.SEPARATOR);
    log.info("Generating \"Subversion\" report.");
    log.info("");
    if (this.partitionedTags.size() > 0) {
      log.info(" [tags]");
      for (Collection<Tag> tags : this.partitionedTags) {
        StringBuilder line = new StringBuilder("   +");
        for (Tag tag : tags) {
          appendTagTextToLine(line, tag);
        }
        log.info("   |");
        log.info(line.toString());
      }
    }
    log.info("");
  }

  private void appendTagTextToLine(StringBuilder sb, Tag t) {
    sb.append("--[")
      .append(t.getMajorVersion())
      .append(".")
      .append(t.getMinorVersion())
      .append(".")
      .append(t.getPatchVersion())
      .append("]");
  }

  void generateHtmlReportOutput(Sink sink, Locale locale) {
    pageHeading(sink, locale);
    pageBody(sink, locale);
  }

  private void pageHeading(Sink sink, Locale locale) {
    String title = "Subversion Project Report";
    sink.head();
    sink.title();
    sink.text(title);
    sink.title_();
    sink.head_();
    sink.body();
    sink.section1();
    sink.sectionTitle1();
    sink.text(title);
    sink.sectionTitle1_();
    sink.paragraph();
    sink.text(getDescription(locale));
    sink.paragraph_();
    sink.section1_();
  }

  @SuppressWarnings({ "deprecation", "unused" })
  private void pageBody(Sink sink, Locale locale) {
    sink.section2();
    sink.verbatim(true);
    sink.text("[tags]\n");
    if (this.partitionedTags.size() > 0) {
      for (Collection<Tag> tags : this.partitionedTags) {
        StringBuilder line = new StringBuilder("  +");
        for (Tag tag : tags) {
          appendTagTextToLine(line, tag);
        }
        sink.text("  |\n");
        sink.text(line.toString() + "\n");
      }
    }
    sink.verbatim_();
    sink.section2_();
  }
}
