package com.thoughtworks.svn.merge

import org.scalatra._
import scalate.ScalateSupport

class MergeReportServlet extends MergereportStack {

  get("/") {
       contentType="text/html"
       ssp("/MissingMerges")
  }
}
