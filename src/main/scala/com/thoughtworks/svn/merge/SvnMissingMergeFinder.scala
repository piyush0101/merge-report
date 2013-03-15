package com.thoughtworks.svn.merge

import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.io.SVNRepositoryFactory
import org.tmatesoft.svn.core.wc.SVNWCUtil
import collection.mutable

object SvnMissingMergeFinder {
  def find : List[LogEntry] = {
    val svnUrl = SVNURL.create("svn", null, "localhost", 3690, "myrepo/git-training", false)

    val repository = SVNRepositoryFactory.create(svnUrl)

    val authManager = SVNWCUtil.createDefaultAuthenticationManager("piyush", "ora24sap")
    repository.setAuthenticationManager(authManager)

    val connector = new SvnRepositoryConnector(repository)

    val finder: SvnMissingMergeFinder = new SvnMissingMergeFinder(connector)
    finder.findUnmergedRevisions("trunk", "mybranch").toList
  }
}

class SvnMissingMergeFinder(connector: RepositoryConnector) {

  def findUnmergedRevisions(source: String, target: String): mutable.LinkedList[LogEntry] = {
    if (source == null || target == null) return new mutable.LinkedList[LogEntry]()
    if (source == "" || target == "") return new mutable.LinkedList[LogEntry]()

    val logEntries = connector.getLog("trunk")
    val mergeInfo = connector.getMergeInfo("trunk", "mybranch")

    var unmergedList = new mutable.LinkedList[LogEntry]()

    if (mergeInfo == null) {
      logEntries.foreach((entry: LogEntry) => unmergedList = unmergedList :+ entry)
    }

    else logEntries.foreach((entry: LogEntry)
    => if (!new MergeRangeComposite(mergeInfo.mergeRange.toList).contains(entry.revision)) unmergedList = unmergedList :+ entry)

    unmergedList
  }
}
