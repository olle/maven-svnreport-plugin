package com.studiomediatech.maven.report;

import java.io.File;
import java.util.Locale;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;

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
    getLog().info("SCM URL: " + this.project.getScm().getUrl());
    getLog().info(SEPARATOR);
  }
}
