package com.thoughtworks.svn.merge

import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers

class MergeRangeCompositeTest extends Spec with ShouldMatchers {

  describe("merge range composite") {
    it("contains should return false if list of merge ranges is empty") {
      val composite = new MergeRangeComposite(List())
      composite.contains(1) should be(false)
    }

    it("contains should return false if revision is not contained in any of merge ranges") {
      val composite = new MergeRangeComposite(List(new MergeRange(1,5), new MergeRange(6,8)))
      composite.contains(10) should be(false)
    }

    it("contains should return true if revision is contained in one of merge ranges") {
      val composite = new MergeRangeComposite(List(new MergeRange(1,5), new MergeRange(6,8)))
      composite.contains(4) should be(true)
    }
  }

}
