package utils.install;

import cm.util.*;
import cm.persist.*;
import cm.exp.INeedOutFile;

import utils.*;

import ob.*;

import java.io.*;
import java.sql.*;
import java.util.*;

/** Class for generating the description of all the db tables.
 *
 *  @version $Id$
 *  @author Madhu */
public class GenDbDesc implements INeedOutFile {

	public void start (String outFName, boolean isAuto, String args[]) throws Exception {
    DbColumn[] tabCols;
    DbConstraint[] tabCons;
    Hashtable currColsHt, currConsHt;
    String dbUser  = OBEnv.getDbUser().toUpperCase();

    File file = new File(outFName);
    if (file.exists()) {
      if (!DoUpdate.getBooleanFromUser("The file \"" + file + "\" exists. " +
                                       "Would you like to overwrite it?"))
      {
        System.err.println("Request cancelled.");
        System.exit(1);
      }
    }

    fw_ = new FileWriter(file);

    String ver = Env.getPCVersion();
    // TODO - remove log_
    log_ = new Log(ver + "_DB_DESCRIPTION");
    heading_ = ("ProChannel Release " + ver + " Database Table Definitions");
    java.util.Date d = new java.util.Date();
    lastUpdDtStr_ = convertDate(d);
    DbConnection c = DbPool.getCon();
    UpgradeDb udb = new UpgradeDb();

    long m = System.currentTimeMillis();
    Hashtable currTableNamesHt = udb.getCurrDbTableNames(c);
    long b = System.currentTimeMillis();
    log_.log(5, "Time taken to get current table names: " + (b - m) + "ms");
	  String[] tabNames = new String[currTableNamesHt.size()];
    Enumeration e = currTableNamesHt.keys();
    int i = 0;
    long a = System.currentTimeMillis();
    while (e.hasMoreElements()) {
      tabNames[i] = (String)e.nextElement();
      i++;
    }
    b = System.currentTimeMillis();
    log_.log(5, "Time taken to get enumerate table names: " + (b - a) + "ms");
    a = System.currentTimeMillis();
    new StringQuickSort(tabNames); // sort into alphabetical order
    b = System.currentTimeMillis();
    log_.log(5, "Time taken to sort current table names: " + (b - a) + "ms");

    fw_.write(getFileHeader());
    fw_.flush();

    writeTableNames(tabNames);

    for (i = 0; i < tabNames.length; i++) {
      a = System.currentTimeMillis();
      currColsHt = udb.getOldColumns (c, tabNames[i]);
      //currColsHt = udb.getOldColumns (c, dbUser, tabNames[i]);
      b = System.currentTimeMillis();
      log_.log(5, "Time taken to get " + tabNames[i] + "'s cols: " + (b - a) + "ms");
      // TODO - remove log_ arg to getOldConstraints.. it does nothing.
      //currConsHt =  udb.getOldConstraints (c, dbUser, tabNames[i], log_);
      currConsHt =  udb.getOldConstraints (c, tabNames[i]);
      a = System.currentTimeMillis();
      tabCols = getColsInOrder(currColsHt);
      b = System.currentTimeMillis();
      log_.log(5, "Time taken to order " + tabNames[i] + "'s cols: " + (b - a) + "ms");
      tabCons = getConsInOrder(currConsHt);
      a = System.currentTimeMillis();
      writeTableDesc(tabNames[i], tabCols, tabCons);
      //writeTableDesc(tabNames[i], tabCols);
      b = System.currentTimeMillis();
      log_.log(5, "Time taken to write " + tabNames[i] + "'s cols: " + (b - a) + "ms");
      msgOut_.write((int)(((double)i/tabNames.length)*100) + "% done.\t\t\r");
    }
    long n = System.currentTimeMillis();
    msgOut_.writeln("Total time taken: " + (n - m) + "ms");
    fw_.close();
	}	//	end start()

  private DbColumn[] getColsInOrder(Hashtable colsHt) {
    DbColumn col;
    int ordinalPosn;
    DbColumn[] dbCols = new DbColumn[colsHt.size()];

    Enumeration e1 = colsHt.elements();
    while (e1.hasMoreElements()) {
      col = (DbColumn)e1.nextElement();
      ordinalPosn = col.getOrdinalPosn();
      dbCols[ordinalPosn-1] = col;
    }
    return dbCols;
  }

  private DbConstraint[] getConsInOrder(Hashtable consHt) {
    DbConstraint[] dbCons = new DbConstraint[consHt.size()];
    DbConstraint[] retCons = null;
    Vector consV = null;

    Enumeration e1 = consHt.elements();
    for (int i = 0; i < consHt.size(); i++) {
      dbCons[i] = (DbConstraint)e1.nextElement();
      if (dbCons[i].getType().equals("IGNORE")) {
        continue;
      }
      if (consV == null) {
        consV = new Vector();
      }
      consV.addElement(dbCons[i]);
    }
    if (consV != null && consV.size() > 0) {
      retCons = new DbConstraint[consV.size()];
      consV.copyInto(retCons);
    }
    return retCons;
  }

	public void writeTableNames(String[] tabNames) throws IOException {
    StringBuffer buf = new StringBuffer();
    buf.append("<p><table cellspacing=0 cellpadding=1 border=1>");

    for (int i = 0; i < tabNames.length; i++) {
      if (i%3 == 0) {
        if (i != 0) {
          buf.append("</tr>");
        }
        buf.append("<tr>"); // start a new row after every 4 cols
      }
      buf.append("<td><A HREF=\"#" + tabNames[i] + "\">")
         .append(tabNames[i] + "</A></td>");
    }
    buf.append("</tr></table></p>");
    fw_.write(buf.toString());
    fw_.flush();
  }

	//public void writeTableDesc(String tabName, DbColumn[] tabCols)
	public void writeTableDesc(String tabName, DbColumn[] tabCols,
	                           DbConstraint[] tabCons)
	  throws IOException
  {
    StringBuffer buf = new StringBuffer();
    buf.append("<A NAME=" + getQuoted(tabName) + ">")
       .append("<p><H4>" + tabName + "</A></H4></p>")
       .append("<p><table cellspacing=0 cellpadding=5 border=0>")
       .append(getColHeader());
    for (int i = 0; i < tabCols.length; i++) {
      buf.append(formatColLine(tabCols[i]));
    }
    buf.append("</table></p>");

    // append constraint info
    if (tabCons != null && tabCons.length > 0) {
      buf.append("<p><table cellspacing=0 cellpadding=5 border=1>")
         .append(getConsHeader());
      for (int i = 0; i < tabCons.length; i++) {
        buf.append(formatConsLine(tabCons[i]));
      }
      buf.append("</table></p>");
    }

    buf.append("<p><FONT SIZE=2><A HREF=\"#Top\">Go to Top</A><br>")
       .append("<A HREF=" + getQuoted(DIAGS_LINK) + ">")
       .append("Go to DM Diagrams</FONT></A></p>");

    fw_.write(buf.toString()); 
    fw_.flush();
		return;
	}	//	end method writeTableDesc()

  private static String convertDate(java.util.Date dt) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTime(dt);
    return ((calendar.get(calendar.MONTH)+1) + "/" +
            calendar.get(calendar.DATE) + "/" +
            calendar.get(calendar.YEAR));

  }

  private String getQuoted(String str) {
    return ("\"" + str + "\"");
  }

  private String getFileHeader() {
    StringBuffer sb = new StringBuffer();
    sb.append("<html><HEAD><A NAME=\"Top\">")
      .append("<TITLE>" + heading_ + "</TITLE>")
      .append("</HEAD>")
      .append("<BODY BGCOLOR=WHITE LINK=BLUE VLINK=MAGENTA>")
      .append("<H2><CENTER>" + heading_ + "</H2></CENTER>")
      .append("<UL>")
      .append("<FONT SIZE=2><I>Last Updated: " + lastUpdDtStr_ + "</I></FONT><P>")
      .append("<FONT SIZE=1>");
    return sb.toString();
  }

  private String getColHeader() {
    StringBuffer sb = new StringBuffer();
    sb.append("<tr><td><H5>Name</H5></td>" + "<td><H5>Null?</H5></td>")
      .append("<td><H5>Type</H5></td></tr>");
    return sb.toString();
  }

  private String getConsHeader() {
    StringBuffer sb = new StringBuffer();
    sb.append("<tr><td><H5>Constraint Name</H5></td>" + "<td><H5>Type</H5></td>")
      .append("<td><H5>Columns</H5></td></tr>");
    return sb.toString();
  }

  private String formatColLine(DbColumn col) {
    StringBuffer sb = new StringBuffer();
    sb.append("<tr><td>" + col.getColName() + "</td>");
    String ccons = col.getColConstr();
    sb.append((ccons == null ? "<td></td>" : "<td>" + ccons + "</td>"))
      .append("<td>" + col.getDataType() + "</td></tr>");
    return (sb.toString());
  }

  private String formatConsLine(DbConstraint cons) {
    StringBuffer sb = new StringBuffer();
    sb.append("<tr><td>" + cons.getName() + "</td>")
      .append("<td>" + cons.getType() + "</td>")
      .append("<td>" + cons.getText() + "</td></tr>");
    return (sb.toString());
  }

	public String getStatusMessage () {
		throw new RuntimeException ("This class does not support" +
                                " getStatusMessage()");
	}

	public boolean wasSuccessful () {
		return (true);
	}

  public void setCurrentType(String p) {
    currentType_ = p;
  }

  /** If this is being invoked via ProChannel instead of the command line,
   *  we suppress messages to the console. */
  public void setIsInCommandLineMode(boolean b) {
  }

  private FileWriter fw_;
  private String heading_;
  private String lastUpdDtStr_;

  private String currentType_;
  private Log log_;
  private static final String DIAGS_LINK = "PC40DataModelDiagsLink.htm";
  private IHandleTextMessages msgOut_    = new StdErrMessageHandler();

}	//	end class GenDbDesc
