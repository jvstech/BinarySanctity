//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           DuplicateSectionNameScore.java
//! @description    Checks for duplicate PE file section names (a.k.a. more than
//!                 one PE section header sharing the same name).
//!

public class DuplicateSectionNameScore extends Score
{
  public static final String TITLE = "Duplicate section names";

  public DuplicateSectionNameScore(PortableExecutableFileChannel peFile)
  {
    if (peFile == null)
    {
      throw new NullPointerException();
    }

    int score = 0;
    for (String sectionName : peFile.getSectionTable().keySet())
    {
      if (peFile.getSections(sectionName).size() > 1)
      {
        score += 60;
        addDetail("\"" + StringUtil.escapeFull(sectionName) + "\"");
      }
    }

    setValue(score);
  }

  @Override
  public String getTitle()
  {
    return TITLE;
  }

  @Override
  public String getDescription()
  {
    return "Score based on the number of PE sections sharing the same name";
  }

  @Override
  public boolean isSoftMalwareIndication()
  {
    return (getValue() > 0);
  }
}
