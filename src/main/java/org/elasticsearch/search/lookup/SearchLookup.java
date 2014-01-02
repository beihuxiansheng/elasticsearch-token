begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.lookup
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|lookup
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
name|ImmutableMap
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
name|AtomicReaderContext
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
name|Scorer
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
name|index
operator|.
name|fielddata
operator|.
name|IndexFieldDataService
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
name|MapperService
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SearchLookup
specifier|public
class|class
name|SearchLookup
block|{
DECL|field|docMap
specifier|final
name|DocLookup
name|docMap
decl_stmt|;
DECL|field|sourceLookup
specifier|final
name|SourceLookup
name|sourceLookup
decl_stmt|;
DECL|field|fieldsLookup
specifier|final
name|FieldsLookup
name|fieldsLookup
decl_stmt|;
DECL|field|indexLookup
specifier|final
name|IndexLookup
name|indexLookup
decl_stmt|;
DECL|field|asMap
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|asMap
decl_stmt|;
DECL|method|SearchLookup
specifier|public
name|SearchLookup
parameter_list|(
name|MapperService
name|mapperService
parameter_list|,
name|IndexFieldDataService
name|fieldDataService
parameter_list|,
annotation|@
name|Nullable
name|String
index|[]
name|types
parameter_list|)
block|{
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|docMap
operator|=
operator|new
name|DocLookup
argument_list|(
name|mapperService
argument_list|,
name|fieldDataService
argument_list|,
name|types
argument_list|)
expr_stmt|;
name|sourceLookup
operator|=
operator|new
name|SourceLookup
argument_list|()
expr_stmt|;
name|fieldsLookup
operator|=
operator|new
name|FieldsLookup
argument_list|(
name|mapperService
argument_list|,
name|types
argument_list|)
expr_stmt|;
name|indexLookup
operator|=
operator|new
name|IndexLookup
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|put
argument_list|(
literal|"doc"
argument_list|,
name|docMap
argument_list|)
expr_stmt|;
name|builder
operator|.
name|put
argument_list|(
literal|"_doc"
argument_list|,
name|docMap
argument_list|)
expr_stmt|;
name|builder
operator|.
name|put
argument_list|(
literal|"_source"
argument_list|,
name|sourceLookup
argument_list|)
expr_stmt|;
name|builder
operator|.
name|put
argument_list|(
literal|"_fields"
argument_list|,
name|fieldsLookup
argument_list|)
expr_stmt|;
name|builder
operator|.
name|put
argument_list|(
literal|"_index"
argument_list|,
name|indexLookup
argument_list|)
expr_stmt|;
name|asMap
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
DECL|method|asMap
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|asMap
parameter_list|()
block|{
return|return
name|this
operator|.
name|asMap
return|;
block|}
DECL|method|source
specifier|public
name|SourceLookup
name|source
parameter_list|()
block|{
return|return
name|this
operator|.
name|sourceLookup
return|;
block|}
DECL|method|indexLookup
specifier|public
name|IndexLookup
name|indexLookup
parameter_list|()
block|{
return|return
name|this
operator|.
name|indexLookup
return|;
block|}
DECL|method|fields
specifier|public
name|FieldsLookup
name|fields
parameter_list|()
block|{
return|return
name|this
operator|.
name|fieldsLookup
return|;
block|}
DECL|method|doc
specifier|public
name|DocLookup
name|doc
parameter_list|()
block|{
return|return
name|this
operator|.
name|docMap
return|;
block|}
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
name|docMap
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{
name|docMap
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|sourceLookup
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|fieldsLookup
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|indexLookup
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|setNextDocId
specifier|public
name|void
name|setNextDocId
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|docMap
operator|.
name|setNextDocId
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|sourceLookup
operator|.
name|setNextDocId
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|fieldsLookup
operator|.
name|setNextDocId
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|indexLookup
operator|.
name|setNextDocId
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

