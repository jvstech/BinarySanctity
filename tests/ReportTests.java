import org.junit.jupiter.api.Test;

class ReportTests
{
  @Test
  void adaptPEScore()
    throws Exception
  {
    PortableExecutableFileChannel malwareFile =
      PortableExecutableFileChannel.create(TestUtil.getHupigonMalwareChannel());
    PortableExecutableScore malwareScore =
      new PortableExecutableScore(malwareFile);
    AggregateScoreAdaptor scoreAdaptor =
      new AggregateScoreAdaptor(malwareScore);
    for (Score score : scoreAdaptor.getFlattenedScores())
    {
      System.out.println(score.getTitle());
    }
  }

  @Test
  void reportScoreXml()
    throws Exception
  {
    PortableExecutableFileChannel malwareFile =
      PortableExecutableFileChannel.create(TestUtil.getHupigonMalwareChannel());
    PortableExecutableScore malwareScore =
      new PortableExecutableScore(malwareFile);
    ReportScoreAdaptor scoreAdaptor = new ReportScoreAdaptor(malwareScore);
    System.out.println(scoreAdaptor.toXml());
  }
}
