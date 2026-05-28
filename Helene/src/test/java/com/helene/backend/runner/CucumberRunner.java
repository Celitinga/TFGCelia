package com.helene.backend.runner;

import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
        key = "cucumber.plugin",
        value = "pretty, html:target/cucumber-reports/report.html"
)
@ConfigurationParameter(
        key = "cucumber.glue",
        value = "com.helene.backend.steps, com.helene.backend.config"
)
@ConfigurationParameter(
        key = "cucumber.publish.quiet",
        value = "true"
)
public class CucumberRunner {}