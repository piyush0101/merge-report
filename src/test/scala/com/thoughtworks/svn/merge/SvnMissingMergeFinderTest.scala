package com.thoughtworks.svn.merge

import org.scalatest.{Spec, FunSuite}
import org.scalatest.matchers.{MustMatchers, ShouldMatchers}

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SvnMissingMergeFinderTest extends Spec with MustMatchers with ShouldMatchers {
  describe("svn missing merge finder") {

    it("should return an empty list of entries given a null source and target repos") {
      val connector = new FakeRepositoryConnector
      val missingMergeFinder = new SvnMissingMergeFinder(connector)
      val unmergedRevisions = missingMergeFinder.findUnmergedRevisions(null, null)

      unmergedRevisions should  have size (0)
    }

    it("should return an empty list of entries given a an empty source and target repos") {
      val connector = new FakeRepositoryConnector
      val missingMergeFinder = new SvnMissingMergeFinder(connector)
      val unmergedRevisions = missingMergeFinder.findUnmergedRevisions("", "")

      unmergedRevisions should have size(0)
    }

    it("should return all revisions as unmerged if no revision has been merged") {
      val connector = new RepositoryConnector {
        def getLog(branch: String): List[LogEntry] =
          List(LogEntry("first commit", 1), LogEntry("second commit", 2))

        def getMergeInfo(source: String, target: String): MergeInfo =
          null
      }
      val missingMergeFinder = new SvnMissingMergeFinder(connector)
      val unmergedRevisions = missingMergeFinder.findUnmergedRevisions("trunk", "mybranch")

      unmergedRevisions should have size(2)
    }

    it("should return all revisions as merged if all revisions have been merged in a single commit") {
      val connector = new RepositoryConnector {
        def getLog(branch: String): List[LogEntry] =
          List(LogEntry("first commit", 1), LogEntry("second commit", 2))

        def getMergeInfo(source: String, target: String): MergeInfo =
          MergeInfo("trunk", Array(new MergeRange(1, 2)))
      }

      val missingMergeFinder = new SvnMissingMergeFinder(connector)
      val unmergedRevisions = missingMergeFinder.findUnmergedRevisions("trunk", "mybranch")

      unmergedRevisions should have size(0)
    }

    it("should return all revisions as merged if all revisions have been merged in separate commits") {
      val connector = new RepositoryConnector {
        def getLog(branch: String): List[LogEntry] =
          List(LogEntry("first commit", 1), LogEntry("second commit", 2))

        def getMergeInfo(source: String, target: String): MergeInfo =
          MergeInfo("trunk", Array(new MergeRange(1, 1), new MergeRange(2,2)))
      }

      val missingMergeFinder = new SvnMissingMergeFinder(connector)
      val unmergedRevisions = missingMergeFinder.findUnmergedRevisions("trunk", "mybranch")

      unmergedRevisions should have size(0)
    }

    it("should return revisions that have not been merged") {
      val connector = new RepositoryConnector {
        def getLog(branch: String): List[LogEntry] =
          List(LogEntry("first commit", 1), LogEntry("second commit", 2))

        def getMergeInfo(source: String, target: String): MergeInfo =
          MergeInfo("trunk", Array(new MergeRange(1, 1)))
      }

      val missingMergeFinder = new SvnMissingMergeFinder(connector)
      val unmergedRevisions = missingMergeFinder.findUnmergedRevisions("trunk", "mybranch")

      unmergedRevisions should have size(1)
      unmergedRevisions should contain(LogEntry("second commit", 2))
    }

  }

}
