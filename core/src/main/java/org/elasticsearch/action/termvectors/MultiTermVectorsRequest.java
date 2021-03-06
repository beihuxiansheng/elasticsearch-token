begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.termvectors
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|termvectors
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchParseException
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
name|ActionRequest
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
name|ActionRequestValidationException
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
name|CompositeIndicesRequest
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
name|RealtimeRequest
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
name|ValidateActions
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
name|Nullable
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
DECL|class|MultiTermVectorsRequest
specifier|public
class|class
name|MultiTermVectorsRequest
extends|extends
name|ActionRequest
implements|implements
name|Iterable
argument_list|<
name|TermVectorsRequest
argument_list|>
implements|,
name|CompositeIndicesRequest
implements|,
name|RealtimeRequest
block|{
DECL|field|preference
name|String
name|preference
decl_stmt|;
DECL|field|requests
name|List
argument_list|<
name|TermVectorsRequest
argument_list|>
name|requests
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|ids
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|add
specifier|public
name|MultiTermVectorsRequest
name|add
parameter_list|(
name|TermVectorsRequest
name|termVectorsRequest
parameter_list|)
block|{
name|requests
operator|.
name|add
argument_list|(
name|termVectorsRequest
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|add
specifier|public
name|MultiTermVectorsRequest
name|add
parameter_list|(
name|String
name|index
parameter_list|,
annotation|@
name|Nullable
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|requests
operator|.
name|add
argument_list|(
operator|new
name|TermVectorsRequest
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
name|ActionRequestValidationException
name|validationException
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|requests
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|validationException
operator|=
name|ValidateActions
operator|.
name|addValidationError
argument_list|(
literal|"multi term vectors: no documents requested"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|requests
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|TermVectorsRequest
name|termVectorsRequest
init|=
name|requests
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|ActionRequestValidationException
name|validationExceptionForDoc
init|=
name|termVectorsRequest
operator|.
name|validate
argument_list|()
decl_stmt|;
if|if
condition|(
name|validationExceptionForDoc
operator|!=
literal|null
condition|)
block|{
name|validationException
operator|=
name|ValidateActions
operator|.
name|addValidationError
argument_list|(
literal|"at multi term vectors for doc "
operator|+
name|i
argument_list|,
name|validationExceptionForDoc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|validationException
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|TermVectorsRequest
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableCollection
argument_list|(
name|requests
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|requests
operator|.
name|isEmpty
argument_list|()
operator|&&
name|ids
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|getRequests
specifier|public
name|List
argument_list|<
name|TermVectorsRequest
argument_list|>
name|getRequests
parameter_list|()
block|{
return|return
name|requests
return|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|TermVectorsRequest
name|template
parameter_list|,
annotation|@
name|Nullable
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|parser
operator|!=
literal|null
condition|)
block|{
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
if|if
condition|(
literal|"docs"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"docs array element should include an object"
argument_list|)
throw|;
block|}
name|TermVectorsRequest
name|termVectorsRequest
init|=
operator|new
name|TermVectorsRequest
argument_list|(
name|template
argument_list|)
decl_stmt|;
name|TermVectorsRequest
operator|.
name|parseRequest
argument_list|(
name|termVectorsRequest
argument_list|,
name|parser
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|termVectorsRequest
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"ids"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
if|if
condition|(
operator|!
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ids array element should only contain ids"
argument_list|)
throw|;
block|}
name|ids
operator|.
name|add
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"no parameter named [{}] and type ARRAY"
argument_list|,
name|currentFieldName
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
operator|&&
name|currentFieldName
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
literal|"parameters"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|TermVectorsRequest
operator|.
name|parseRequest
argument_list|(
name|template
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"no parameter named [{}] and type OBJECT"
argument_list|,
name|currentFieldName
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|currentFieldName
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"_mtermvectors: Parameter [{}] not supported"
argument_list|,
name|currentFieldName
argument_list|)
throw|;
block|}
block|}
block|}
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
name|TermVectorsRequest
name|curRequest
init|=
operator|new
name|TermVectorsRequest
argument_list|(
name|template
argument_list|)
decl_stmt|;
name|curRequest
operator|.
name|id
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|requests
operator|.
name|add
argument_list|(
name|curRequest
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|preference
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|requests
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|size
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|requests
operator|.
name|add
argument_list|(
name|TermVectorsRequest
operator|.
name|readTermVectorsRequest
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|preference
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|requests
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|TermVectorsRequest
name|termVectorsRequest
range|:
name|requests
control|)
block|{
name|termVectorsRequest
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|ids
specifier|public
name|void
name|ids
parameter_list|(
name|String
index|[]
name|ids
parameter_list|)
block|{
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
name|this
operator|.
name|ids
operator|.
name|add
argument_list|(
name|id
operator|.
name|replaceAll
argument_list|(
literal|"\\s"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|requests
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|realtime
specifier|public
name|MultiTermVectorsRequest
name|realtime
parameter_list|(
name|boolean
name|realtime
parameter_list|)
block|{
for|for
control|(
name|TermVectorsRequest
name|request
range|:
name|requests
control|)
block|{
name|request
operator|.
name|realtime
argument_list|(
name|realtime
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

