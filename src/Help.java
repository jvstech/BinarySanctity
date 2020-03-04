//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           Help.java
//! @description    Provides a description of Binary Sanctity.
//!

public class Help
{
  public static void about()
  {
    System.out.println("B I N A R Y   S A N C T I T Y");
    System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-\n");
    System.out.println("Binary Sanctity is an at-a-glance malware analysis " +
      "and scoring utility.\nGiven a file, common malware characteristics " +
      "are checked and analyzed. Each\nanalysis section is assigned a score. " +
      "These scores are combined and amplified\nto generate a final, overall " +
      "score. This score does not guarantee that a file\nis malware -- it is " +
      "only meant to be a possible indicator of malicious\nbehavior.");
  }
}
