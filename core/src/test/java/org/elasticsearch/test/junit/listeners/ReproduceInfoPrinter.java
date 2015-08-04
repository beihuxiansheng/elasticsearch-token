begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.junit.listeners
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|junit
operator|.
name|listeners
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|ReproduceErrorMessageBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|TraceFormatting
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|Strings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|logging
operator|.
name|ESLogger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|logging
operator|.
name|Loggers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ElasticsearchTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|InternalTestCluster
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|internal
operator|.
name|AssumptionViolatedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|notification
operator|.
name|Failure
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|notification
operator|.
name|RunListener
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|SysGlobals
operator|.
name|SYSPROP_ITERATIONS
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|SysGlobals
operator|.
name|SYSPROP_PREFIX
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|SysGlobals
operator|.
name|SYSPROP_TESTMETHOD
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ElasticsearchIntegrationTest
operator|.
name|TESTS_CLUSTER
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|ElasticsearchRestTestCase
operator|.
name|REST_TESTS_BLACKLIST
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|ElasticsearchRestTestCase
operator|.
name|REST_TESTS_SPEC
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|ElasticsearchRestTestCase
operator|.
name|REST_TESTS_SUITE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|ElasticsearchRestTestCase
operator|.
name|Rest
import|;
end_import

begin_comment
comment|/**  * A {@link RunListener} that emits to {@link System#err} a string with command  * line parameters allowing quick test re-run under MVN command line.  */
end_comment

begin_class
DECL|class|ReproduceInfoPrinter
specifier|public
class|class
name|ReproduceInfoPrinter
extends|extends
name|RunListener
block|{
DECL|field|logger
specifier|protected
specifier|final
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|ElasticsearchTestCase
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|testStarted
specifier|public
name|void
name|testStarted
parameter_list|(
name|Description
name|description
parameter_list|)
throws|throws
name|Exception
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"Test {} started"
argument_list|,
name|description
operator|.
name|getDisplayName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testFinished
specifier|public
name|void
name|testFinished
parameter_list|(
name|Description
name|description
parameter_list|)
throws|throws
name|Exception
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"Test {} finished"
argument_list|,
name|description
operator|.
name|getDisplayName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * true if we are running maven integration tests (mvn verify)      */
DECL|method|inVerifyPhase
specifier|static
name|boolean
name|inVerifyPhase
parameter_list|()
block|{
return|return
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.verify.phase"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|testFailure
specifier|public
name|void
name|testFailure
parameter_list|(
name|Failure
name|failure
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Ignore assumptions.
if|if
condition|(
name|failure
operator|.
name|getException
argument_list|()
operator|instanceof
name|AssumptionViolatedException
condition|)
block|{
return|return;
block|}
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|inVerifyPhase
argument_list|()
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"REPRODUCE WITH: mvn verify -Pdev -Dskip.unit.tests"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|b
operator|.
name|append
argument_list|(
literal|"REPRODUCE WITH: mvn test -Pdev"
argument_list|)
expr_stmt|;
block|}
name|MavenMessageBuilder
name|mavenMessageBuilder
init|=
operator|new
name|MavenMessageBuilder
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|mavenMessageBuilder
operator|.
name|appendAllOpts
argument_list|(
name|failure
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
comment|//Rest tests are a special case as they allow for additional parameters
if|if
condition|(
name|failure
operator|.
name|getDescription
argument_list|()
operator|.
name|getTestClass
argument_list|()
operator|.
name|isAnnotationPresent
argument_list|(
name|Rest
operator|.
name|class
argument_list|)
condition|)
block|{
name|mavenMessageBuilder
operator|.
name|appendRestTestsProperties
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|traces
specifier|protected
name|TraceFormatting
name|traces
parameter_list|()
block|{
name|TraceFormatting
name|traces
init|=
operator|new
name|TraceFormatting
argument_list|()
decl_stmt|;
try|try
block|{
name|traces
operator|=
name|RandomizedContext
operator|.
name|current
argument_list|()
operator|.
name|getRunner
argument_list|()
operator|.
name|getTraceFormatting
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// Ignore if no context.
block|}
return|return
name|traces
return|;
block|}
DECL|class|MavenMessageBuilder
specifier|protected
specifier|static
class|class
name|MavenMessageBuilder
extends|extends
name|ReproduceErrorMessageBuilder
block|{
DECL|method|MavenMessageBuilder
specifier|public
name|MavenMessageBuilder
parameter_list|(
name|StringBuilder
name|b
parameter_list|)
block|{
name|super
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|appendAllOpts
specifier|public
name|ReproduceErrorMessageBuilder
name|appendAllOpts
parameter_list|(
name|Description
name|description
parameter_list|)
block|{
name|super
operator|.
name|appendAllOpts
argument_list|(
name|description
argument_list|)
expr_stmt|;
if|if
condition|(
name|description
operator|.
name|getMethodName
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|//prints out the raw method description instead of methodName(description) which filters out the parameters
name|super
operator|.
name|appendOpt
argument_list|(
name|SYSPROP_TESTMETHOD
argument_list|()
argument_list|,
literal|"\""
operator|+
name|description
operator|.
name|getMethodName
argument_list|()
operator|+
literal|"\""
argument_list|)
expr_stmt|;
block|}
return|return
name|appendESProperties
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|appendEnvironmentSettings
specifier|public
name|ReproduceErrorMessageBuilder
name|appendEnvironmentSettings
parameter_list|()
block|{
comment|// we handle our own environment settings
return|return
name|this
return|;
block|}
comment|/**          * Append a single VM option.          */
annotation|@
name|Override
DECL|method|appendOpt
specifier|public
name|ReproduceErrorMessageBuilder
name|appendOpt
parameter_list|(
name|String
name|sysPropName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|sysPropName
operator|.
name|equals
argument_list|(
name|SYSPROP_ITERATIONS
argument_list|()
argument_list|)
condition|)
block|{
comment|// we don't want the iters to be in there!
return|return
name|this
return|;
block|}
if|if
condition|(
name|sysPropName
operator|.
name|equals
argument_list|(
name|SYSPROP_TESTMETHOD
argument_list|()
argument_list|)
condition|)
block|{
comment|//don't print out the test method, we print it ourselves in appendAllOpts
comment|//without filtering out the parameters (needed for REST tests)
return|return
name|this
return|;
block|}
if|if
condition|(
name|sysPropName
operator|.
name|equals
argument_list|(
name|SYSPROP_PREFIX
argument_list|()
argument_list|)
condition|)
block|{
comment|// we always use the default prefix
return|return
name|this
return|;
block|}
if|if
condition|(
name|Strings
operator|.
name|hasLength
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
name|super
operator|.
name|appendOpt
argument_list|(
name|sysPropName
argument_list|,
name|value
argument_list|)
return|;
block|}
return|return
name|this
return|;
block|}
DECL|method|appendESProperties
specifier|public
name|ReproduceErrorMessageBuilder
name|appendESProperties
parameter_list|()
block|{
name|appendProperties
argument_list|(
literal|"es.logger.level"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|inVerifyPhase
argument_list|()
condition|)
block|{
comment|// these properties only make sense for unit tests
name|appendProperties
argument_list|(
literal|"es.node.mode"
argument_list|,
literal|"es.node.local"
argument_list|,
name|TESTS_CLUSTER
argument_list|,
name|InternalTestCluster
operator|.
name|TESTS_ENABLE_MOCK_MODULES
argument_list|)
expr_stmt|;
block|}
name|appendProperties
argument_list|(
literal|"tests.assertion.disabled"
argument_list|,
literal|"tests.security.manager"
argument_list|,
literal|"tests.nightly"
argument_list|,
literal|"tests.jvms"
argument_list|,
literal|"tests.client.ratio"
argument_list|,
literal|"tests.heap.size"
argument_list|,
literal|"tests.bwc"
argument_list|,
literal|"tests.bwc.version"
argument_list|)
expr_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.jvm.argline"
argument_list|)
operator|!=
literal|null
operator|&&
operator|!
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.jvm.argline"
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|appendOpt
argument_list|(
literal|"tests.jvm.argline"
argument_list|,
literal|"\""
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.jvm.argline"
argument_list|)
operator|+
literal|"\""
argument_list|)
expr_stmt|;
block|}
name|appendOpt
argument_list|(
literal|"tests.locale"
argument_list|,
name|Locale
operator|.
name|getDefault
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|appendOpt
argument_list|(
literal|"tests.timezone"
argument_list|,
name|TimeZone
operator|.
name|getDefault
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|appendRestTestsProperties
specifier|public
name|ReproduceErrorMessageBuilder
name|appendRestTestsProperties
parameter_list|()
block|{
return|return
name|appendProperties
argument_list|(
name|REST_TESTS_SUITE
argument_list|,
name|REST_TESTS_SPEC
argument_list|,
name|REST_TESTS_BLACKLIST
argument_list|)
return|;
block|}
DECL|method|appendProperties
specifier|protected
name|ReproduceErrorMessageBuilder
name|appendProperties
parameter_list|(
name|String
modifier|...
name|properties
parameter_list|)
block|{
for|for
control|(
name|String
name|sysPropName
range|:
name|properties
control|)
block|{
if|if
condition|(
name|Strings
operator|.
name|hasLength
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
name|sysPropName
argument_list|)
argument_list|)
condition|)
block|{
name|appendOpt
argument_list|(
name|sysPropName
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
name|sysPropName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|this
return|;
block|}
block|}
block|}
end_class

end_unit

