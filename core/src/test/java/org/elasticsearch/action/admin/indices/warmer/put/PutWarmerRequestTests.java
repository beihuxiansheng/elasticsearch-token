begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.warmer.put
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|warmer
operator|.
name|put
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionRequestValidationException
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
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
name|hasSize
import|;
end_import

begin_class
DECL|class|PutWarmerRequestTests
specifier|public
class|class
name|PutWarmerRequestTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
comment|// issue 4196
DECL|method|testThatValidationWithoutSpecifyingSearchRequestFails
specifier|public
name|void
name|testThatValidationWithoutSpecifyingSearchRequestFails
parameter_list|()
block|{
name|PutWarmerRequest
name|putWarmerRequest
init|=
operator|new
name|PutWarmerRequest
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|ActionRequestValidationException
name|validationException
init|=
name|putWarmerRequest
operator|.
name|validate
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|validationException
operator|.
name|validationErrors
argument_list|()
argument_list|,
name|hasSize
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|validationException
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"search request is missing"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

