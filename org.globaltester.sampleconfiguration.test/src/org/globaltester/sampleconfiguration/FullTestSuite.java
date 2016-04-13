package org.globaltester.sampleconfiguration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({SampleConfigTest.class, GtSampleConfigProjectTest.class})
public class FullTestSuite {

}
