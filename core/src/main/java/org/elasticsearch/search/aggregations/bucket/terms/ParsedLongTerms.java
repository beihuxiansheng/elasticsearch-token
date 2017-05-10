begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.terms
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|terms
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
name|ObjectParser
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
name|XContentParser
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

begin_class
DECL|class|ParsedLongTerms
specifier|public
class|class
name|ParsedLongTerms
extends|extends
name|ParsedTerms
block|{
annotation|@
name|Override
DECL|method|getType
specifier|protected
name|String
name|getType
parameter_list|()
block|{
return|return
name|LongTerms
operator|.
name|NAME
return|;
block|}
DECL|field|PARSER
specifier|private
specifier|static
name|ObjectParser
argument_list|<
name|ParsedLongTerms
argument_list|,
name|Void
argument_list|>
name|PARSER
init|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
name|ParsedLongTerms
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
literal|true
argument_list|,
name|ParsedLongTerms
operator|::
operator|new
argument_list|)
decl_stmt|;
static|static
block|{
name|declareParsedTermsFields
argument_list|(
name|PARSER
argument_list|,
name|ParsedBucket
operator|::
name|fromXContent
argument_list|)
expr_stmt|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|ParsedLongTerms
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ParsedLongTerms
name|aggregation
init|=
name|PARSER
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|aggregation
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|aggregation
return|;
block|}
DECL|class|ParsedBucket
specifier|public
specifier|static
class|class
name|ParsedBucket
extends|extends
name|ParsedTerms
operator|.
name|ParsedBucket
block|{
DECL|field|key
specifier|private
name|Long
name|key
decl_stmt|;
annotation|@
name|Override
DECL|method|getKey
specifier|public
name|Object
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
annotation|@
name|Override
DECL|method|getKeyAsString
specifier|public
name|String
name|getKeyAsString
parameter_list|()
block|{
name|String
name|keyAsString
init|=
name|super
operator|.
name|getKeyAsString
argument_list|()
decl_stmt|;
if|if
condition|(
name|keyAsString
operator|!=
literal|null
condition|)
block|{
return|return
name|keyAsString
return|;
block|}
if|if
condition|(
name|key
operator|!=
literal|null
condition|)
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|key
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getKeyAsNumber
specifier|public
name|Number
name|getKeyAsNumber
parameter_list|()
block|{
return|return
name|key
return|;
block|}
annotation|@
name|Override
DECL|method|keyToXContent
specifier|protected
name|XContentBuilder
name|keyToXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|field
argument_list|(
name|CommonFields
operator|.
name|KEY
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|super
operator|.
name|getKeyAsString
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|CommonFields
operator|.
name|KEY_AS_STRING
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|getKeyAsString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
DECL|method|fromXContent
specifier|static
name|ParsedBucket
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|parseTermsBucketXContent
argument_list|(
name|parser
argument_list|,
name|ParsedBucket
operator|::
operator|new
argument_list|,
parameter_list|(
name|p
parameter_list|,
name|bucket
parameter_list|)
lambda|->
name|bucket
operator|.
name|key
operator|=
name|p
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

