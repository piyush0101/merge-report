package com.thoughtworks.svn.merge

trait RepositoryConnector {

  def getMergeInfo(source: String, target: String) : MergeInfo
  def getLog(branch: String) : List[LogEntry]

}
