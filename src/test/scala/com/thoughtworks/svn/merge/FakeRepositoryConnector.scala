package com.thoughtworks.svn.merge

class FakeRepositoryConnector extends RepositoryConnector {
  def getMergeInfo(branch: String, source: String): MergeInfo = null
  def getLog(branch: String): List[LogEntry] = null
}
