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
  MavenProject project;

  /**
   * Doxia Site Renderer.
   * 
   * @component
   * @required
   * @readonly
   */
  Renderer renderer;

  /**
   * The report output directory.
   * 
   * @parameter expression="${project.build.directory}"
   * @required
   * @readonly
   */
  File outputDirectory;

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
    return this.renderer;
  }

  @Override
  protected String getOutputDirectory() {
    return this.outputDirectory.getAbsolutePath();
  }

  @Override
  protected MavenProject getProject() {
    return this.project;
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
    String connection = this.project.getScm().getConnection();
    if (connection == null) {
      throw new IllegalArgumentException("project scm connection must not be empty");
    }
    return connection;
  }

  void partitionTagsByMajorMinor() {
    Collection<Tag> majorMinorTags = TagUtils.filterUniqueMajorMinors(this.tags);
    for (Tag t : majorMinorTags) {
      Collection<Tag> patchTags = TagUtils.filterByMajorMinor(this.tags,
                                                              t.getMajorVersion(),
                                                              t.getMinorVersion());
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
        String line = "   +";
        for (Tag t : tags) {
          line += "--[" + t.getMajorVersion() + "." + t.getMinorVersion() + "." + t.getPatchVersion() + "]";
        }
        log.info("   |");
        log.info(line);
      }
    }
    log.info("");
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
        String line = "  +";
        for (Tag t : tags) {
          line += "--[" + t.getMajorVersion() + "." + t.getMinorVersion() + "." + t.getPatchVersion() + "]";
        }
        sink.text("  |\n");
        sink.text(line + "\n");
      }
    }
    sink.verbatim_();
    sink.section2_();
  }
}
