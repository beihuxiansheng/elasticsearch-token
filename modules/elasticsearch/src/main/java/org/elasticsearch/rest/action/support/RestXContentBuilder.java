begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|support
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|xcontent
operator|.
name|XContentFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|xcontent
operator|.
name|XContentType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|xcontent
operator|.
name|builder
operator|.
name|BinaryXContentBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|xcontent
operator|.
name|builder
operator|.
name|XContentBuilder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|RestXContentBuilder
specifier|public
class|class
name|RestXContentBuilder
block|{
DECL|method|restContentBuilder
specifier|public
specifier|static
name|BinaryXContentBuilder
name|restContentBuilder
parameter_list|(
name|RestRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentType
name|contentType
init|=
name|XContentType
operator|.
name|fromRestContentType
argument_list|(
name|request
operator|.
name|header
argument_list|(
literal|"Content-Type"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|contentType
operator|==
literal|null
condition|)
block|{
comment|// try and guess it from the body, if exists
if|if
condition|(
name|request
operator|.
name|hasContent
argument_list|()
condition|)
block|{
name|contentType
operator|=
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|request
operator|.
name|contentByteArray
argument_list|()
argument_list|,
name|request
operator|.
name|contentByteArrayOffset
argument_list|()
argument_list|,
name|request
operator|.
name|contentLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|contentType
operator|==
literal|null
condition|)
block|{
comment|// default to JSON
name|contentType
operator|=
name|XContentType
operator|.
name|JSON
expr_stmt|;
block|}
name|BinaryXContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBinaryBuilder
argument_list|(
name|contentType
argument_list|)
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"pretty"
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|builder
operator|.
name|prettyPrint
argument_list|()
expr_stmt|;
block|}
name|String
name|casing
init|=
name|request
operator|.
name|param
argument_list|(
literal|"case"
argument_list|)
decl_stmt|;
if|if
condition|(
name|casing
operator|!=
literal|null
operator|&&
literal|"camelCase"
operator|.
name|equals
argument_list|(
name|casing
argument_list|)
condition|)
block|{
name|builder
operator|.
name|fieldCaseConversion
argument_list|(
name|XContentBuilder
operator|.
name|FieldCaseConversion
operator|.
name|CAMELCASE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// we expect all REST interfaces to write results in underscore casing, so
comment|// no need for double casing
name|builder
operator|.
name|fieldCaseConversion
argument_list|(
name|XContentBuilder
operator|.
name|FieldCaseConversion
operator|.
name|NONE
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

