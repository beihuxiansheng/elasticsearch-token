begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  *  */
end_comment

begin_package
DECL|package|org.elasticsearch.bootstrap
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|bootstrap
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|SuppressForbidden
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|NodeValidationException
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
name|ESTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|bootstrap
operator|.
name|BootstrapChecks
operator|.
name|ES_ENFORCE_BOOTSTRAP_CHECKS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|allOf
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|hasToString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verifyNoMoreInteractions
import|;
end_import

begin_class
DECL|class|EvilBootstrapChecksTests
specifier|public
class|class
name|EvilBootstrapChecksTests
extends|extends
name|ESTestCase
block|{
DECL|field|esEnforceBootstrapChecks
specifier|private
name|String
name|esEnforceBootstrapChecks
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|ES_ENFORCE_BOOTSTRAP_CHECKS
argument_list|)
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|setEsEnforceBootstrapChecks
argument_list|(
name|esEnforceBootstrapChecks
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testEnforceBootstrapChecks
specifier|public
name|void
name|testEnforceBootstrapChecks
parameter_list|()
throws|throws
name|NodeValidationException
block|{
name|setEsEnforceBootstrapChecks
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|BootstrapCheck
argument_list|>
name|checks
init|=
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|BootstrapCheck
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|check
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|errorMessage
parameter_list|()
block|{
return|return
literal|"error"
return|;
block|}
block|}
argument_list|)
decl_stmt|;
specifier|final
name|Logger
name|logger
init|=
name|mock
argument_list|(
name|Logger
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|NodeValidationException
name|e
init|=
name|expectThrows
argument_list|(
name|NodeValidationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|BootstrapChecks
operator|.
name|check
argument_list|(
literal|false
argument_list|,
name|checks
argument_list|,
name|logger
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Matcher
argument_list|<
name|String
argument_list|>
name|allOf
init|=
name|allOf
argument_list|(
name|containsString
argument_list|(
literal|"bootstrap checks failed"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"error"
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
argument_list|,
name|hasToString
argument_list|(
name|allOf
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|logger
argument_list|)
operator|.
name|info
argument_list|(
literal|"explicitly enforcing bootstrap checks"
argument_list|)
expr_stmt|;
name|verifyNoMoreInteractions
argument_list|(
name|logger
argument_list|)
expr_stmt|;
block|}
DECL|method|testNonEnforcedBootstrapChecks
specifier|public
name|void
name|testNonEnforcedBootstrapChecks
parameter_list|()
throws|throws
name|NodeValidationException
block|{
name|setEsEnforceBootstrapChecks
argument_list|(
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|Logger
name|logger
init|=
name|mock
argument_list|(
name|Logger
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// nothing should happen
name|BootstrapChecks
operator|.
name|check
argument_list|(
literal|false
argument_list|,
name|emptyList
argument_list|()
argument_list|,
name|logger
argument_list|)
expr_stmt|;
name|verifyNoMoreInteractions
argument_list|(
name|logger
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidValue
specifier|public
name|void
name|testInvalidValue
parameter_list|()
block|{
specifier|final
name|String
name|value
init|=
name|randomAsciiOfLength
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|setEsEnforceBootstrapChecks
argument_list|(
name|value
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|enforceLimits
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
specifier|final
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|BootstrapChecks
operator|.
name|check
argument_list|(
name|enforceLimits
argument_list|,
name|emptyList
argument_list|()
argument_list|,
literal|"testInvalidValue"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Matcher
argument_list|<
name|String
argument_list|>
name|matcher
init|=
name|containsString
argument_list|(
literal|"[es.enforce.bootstrap.checks] must be [true] but was ["
operator|+
name|value
operator|+
literal|"]"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
argument_list|,
name|hasToString
argument_list|(
name|matcher
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"set or clear system property es.enforce.bootstrap.checks"
argument_list|)
DECL|method|setEsEnforceBootstrapChecks
specifier|public
name|void
name|setEsEnforceBootstrapChecks
parameter_list|(
specifier|final
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
name|ES_ENFORCE_BOOTSTRAP_CHECKS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|ES_ENFORCE_BOOTSTRAP_CHECKS
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

