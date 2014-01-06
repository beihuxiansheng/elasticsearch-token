begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Constructs a filter that only match on documents that the field has a value in them.  */
end_comment

begin_class
DECL|class|MissingFilterBuilder
specifier|public
class|class
name|MissingFilterBuilder
extends|extends
name|BaseFilterBuilder
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|filterName
specifier|private
name|String
name|filterName
decl_stmt|;
DECL|field|nullValue
specifier|private
name|Boolean
name|nullValue
decl_stmt|;
DECL|field|existence
specifier|private
name|Boolean
name|existence
decl_stmt|;
DECL|method|MissingFilterBuilder
specifier|public
name|MissingFilterBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**      * Should the missing filter automatically include fields with null value configured in the      * mappings. Defaults to<tt>false</tt>.      */
DECL|method|nullValue
specifier|public
name|MissingFilterBuilder
name|nullValue
parameter_list|(
name|boolean
name|nullValue
parameter_list|)
block|{
name|this
operator|.
name|nullValue
operator|=
name|nullValue
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should hte missing filter include documents where the field doesn't exists in the docs.      * Defaults to<tt>true</tt>.      */
DECL|method|existence
specifier|public
name|MissingFilterBuilder
name|existence
parameter_list|(
name|boolean
name|existence
parameter_list|)
block|{
name|this
operator|.
name|existence
operator|=
name|existence
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the filter name for the filter that can be used when searching for matched_filters per hit.      */
DECL|method|filterName
specifier|public
name|MissingFilterBuilder
name|filterName
parameter_list|(
name|String
name|filterName
parameter_list|)
block|{
name|this
operator|.
name|filterName
operator|=
name|filterName
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|doXContent
specifier|protected
name|void
name|doXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|MissingFilterParser
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|nullValue
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"null_value"
argument_list|,
name|nullValue
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|existence
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"existence"
argument_list|,
name|existence
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|filterName
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"_name"
argument_list|,
name|filterName
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

