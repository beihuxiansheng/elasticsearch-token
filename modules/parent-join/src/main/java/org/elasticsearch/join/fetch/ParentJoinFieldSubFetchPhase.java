begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.join.fetch
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|join
operator|.
name|fetch
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
name|LeafReader
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
name|index
operator|.
name|SortedDocValues
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
name|ExceptionsHelper
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
name|DocumentMapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|join
operator|.
name|mapper
operator|.
name|ParentIdFieldMapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|join
operator|.
name|mapper
operator|.
name|ParentJoinFieldMapper
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
name|SearchHitField
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
name|fetch
operator|.
name|FetchSubPhase
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * A sub fetch phase that retrieves the join name and the parent id for each document containing  * a {@link ParentJoinFieldMapper} field.  */
end_comment

begin_class
DECL|class|ParentJoinFieldSubFetchPhase
specifier|public
specifier|final
class|class
name|ParentJoinFieldSubFetchPhase
implements|implements
name|FetchSubPhase
block|{
annotation|@
name|Override
DECL|method|hitExecute
specifier|public
name|void
name|hitExecute
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|HitContext
name|hitContext
parameter_list|)
block|{
if|if
condition|(
name|context
operator|.
name|storedFieldsContext
argument_list|()
operator|!=
literal|null
operator|&&
name|context
operator|.
name|storedFieldsContext
argument_list|()
operator|.
name|fetchFields
argument_list|()
operator|==
literal|false
condition|)
block|{
return|return;
block|}
name|ParentJoinFieldMapper
name|mapper
init|=
name|ParentJoinFieldMapper
operator|.
name|getMapper
argument_list|(
name|context
operator|.
name|mapperService
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapper
operator|==
literal|null
condition|)
block|{
comment|// hit has no join field.
return|return;
block|}
name|String
name|joinName
init|=
name|getSortedDocValue
argument_list|(
name|mapper
operator|.
name|name
argument_list|()
argument_list|,
name|hitContext
operator|.
name|reader
argument_list|()
argument_list|,
name|hitContext
operator|.
name|docId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|joinName
operator|==
literal|null
condition|)
block|{
return|return;
block|}
comment|// if the hit is a children we extract the parentId (if it's a parent we can use the _id field directly)
name|ParentIdFieldMapper
name|parentMapper
init|=
name|mapper
operator|.
name|getParentIdFieldMapper
argument_list|(
name|joinName
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|String
name|parentId
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|parentMapper
operator|!=
literal|null
condition|)
block|{
name|parentId
operator|=
name|getSortedDocValue
argument_list|(
name|parentMapper
operator|.
name|name
argument_list|()
argument_list|,
name|hitContext
operator|.
name|reader
argument_list|()
argument_list|,
name|hitContext
operator|.
name|docId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|SearchHitField
argument_list|>
name|fields
init|=
name|hitContext
operator|.
name|hit
argument_list|()
operator|.
name|fieldsOrNull
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|hitContext
operator|.
name|hit
argument_list|()
operator|.
name|fields
argument_list|(
name|fields
argument_list|)
expr_stmt|;
block|}
name|fields
operator|.
name|put
argument_list|(
name|mapper
operator|.
name|name
argument_list|()
argument_list|,
operator|new
name|SearchHitField
argument_list|(
name|mapper
operator|.
name|name
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|joinName
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|parentId
operator|!=
literal|null
condition|)
block|{
name|fields
operator|.
name|put
argument_list|(
name|parentMapper
operator|.
name|name
argument_list|()
argument_list|,
operator|new
name|SearchHitField
argument_list|(
name|parentMapper
operator|.
name|name
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|parentId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getSortedDocValue
specifier|private
name|String
name|getSortedDocValue
parameter_list|(
name|String
name|field
parameter_list|,
name|LeafReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|)
block|{
try|try
block|{
name|SortedDocValues
name|docValues
init|=
name|reader
operator|.
name|getSortedDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|docValues
operator|==
literal|null
operator|||
name|docValues
operator|.
name|advanceExact
argument_list|(
name|docId
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
name|ord
init|=
name|docValues
operator|.
name|ordValue
argument_list|()
decl_stmt|;
name|BytesRef
name|joinName
init|=
name|docValues
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
decl_stmt|;
return|return
name|joinName
operator|.
name|utf8ToString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|ExceptionsHelper
operator|.
name|convertToElastic
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

