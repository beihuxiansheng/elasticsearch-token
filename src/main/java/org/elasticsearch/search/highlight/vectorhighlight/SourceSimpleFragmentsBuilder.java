begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.highlight.vectorhighlight
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|highlight
operator|.
name|vectorhighlight
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
name|document
operator|.
name|Field
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
name|IndexReader
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
name|vectorhighlight
operator|.
name|BoundaryScanner
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
name|vectorhighlight
operator|.
name|XSimpleFragmentsBuilder
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
name|FieldMapper
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
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|lookup
operator|.
name|SearchLookup
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
name|List
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SourceSimpleFragmentsBuilder
specifier|public
class|class
name|SourceSimpleFragmentsBuilder
extends|extends
name|XSimpleFragmentsBuilder
block|{
DECL|field|mapper
specifier|private
specifier|final
name|FieldMapper
name|mapper
decl_stmt|;
DECL|field|searchContext
specifier|private
specifier|final
name|SearchContext
name|searchContext
decl_stmt|;
DECL|method|SourceSimpleFragmentsBuilder
specifier|public
name|SourceSimpleFragmentsBuilder
parameter_list|(
name|FieldMapper
name|mapper
parameter_list|,
name|SearchContext
name|searchContext
parameter_list|,
name|String
index|[]
name|preTags
parameter_list|,
name|String
index|[]
name|postTags
parameter_list|,
name|BoundaryScanner
name|boundaryScanner
parameter_list|)
block|{
name|super
argument_list|(
name|preTags
argument_list|,
name|postTags
argument_list|,
name|boundaryScanner
argument_list|)
expr_stmt|;
name|this
operator|.
name|mapper
operator|=
name|mapper
expr_stmt|;
name|this
operator|.
name|searchContext
operator|=
name|searchContext
expr_stmt|;
block|}
DECL|field|EMPTY_FIELDS
specifier|public
specifier|static
specifier|final
name|Field
index|[]
name|EMPTY_FIELDS
init|=
operator|new
name|Field
index|[
literal|0
index|]
decl_stmt|;
annotation|@
name|Override
DECL|method|getFields
specifier|protected
name|Field
index|[]
name|getFields
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
comment|// we know its low level reader, and matching docId, since that's how we call the highlighter with
name|SearchLookup
name|lookup
init|=
name|searchContext
operator|.
name|lookup
argument_list|()
decl_stmt|;
name|lookup
operator|.
name|setNextReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|lookup
operator|.
name|setNextDocId
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|values
init|=
name|lookup
operator|.
name|source
argument_list|()
operator|.
name|extractRawValues
argument_list|(
name|mapper
operator|.
name|names
argument_list|()
operator|.
name|sourcePath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|EMPTY_FIELDS
return|;
block|}
name|Field
index|[]
name|fields
init|=
operator|new
name|Field
index|[
name|values
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
name|values
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|fields
index|[
name|i
index|]
operator|=
operator|new
name|Field
argument_list|(
name|mapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
expr_stmt|;
block|}
return|return
name|fields
return|;
block|}
block|}
end_class

end_unit

