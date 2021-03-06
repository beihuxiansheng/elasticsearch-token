begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent.support.filtering
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|support
operator|.
name|filtering
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
name|xcontent
operator|.
name|XContentBuilder
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
name|xcontent
operator|.
name|XContentType
import|;
end_import

begin_class
DECL|class|SmileFilteringGeneratorTests
specifier|public
class|class
name|SmileFilteringGeneratorTests
extends|extends
name|JsonFilteringGeneratorTests
block|{
annotation|@
name|Override
DECL|method|getXContentType
specifier|protected
name|XContentType
name|getXContentType
parameter_list|()
block|{
return|return
name|XContentType
operator|.
name|SMILE
return|;
block|}
annotation|@
name|Override
DECL|method|assertXContentBuilder
specifier|protected
name|void
name|assertXContentBuilder
parameter_list|(
name|XContentBuilder
name|expected
parameter_list|,
name|XContentBuilder
name|builder
parameter_list|)
block|{
name|assertBinary
argument_list|(
name|expected
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

