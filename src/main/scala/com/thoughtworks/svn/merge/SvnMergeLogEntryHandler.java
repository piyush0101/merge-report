package com.thoughtworks.svn.merge;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.ArrayList;
import java.util.List;

public class SvnMergeLogEntryHandler implements ISVNLogEntryHandler {

    private List<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();

    public void handleLogEntry(SVNLogEntry svnLogEntry) throws SVNException {
        if (svnLogEntry != null) {
            logEntries.add(svnLogEntry);
        }
    }

    public List<SVNLogEntry> getLogEntries() {
        return logEntries;
    }
}
