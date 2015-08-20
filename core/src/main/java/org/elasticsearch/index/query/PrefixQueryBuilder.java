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
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|MultiTermQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|PrefixQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|lucene
operator|.
name|BytesRefs
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
name|index
operator|.
name|mapper
operator|.
name|MappedFieldType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|support
operator|.
name|QueryParsers
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * A Query that matches documents containing terms with a specified prefix.  */
end_comment

begin_class
DECL|class|PrefixQueryBuilder
specifier|public
class|class
name|PrefixQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|PrefixQueryBuilder
argument_list|>
implements|implements
name|MultiTermQueryBuilder
argument_list|<
name|PrefixQueryBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"prefix"
decl_stmt|;
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|value
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
DECL|field|rewrite
specifier|private
name|String
name|rewrite
decl_stmt|;
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|PrefixQueryBuilder
name|PROTOTYPE
init|=
operator|new
name|PrefixQueryBuilder
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|/**      * A Query that matches documents containing terms with a specified prefix.      *      * @param fieldName The name of the field      * @param value The prefix query      */
DECL|method|PrefixQueryBuilder
specifier|public
name|PrefixQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|fieldName
specifier|public
name|String
name|fieldName
parameter_list|()
block|{
return|return
name|this
operator|.
name|fieldName
return|;
block|}
DECL|method|value
specifier|public
name|String
name|value
parameter_list|()
block|{
return|return
name|this
operator|.
name|value
return|;
block|}
DECL|method|rewrite
specifier|public
name|PrefixQueryBuilder
name|rewrite
parameter_list|(
name|String
name|rewrite
parameter_list|)
block|{
name|this
operator|.
name|rewrite
operator|=
name|rewrite
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|rewrite
specifier|public
name|String
name|rewrite
parameter_list|()
block|{
return|return
name|this
operator|.
name|rewrite
return|;
block|}
annotation|@
name|Override
DECL|method|doXContent
specifier|public
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
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"prefix"
argument_list|,
name|this
operator|.
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|rewrite
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"rewrite"
argument_list|,
name|rewrite
argument_list|)
expr_stmt|;
block|}
name|printBoostAndQueryName
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|doToQuery
specifier|protected
name|Query
name|doToQuery
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|MultiTermQuery
operator|.
name|RewriteMethod
name|method
init|=
name|QueryParsers
operator|.
name|parseRewriteMethod
argument_list|(
name|context
operator|.
name|parseFieldMatcher
argument_list|()
argument_list|,
name|rewrite
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
literal|null
decl_stmt|;
name|MappedFieldType
name|fieldType
init|=
name|context
operator|.
name|fieldMapper
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldType
operator|!=
literal|null
condition|)
block|{
name|query
operator|=
name|fieldType
operator|.
name|prefixQuery
argument_list|(
name|value
argument_list|,
name|method
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
name|PrefixQuery
name|prefixQuery
init|=
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|method
operator|!=
literal|null
condition|)
block|{
name|prefixQuery
operator|.
name|setRewriteMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
block|}
name|query
operator|=
name|prefixQuery
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|public
name|QueryValidationException
name|validate
parameter_list|()
block|{
name|QueryValidationException
name|validationException
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isEmpty
argument_list|(
name|this
operator|.
name|fieldName
argument_list|)
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"field name cannot be null or empty."
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|value
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"query text cannot be null"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
return|return
name|validationException
return|;
block|}
annotation|@
name|Override
DECL|method|doReadFrom
specifier|protected
name|PrefixQueryBuilder
name|doReadFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|PrefixQueryBuilder
name|prefixQueryBuilder
init|=
operator|new
name|PrefixQueryBuilder
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|,
name|in
operator|.
name|readString
argument_list|()
argument_list|)
decl_stmt|;
name|prefixQueryBuilder
operator|.
name|rewrite
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
return|return
name|prefixQueryBuilder
return|;
block|}
annotation|@
name|Override
DECL|method|doWriteTo
specifier|protected
name|void
name|doWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeString
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|rewrite
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doHashCode
specifier|protected
specifier|final
name|int
name|doHashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|fieldName
argument_list|,
name|value
argument_list|,
name|rewrite
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doEquals
specifier|protected
name|boolean
name|doEquals
parameter_list|(
name|PrefixQueryBuilder
name|other
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|,
name|other
operator|.
name|fieldName
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|value
argument_list|,
name|other
operator|.
name|value
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|rewrite
argument_list|,
name|other
operator|.
name|rewrite
argument_list|)
return|;
block|}
block|}
end_class

end_unit
