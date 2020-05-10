//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           Report.java
//! @description    Represents a score paired with an executable file name for
//!                 serialization.
//!

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Report
{
  private String filePath_;
  private ReportScoreAdaptor reportScore_;

  public Report()
  {
  }

  public Report(String filePath, PortableExecutableScore score)
  {
    filePath_ = filePath;
    reportScore_ = new ReportScoreAdaptor(score);
  }

  public String getFilePath()
  {
    return filePath_;
  }

  public void setFilePath(String filePath)
  {
    filePath_ = filePath;
  }

  public ReportScoreAdaptor getReportScore()
  {
    return reportScore_;
  }

  public void setReportScore(ReportScoreAdaptor reportScore)
  {
    reportScore_ = reportScore;
  }

  @Override
  public String toString()
  {
    return String.format("Path: %s\n\n%s", filePath_, reportScore_.toString());
  }

  // Serializes this report to an XML string
  public String toXml()
  {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    XMLEncoder encoder = new XMLEncoder(os);
    encoder.setExceptionListener(
      e -> System.err.println("Exception while encoding report: " +
        e.toString()));
    encoder.writeObject(this);
    encoder.close();
    return os.toString();
  }

  // Creates a new ReportScoreAdaptor object from the given XML string
  public static Report fromXml(String xml)
  {
    ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes());
    XMLDecoder decoder = new XMLDecoder(is);
    decoder.setExceptionListener(
      e -> System.err.println("Exception while decoding report: " +
        e.toString()));
    Report result = (Report)decoder.readObject();
    decoder.close();
    return result;
  }
}
