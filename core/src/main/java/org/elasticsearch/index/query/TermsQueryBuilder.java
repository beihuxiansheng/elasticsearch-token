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
name|queries
operator|.
name|TermsQuery
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
name|BooleanClause
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
name|BooleanQuery
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TermQuery
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|get
operator|.
name|GetRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|get
operator|.
name|GetResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
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
name|lucene
operator|.
name|search
operator|.
name|Queries
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
name|support
operator|.
name|XContentMapValues
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
name|indices
operator|.
name|cache
operator|.
name|query
operator|.
name|terms
operator|.
name|TermsLookup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|SearchContext
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|IntStream
import|;
end_import

begin_comment
comment|/**  * A filter for a field based on several terms matching on any of them.  */
end_comment

begin_class
DECL|class|TermsQueryBuilder
specifier|public
class|class
name|TermsQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|TermsQueryBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"terms"
decl_stmt|;
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|TermsQueryBuilder
name|PROTOTYPE
init|=
operator|new
name|TermsQueryBuilder
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_DISABLE_COORD
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_DISABLE_COORD
init|=
literal|false
decl_stmt|;
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|values
decl_stmt|;
annotation|@
name|Deprecated
DECL|field|minimumShouldMatch
specifier|private
name|String
name|minimumShouldMatch
decl_stmt|;
annotation|@
name|Deprecated
DECL|field|disableCoord
specifier|private
name|boolean
name|disableCoord
init|=
name|DEFAULT_DISABLE_COORD
decl_stmt|;
DECL|field|termsLookup
specifier|private
specifier|final
name|TermsLookup
name|termsLookup
decl_stmt|;
DECL|method|TermsQueryBuilder
specifier|public
name|TermsQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|TermsLookup
name|termsLookup
parameter_list|)
block|{
name|this
argument_list|(
name|fieldName
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|DEFAULT_DISABLE_COORD
argument_list|,
name|termsLookup
argument_list|)
expr_stmt|;
block|}
comment|/**      * constructor used internally for serialization of both value / termslookup variants      */
DECL|method|TermsQueryBuilder
name|TermsQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|values
parameter_list|,
name|String
name|minimumShouldMatch
parameter_list|,
name|boolean
name|disableCoord
parameter_list|,
name|TermsLookup
name|termsLookup
parameter_list|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isEmpty
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field name cannot be null."
argument_list|)
throw|;
block|}
if|if
condition|(
name|values
operator|==
literal|null
operator|&&
name|termsLookup
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No value or termsLookup specified for terms query"
argument_list|)
throw|;
block|}
if|if
condition|(
name|values
operator|!=
literal|null
operator|&&
name|termsLookup
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Both values and termsLookup specified for terms query"
argument_list|)
throw|;
block|}
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|disableCoord
operator|=
name|disableCoord
expr_stmt|;
name|this
operator|.
name|minimumShouldMatch
operator|=
name|minimumShouldMatch
expr_stmt|;
name|this
operator|.
name|termsLookup
operator|=
name|termsLookup
expr_stmt|;
block|}
comment|/**      * A filter for a field based on several terms matching on any of them.      *      * @param fieldName The field name      * @param values The terms      */
DECL|method|TermsQueryBuilder
specifier|public
name|TermsQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
modifier|...
name|values
parameter_list|)
block|{
name|this
argument_list|(
name|fieldName
argument_list|,
name|values
operator|!=
literal|null
condition|?
name|Arrays
operator|.
name|asList
argument_list|(
name|values
argument_list|)
else|:
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * A filter for a field based on several terms matching on any of them.      *      * @param fieldName The field name      * @param values The terms      */
DECL|method|TermsQueryBuilder
specifier|public
name|TermsQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|int
modifier|...
name|values
parameter_list|)
block|{
name|this
argument_list|(
name|fieldName
argument_list|,
name|values
operator|!=
literal|null
condition|?
name|Arrays
operator|.
name|stream
argument_list|(
name|values
argument_list|)
operator|.
name|mapToObj
argument_list|(
name|s
lambda|->
name|s
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
else|:
operator|(
name|Iterable
argument_list|<
name|?
argument_list|>
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * A filter for a field based on several terms matching on any of them.      *      * @param fieldName The field name      * @param values The terms      */
DECL|method|TermsQueryBuilder
specifier|public
name|TermsQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|long
modifier|...
name|values
parameter_list|)
block|{
name|this
argument_list|(
name|fieldName
argument_list|,
name|values
operator|!=
literal|null
condition|?
name|Arrays
operator|.
name|stream
argument_list|(
name|values
argument_list|)
operator|.
name|mapToObj
argument_list|(
name|s
lambda|->
name|s
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
else|:
operator|(
name|Iterable
argument_list|<
name|?
argument_list|>
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * A filter for a field based on several terms matching on any of them.      *      * @param fieldName The field name      * @param values The terms      */
DECL|method|TermsQueryBuilder
specifier|public
name|TermsQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|float
modifier|...
name|values
parameter_list|)
block|{
name|this
argument_list|(
name|fieldName
argument_list|,
name|values
operator|!=
literal|null
condition|?
name|IntStream
operator|.
name|range
argument_list|(
literal|0
argument_list|,
name|values
operator|.
name|length
argument_list|)
operator|.
name|mapToObj
argument_list|(
name|i
lambda|->
name|values
index|[
name|i
index|]
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
else|:
operator|(
name|Iterable
argument_list|<
name|?
argument_list|>
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * A filter for a field based on several terms matching on any of them.      *      * @param fieldName The field name      * @param values The terms      */
DECL|method|TermsQueryBuilder
specifier|public
name|TermsQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|double
modifier|...
name|values
parameter_list|)
block|{
name|this
argument_list|(
name|fieldName
argument_list|,
name|values
operator|!=
literal|null
condition|?
name|Arrays
operator|.
name|stream
argument_list|(
name|values
argument_list|)
operator|.
name|mapToObj
argument_list|(
name|s
lambda|->
name|s
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
else|:
operator|(
name|Iterable
argument_list|<
name|?
argument_list|>
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * A filter for a field based on several terms matching on any of them.      *      * @param fieldName The field name      * @param values The terms      */
DECL|method|TermsQueryBuilder
specifier|public
name|TermsQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Object
modifier|...
name|values
parameter_list|)
block|{
name|this
argument_list|(
name|fieldName
argument_list|,
name|values
operator|!=
literal|null
condition|?
name|Arrays
operator|.
name|asList
argument_list|(
name|values
argument_list|)
else|:
operator|(
name|Iterable
argument_list|<
name|?
argument_list|>
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * A filter for a field based on several terms matching on any of them.      *      * @param fieldName The field name      * @param values The terms      */
DECL|method|TermsQueryBuilder
specifier|public
name|TermsQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Iterable
argument_list|<
name|?
argument_list|>
name|values
parameter_list|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isEmpty
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field name cannot be null."
argument_list|)
throw|;
block|}
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No value specified for terms query"
argument_list|)
throw|;
block|}
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|convertToBytesRefListIfStringList
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|this
operator|.
name|termsLookup
operator|=
literal|null
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
DECL|method|values
specifier|public
name|List
argument_list|<
name|Object
argument_list|>
name|values
parameter_list|()
block|{
return|return
name|convertToStringListIfBytesRefList
argument_list|(
name|this
operator|.
name|values
argument_list|)
return|;
block|}
comment|/**      * Sets the minimum number of matches across the provided terms. Defaults to<tt>1</tt>.      * @deprecated use [bool] query instead      */
annotation|@
name|Deprecated
DECL|method|minimumShouldMatch
specifier|public
name|TermsQueryBuilder
name|minimumShouldMatch
parameter_list|(
name|String
name|minimumShouldMatch
parameter_list|)
block|{
name|this
operator|.
name|minimumShouldMatch
operator|=
name|minimumShouldMatch
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|minimumShouldMatch
specifier|public
name|String
name|minimumShouldMatch
parameter_list|()
block|{
return|return
name|this
operator|.
name|minimumShouldMatch
return|;
block|}
comment|/**      * Disables<tt>Similarity#coord(int,int)</tt> in scoring. Defaults to<tt>false</tt>.      * @deprecated use [bool] query instead      */
annotation|@
name|Deprecated
DECL|method|disableCoord
specifier|public
name|TermsQueryBuilder
name|disableCoord
parameter_list|(
name|boolean
name|disableCoord
parameter_list|)
block|{
name|this
operator|.
name|disableCoord
operator|=
name|disableCoord
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|disableCoord
name|boolean
name|disableCoord
parameter_list|()
block|{
return|return
name|this
operator|.
name|disableCoord
return|;
block|}
DECL|method|termsLookup
specifier|public
name|TermsLookup
name|termsLookup
parameter_list|()
block|{
return|return
name|this
operator|.
name|termsLookup
return|;
block|}
comment|/**      * Same as {@link #convertToBytesRefIfString} but on Iterable.      * @param objs the Iterable of input object      * @return the same input or a list of {@link BytesRef} representation if input was a list of type string      */
DECL|method|convertToBytesRefListIfStringList
specifier|private
specifier|static
name|List
argument_list|<
name|Object
argument_list|>
name|convertToBytesRefListIfStringList
parameter_list|(
name|Iterable
argument_list|<
name|?
argument_list|>
name|objs
parameter_list|)
block|{
if|if
condition|(
name|objs
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|List
argument_list|<
name|Object
argument_list|>
name|newObjs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|obj
range|:
name|objs
control|)
block|{
name|newObjs
operator|.
name|add
argument_list|(
name|convertToBytesRefIfString
argument_list|(
name|obj
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|newObjs
return|;
block|}
comment|/**      * Same as {@link #convertToStringIfBytesRef} but on Iterable.      * @param objs the Iterable of input object      * @return the same input or a list of utf8 string if input was a list of type {@link BytesRef}      */
DECL|method|convertToStringListIfBytesRefList
specifier|private
specifier|static
name|List
argument_list|<
name|Object
argument_list|>
name|convertToStringListIfBytesRefList
parameter_list|(
name|Iterable
argument_list|<
name|?
argument_list|>
name|objs
parameter_list|)
block|{
if|if
condition|(
name|objs
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|List
argument_list|<
name|Object
argument_list|>
name|newObjs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|obj
range|:
name|objs
control|)
block|{
name|newObjs
operator|.
name|add
argument_list|(
name|convertToStringIfBytesRef
argument_list|(
name|obj
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|newObjs
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
if|if
condition|(
name|this
operator|.
name|termsLookup
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|termsLookup
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|field
argument_list|(
name|fieldName
argument_list|,
name|convertToStringListIfBytesRefList
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|minimumShouldMatch
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"minimum_should_match"
argument_list|,
name|minimumShouldMatch
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|disableCoord
operator|!=
name|DEFAULT_DISABLE_COORD
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"disable_coord"
argument_list|,
name|disableCoord
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
name|List
argument_list|<
name|Object
argument_list|>
name|terms
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|termsLookup
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|termsLookup
operator|.
name|index
argument_list|()
operator|==
literal|null
condition|)
block|{
name|termsLookup
operator|.
name|index
argument_list|(
name|context
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Client
name|client
init|=
name|context
operator|.
name|getClient
argument_list|()
decl_stmt|;
name|terms
operator|=
name|fetch
argument_list|(
name|termsLookup
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|terms
operator|=
name|values
expr_stmt|;
block|}
if|if
condition|(
name|terms
operator|==
literal|null
operator|||
name|terms
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Queries
operator|.
name|newMatchNoDocsQuery
argument_list|()
return|;
block|}
return|return
name|handleTermsQuery
argument_list|(
name|terms
argument_list|,
name|fieldName
argument_list|,
name|context
argument_list|,
name|minimumShouldMatch
argument_list|,
name|disableCoord
argument_list|)
return|;
block|}
DECL|method|fetch
specifier|private
name|List
argument_list|<
name|Object
argument_list|>
name|fetch
parameter_list|(
name|TermsLookup
name|termsLookup
parameter_list|,
name|Client
name|client
parameter_list|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|GetRequest
name|getRequest
init|=
operator|new
name|GetRequest
argument_list|(
name|termsLookup
operator|.
name|index
argument_list|()
argument_list|,
name|termsLookup
operator|.
name|type
argument_list|()
argument_list|,
name|termsLookup
operator|.
name|id
argument_list|()
argument_list|)
operator|.
name|preference
argument_list|(
literal|"_local"
argument_list|)
operator|.
name|routing
argument_list|(
name|termsLookup
operator|.
name|routing
argument_list|()
argument_list|)
decl_stmt|;
name|getRequest
operator|.
name|copyContextAndHeadersFrom
argument_list|(
name|SearchContext
operator|.
name|current
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|GetResponse
name|getResponse
init|=
name|client
operator|.
name|get
argument_list|(
name|getRequest
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|getResponse
operator|.
name|isExists
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|extractedValues
init|=
name|XContentMapValues
operator|.
name|extractRawValues
argument_list|(
name|termsLookup
operator|.
name|path
argument_list|()
argument_list|,
name|getResponse
operator|.
name|getSourceAsMap
argument_list|()
argument_list|)
decl_stmt|;
name|terms
operator|.
name|addAll
argument_list|(
name|extractedValues
argument_list|)
expr_stmt|;
block|}
return|return
name|terms
return|;
block|}
DECL|method|handleTermsQuery
specifier|private
specifier|static
name|Query
name|handleTermsQuery
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|terms
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|QueryShardContext
name|context
parameter_list|,
name|String
name|minimumShouldMatch
parameter_list|,
name|boolean
name|disableCoord
parameter_list|)
block|{
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
name|String
name|indexFieldName
decl_stmt|;
if|if
condition|(
name|fieldType
operator|!=
literal|null
condition|)
block|{
name|indexFieldName
operator|=
name|fieldType
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|indexFieldName
operator|=
name|fieldName
expr_stmt|;
block|}
name|Query
name|query
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|isFilter
argument_list|()
condition|)
block|{
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
name|termsQuery
argument_list|(
name|terms
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BytesRef
index|[]
name|filterValues
init|=
operator|new
name|BytesRef
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|filterValues
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|filterValues
index|[
name|i
index|]
operator|=
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|terms
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|query
operator|=
operator|new
name|TermsQuery
argument_list|(
name|indexFieldName
argument_list|,
name|filterValues
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|BooleanQuery
operator|.
name|Builder
name|bq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq
operator|.
name|setDisableCoord
argument_list|(
name|disableCoord
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|term
range|:
name|terms
control|)
block|{
if|if
condition|(
name|fieldType
operator|!=
literal|null
condition|)
block|{
name|bq
operator|.
name|add
argument_list|(
name|fieldType
operator|.
name|termQuery
argument_list|(
name|term
argument_list|,
name|context
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|indexFieldName
argument_list|,
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|term
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
block|}
name|query
operator|=
name|Queries
operator|.
name|applyMinimumShouldMatch
argument_list|(
name|bq
operator|.
name|build
argument_list|()
argument_list|,
name|minimumShouldMatch
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|doReadFrom
specifier|protected
name|TermsQueryBuilder
name|doReadFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|field
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|TermsLookup
name|lookup
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|lookup
operator|=
name|TermsLookup
operator|.
name|readTermsLookupFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Object
argument_list|>
name|values
init|=
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|in
operator|.
name|readGenericValue
argument_list|()
decl_stmt|;
name|String
name|minimumShouldMatch
init|=
name|in
operator|.
name|readOptionalString
argument_list|()
decl_stmt|;
name|boolean
name|disableCoord
init|=
name|in
operator|.
name|readBoolean
argument_list|()
decl_stmt|;
return|return
operator|new
name|TermsQueryBuilder
argument_list|(
name|field
argument_list|,
name|values
argument_list|,
name|minimumShouldMatch
argument_list|,
name|disableCoord
argument_list|,
name|lookup
argument_list|)
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
name|writeBoolean
argument_list|(
name|termsLookup
operator|!=
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|termsLookup
operator|!=
literal|null
condition|)
block|{
name|termsLookup
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeGenericValue
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|minimumShouldMatch
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|disableCoord
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doHashCode
specifier|protected
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
name|values
argument_list|,
name|minimumShouldMatch
argument_list|,
name|disableCoord
argument_list|,
name|termsLookup
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
name|TermsQueryBuilder
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
name|values
argument_list|,
name|other
operator|.
name|values
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|minimumShouldMatch
argument_list|,
name|other
operator|.
name|minimumShouldMatch
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|disableCoord
argument_list|,
name|other
operator|.
name|disableCoord
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|termsLookup
argument_list|,
name|other
operator|.
name|termsLookup
argument_list|)
return|;
block|}
block|}
end_class

end_unit

