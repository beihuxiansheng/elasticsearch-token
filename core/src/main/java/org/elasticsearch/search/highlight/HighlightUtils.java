begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.highlight
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|highlight
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|highlight
operator|.
name|DefaultEncoder
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
name|highlight
operator|.
name|Encoder
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
name|highlight
operator|.
name|SimpleHTMLEncoder
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
name|fieldvisitor
operator|.
name|CustomFieldsVisitor
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
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|lookup
operator|.
name|SourceLookup
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
name|List
import|;
end_import

begin_class
DECL|class|HighlightUtils
specifier|public
specifier|final
class|class
name|HighlightUtils
block|{
comment|//U+2029 PARAGRAPH SEPARATOR (PS): each value holds a discrete passage for highlighting (postings highlighter)
DECL|field|PARAGRAPH_SEPARATOR
specifier|public
specifier|static
specifier|final
name|char
name|PARAGRAPH_SEPARATOR
init|=
literal|8233
decl_stmt|;
DECL|field|NULL_SEPARATOR
specifier|public
specifier|static
specifier|final
name|char
name|NULL_SEPARATOR
init|=
literal|'\u0000'
decl_stmt|;
DECL|method|HighlightUtils
specifier|private
name|HighlightUtils
parameter_list|()
block|{      }
DECL|method|loadFieldValues
specifier|static
name|List
argument_list|<
name|Object
argument_list|>
name|loadFieldValues
parameter_list|(
name|SearchContextHighlight
operator|.
name|Field
name|field
parameter_list|,
name|FieldMapper
name|mapper
parameter_list|,
name|SearchContext
name|searchContext
parameter_list|,
name|FetchSubPhase
operator|.
name|HitContext
name|hitContext
parameter_list|)
throws|throws
name|IOException
block|{
comment|//percolator needs to always load from source, thus it sets the global force source to true
name|boolean
name|forceSource
init|=
name|searchContext
operator|.
name|highlight
argument_list|()
operator|.
name|forceSource
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|textsToHighlight
decl_stmt|;
if|if
condition|(
operator|!
name|forceSource
operator|&&
name|mapper
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
condition|)
block|{
name|CustomFieldsVisitor
name|fieldVisitor
init|=
operator|new
name|CustomFieldsVisitor
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|mapper
operator|.
name|fieldType
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|hitContext
operator|.
name|reader
argument_list|()
operator|.
name|document
argument_list|(
name|hitContext
operator|.
name|docId
argument_list|()
argument_list|,
name|fieldVisitor
argument_list|)
expr_stmt|;
name|textsToHighlight
operator|=
name|fieldVisitor
operator|.
name|fields
argument_list|()
operator|.
name|get
argument_list|(
name|mapper
operator|.
name|fieldType
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|textsToHighlight
operator|==
literal|null
condition|)
block|{
comment|// Can happen if the document doesn't have the field to highlight
name|textsToHighlight
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|SourceLookup
name|sourceLookup
init|=
name|searchContext
operator|.
name|lookup
argument_list|()
operator|.
name|source
argument_list|()
decl_stmt|;
name|sourceLookup
operator|.
name|setSegmentAndDocument
argument_list|(
name|hitContext
operator|.
name|readerContext
argument_list|()
argument_list|,
name|hitContext
operator|.
name|docId
argument_list|()
argument_list|)
expr_stmt|;
name|textsToHighlight
operator|=
name|sourceLookup
operator|.
name|extractRawValues
argument_list|(
name|hitContext
operator|.
name|getSourcePath
argument_list|(
name|mapper
operator|.
name|fieldType
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|fullName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
assert|assert
name|textsToHighlight
operator|!=
literal|null
assert|;
return|return
name|textsToHighlight
return|;
block|}
DECL|class|Encoders
specifier|static
class|class
name|Encoders
block|{
DECL|field|DEFAULT
specifier|static
name|Encoder
name|DEFAULT
init|=
operator|new
name|DefaultEncoder
argument_list|()
decl_stmt|;
DECL|field|HTML
specifier|static
name|Encoder
name|HTML
init|=
operator|new
name|SimpleHTMLEncoder
argument_list|()
decl_stmt|;
block|}
block|}
end_class

end_unit

