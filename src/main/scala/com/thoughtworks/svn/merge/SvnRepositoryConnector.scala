package com.thoughtworks.svn.merge

import org.tmatesoft.svn.core.wc.SVNWCUtil
import org.tmatesoft.svn.core._
import org.tmatesoft.svn.core.io.{SVNRepository, SVNRepositoryFactory}
import collection.mutable

class SvnRepositoryConnector(repository: SVNRepository) extends RepositoryConnector {

  def getMergeInfo(source: String, target: String): MergeInfo = {
    val inheritanceMode = SVNMergeInfoInheritance.EXPLICIT
    val mergeInfos = repository.getMergeInfo(Array("branches/" + target), 13L, inheritanceMode, false)
    val mergeInfoArray = mergeInfos.values().toArray(new Array[SVNMergeInfo](mergeInfos.values().size()))

    val allMergeInfos = findAllMergeInfos(mergeInfoArray)
    val mergeInfoOption = allMergeInfos.find(p => p.source.contains(source))

    mergeInfoOption match {
      case Some(info) => info
      case None => null
    }
  }

  def getLog(branch: String): List[LogEntry] = {
    val paths = Array(branch)
    val startRevision = 1L
    val endRevision = 13L
    val discoverChangedPaths = true
    val limit = 100
    val logEntryHandler = new SvnMergeLogEntryHandler
    val strictNode = true

    repository.log(paths, startRevision, endRevision, discoverChangedPaths, strictNode, limit, logEntryHandler)

    val transientLogEntryArray: Array[SVNLogEntry] = new Array[SVNLogEntry](logEntryHandler.getLogEntries.size())

    val list: List[SVNLogEntry] = logEntryHandler
      .getLogEntries
      .toArray(transientLogEntryArray)
      .toList

    list.map((e: SVNLogEntry) => LogEntry(e.getMessage, e.getRevision))
  }

  private def findAllMergeInfos(svnMergeInfos: Array[SVNMergeInfo]): mutable.LinkedList[MergeInfo] = {
    var mergeInfoList = new mutable.LinkedList[MergeInfo]()
    svnMergeInfos.foreach((svnMergeInfo: SVNMergeInfo) => {
      val svnMergeInfoList = svnMergeInfo.getMergeSourcesToMergeLists
      val mergeInfoIterator = svnMergeInfoList.entrySet().iterator()
      while (mergeInfoIterator.hasNext) {
        val entry = mergeInfoIterator.next()
        val mergeRangeList = entry.getValue.asInstanceOf[SVNMergeRangeList]
        val svnMergeRanges = mergeRangeList.getRanges
        val mergeRanges = svnMergeRanges.map((range: SVNMergeRange) => new MergeRange(range.getStartRevision, range.getEndRevision))
        mergeInfoList = mergeInfoList :+ MergeInfo(entry.getKey.asInstanceOf[String], mergeRanges)
      }
    })
    mergeInfoList
  }
}