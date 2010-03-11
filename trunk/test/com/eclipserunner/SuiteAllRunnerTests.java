package com.eclipserunner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.eclipserunner.model.RunnerModelTest;
import com.eclipserunner.utils.SelectionUtilsTest;
import com.eclipserunner.views.actions.validator.LaunchConfigurationNameValidatorTest;

@RunWith(Suite.class)
@SuiteClasses(value = {
	RunnerModelTest.class,
	SelectionUtilsTest.class,
	LaunchConfigurationNameValidatorTest.class
})
public class SuiteAllRunnerTests {

}
