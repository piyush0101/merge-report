package com.thoughtworks.svn.merge

class MergeRangeComposite(mergeRanges: List[MergeRange]) {

  def contains(revision: Long): Boolean = {
    mergeRanges.exists(range => range.contains(revision))
  }
}
