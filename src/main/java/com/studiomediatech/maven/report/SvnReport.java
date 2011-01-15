package com.studiomediatech.maven.report;

import java.io.File;
import java.util.Locale;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * @author olle
 * @goal svn-report
 * @phase site
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
  private MavenProject project;

  /**
   * Doxia Site Renderer.
   * 
   * @component
   * @required
   * @readonly
   */
  private Renderer renderer;

  /**
   * The report output directory.
   * 
   * @parameter expression="${project.build.directory}"
   * @required
   * @readonly
   */
  private File outputDirectory;

  /**
   * @component
   * @required
   * @readonly
   */
  private SVNClientManager svnClientManager;

  @Override
  public String getOutputName() {
    return "svn-report";
  }

  @Override
  public String getName(Locale locale) {
    return "svn-report";
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
    getLog().info(SEPARATOR);
    getLog().info("Generating \"Subversion\" report.");
    getLog().info("SCM CONNECTION: " + this.project.getScm().getConnection());

    try {
      SVNURL svnurl = SVNURL.parseURIEncoded(this.project.getScm().getConnection());
      this.svnClientManager.getLogClient().doList(svnurl,
                                                  SVNRevision.UNDEFINED,
                                                  SVNRevision.UNDEFINED,
                                                  false,
                                                  false,
                                                  new ISVNDirEntryHandler() {
                                                    @Override
                                                    public void handleDirEntry(SVNDirEntry dirEntry) {
                                                      getLog().info("-> " + dirEntry.getName());
                                                    }
                                                  });
    }
    catch (SVNException e) {
      e.printStackTrace();
    }

    getLog().info(SEPARATOR);
  }
}
