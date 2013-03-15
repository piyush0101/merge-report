package com.thoughtworks.svn.merge

class MergeRange(start: Long, end: Long) {

  def contains(revision : Long) : Boolean = {
    revision >= start && revision <= end
  }

}
