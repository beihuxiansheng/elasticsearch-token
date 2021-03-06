begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.memory.breaker
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|memory
operator|.
name|breaker
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|breaker
operator|.
name|CircuitBreaker
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
name|settings
operator|.
name|ClusterSettings
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
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|breaker
operator|.
name|BreakerSettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|breaker
operator|.
name|CircuitBreakerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|breaker
operator|.
name|HierarchyCircuitBreakerService
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
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
name|instanceOf
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
name|is
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
name|notNullValue
import|;
end_import

begin_comment
comment|/**  * Unit tests for the circuit breaker  */
end_comment

begin_class
DECL|class|CircuitBreakerUnitTests
specifier|public
class|class
name|CircuitBreakerUnitTests
extends|extends
name|ESTestCase
block|{
DECL|method|pctBytes
specifier|public
specifier|static
name|long
name|pctBytes
parameter_list|(
name|String
name|percentString
parameter_list|)
block|{
return|return
name|Settings
operator|.
name|EMPTY
operator|.
name|getAsMemory
argument_list|(
literal|""
argument_list|,
name|percentString
argument_list|)
operator|.
name|getBytes
argument_list|()
return|;
block|}
DECL|method|testBreakerSettingsValidationWithValidSettings
specifier|public
name|void
name|testBreakerSettingsValidationWithValidSettings
parameter_list|()
block|{
comment|// parent: {:limit 70}, fd: {:limit 50}, request: {:limit 20}
name|BreakerSettings
name|fd
init|=
operator|new
name|BreakerSettings
argument_list|(
name|CircuitBreaker
operator|.
name|FIELDDATA
argument_list|,
name|pctBytes
argument_list|(
literal|"50%"
argument_list|)
argument_list|,
literal|1.0
argument_list|)
decl_stmt|;
name|BreakerSettings
name|request
init|=
operator|new
name|BreakerSettings
argument_list|(
name|CircuitBreaker
operator|.
name|REQUEST
argument_list|,
name|pctBytes
argument_list|(
literal|"20%"
argument_list|)
argument_list|,
literal|1.0
argument_list|)
decl_stmt|;
name|HierarchyCircuitBreakerService
operator|.
name|validateSettings
argument_list|(
operator|new
name|BreakerSettings
index|[]
block|{
name|fd
block|,
name|request
block|}
argument_list|)
expr_stmt|;
comment|// parent: {:limit 70}, fd: {:limit 40}, request: {:limit 30}
name|fd
operator|=
operator|new
name|BreakerSettings
argument_list|(
name|CircuitBreaker
operator|.
name|FIELDDATA
argument_list|,
name|pctBytes
argument_list|(
literal|"40%"
argument_list|)
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|request
operator|=
operator|new
name|BreakerSettings
argument_list|(
name|CircuitBreaker
operator|.
name|REQUEST
argument_list|,
name|pctBytes
argument_list|(
literal|"30%"
argument_list|)
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|HierarchyCircuitBreakerService
operator|.
name|validateSettings
argument_list|(
operator|new
name|BreakerSettings
index|[]
block|{
name|fd
block|,
name|request
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBreakerSettingsValidationNegativeOverhead
specifier|public
name|void
name|testBreakerSettingsValidationNegativeOverhead
parameter_list|()
block|{
comment|// parent: {:limit 70}, fd: {:limit 50}, request: {:limit 20}
name|BreakerSettings
name|fd
init|=
operator|new
name|BreakerSettings
argument_list|(
name|CircuitBreaker
operator|.
name|FIELDDATA
argument_list|,
name|pctBytes
argument_list|(
literal|"50%"
argument_list|)
argument_list|,
operator|-
literal|0.1
argument_list|)
decl_stmt|;
name|BreakerSettings
name|request
init|=
operator|new
name|BreakerSettings
argument_list|(
name|CircuitBreaker
operator|.
name|REQUEST
argument_list|,
name|pctBytes
argument_list|(
literal|"20%"
argument_list|)
argument_list|,
literal|1.0
argument_list|)
decl_stmt|;
try|try
block|{
name|HierarchyCircuitBreakerService
operator|.
name|validateSettings
argument_list|(
operator|new
name|BreakerSettings
index|[]
block|{
name|fd
block|,
name|request
block|}
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"settings are invalid but validate settings did not throw an exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
literal|"Incorrect message: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"must be non-negative"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRegisterCustomBreaker
specifier|public
name|void
name|testRegisterCustomBreaker
parameter_list|()
throws|throws
name|Exception
block|{
name|CircuitBreakerService
name|service
init|=
operator|new
name|HierarchyCircuitBreakerService
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
operator|new
name|ClusterSettings
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|ClusterSettings
operator|.
name|BUILT_IN_CLUSTER_SETTINGS
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|customName
init|=
literal|"custom"
decl_stmt|;
name|BreakerSettings
name|settings
init|=
operator|new
name|BreakerSettings
argument_list|(
name|customName
argument_list|,
literal|20
argument_list|,
literal|1.0
argument_list|)
decl_stmt|;
name|service
operator|.
name|registerBreaker
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|CircuitBreaker
name|breaker
init|=
name|service
operator|.
name|getBreaker
argument_list|(
name|customName
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|breaker
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|breaker
argument_list|,
name|instanceOf
argument_list|(
name|CircuitBreaker
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|breaker
operator|.
name|getName
argument_list|()
argument_list|,
name|is
argument_list|(
name|customName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

