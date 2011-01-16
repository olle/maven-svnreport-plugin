package com.studiomediatech.svn;

import java.util.ArrayList;
import java.util.List;

import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

public class SvnUtils {

  public static String makeCleanUrl(String connection) {
    return connection.replace("scm:svn:", "");
  }

  public static String makeTagsUrl(String url) {
    int index = url.indexOf("/trunk");
    if (index != -1) {
      return url.substring(0, index) + "/tags";
    }
    if (url.endsWith("/")) {
      return url.substring(0, url.length() - 1) + "/tags";
    }
    else {
      return url + "/tags";
    }
  }

  public static List<String> fetchTags(String tagsUrl) {
    try {
      setupSvnKitLib();
      final ArrayList<String> tagList = new ArrayList<String>();
      SVNClientManager manager = SVNClientManager.newInstance();
      SVNLogClient client = manager.getLogClient();
      final SVNURL url = SVNURL.parseURIDecoded(tagsUrl);
      client.doList(url,
                    SVNRevision.UNDEFINED,
                    SVNRevision.UNDEFINED,
                    true,
                    false,
                    new ISVNDirEntryHandler() {
                      @Override
                      public void handleDirEntry(SVNDirEntry dirEntry) throws SVNException {
                        if (!dirEntry.getURL().equals(url)) {
                          tagList.add(dirEntry.getName());
                        }
                      }
                    });
      return tagList;
    }
    catch (SVNException e) {
      throw new RuntimeException("Unable to fetch tags list.", e);
    }
  }

  private static void setupSvnKitLib() {
    // For using over http:// and https://
    DAVRepositoryFactory.setup();
    // For using over svn:// and svn+xxx://
    SVNRepositoryFactoryImpl.setup();
    // For using over file:///
    FSRepositoryFactory.setup();
  }
}
