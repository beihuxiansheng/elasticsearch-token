begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.percolator
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|percolator
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
name|LeafReaderContext
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
name|Scorer
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
name|SimpleCollector
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
name|common
operator|.
name|logging
operator|.
name|ESLogger
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
name|IndexFieldData
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
name|fielddata
operator|.
name|SortedBinaryDocValues
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
name|FieldsVisitor
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
name|mapper
operator|.
name|MapperService
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
name|Uid
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
name|internal
operator|.
name|UidFieldMapper
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
comment|/**  */
end_comment

begin_class
DECL|class|QueriesLoaderCollector
specifier|final
class|class
name|QueriesLoaderCollector
extends|extends
name|SimpleCollector
block|{
DECL|field|queries
specifier|private
specifier|final
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Query
argument_list|>
name|queries
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|fieldsVisitor
specifier|private
specifier|final
name|FieldsVisitor
name|fieldsVisitor
init|=
operator|new
name|FieldsVisitor
argument_list|(
literal|true
argument_list|)
decl_stmt|;
DECL|field|percolator
specifier|private
specifier|final
name|PercolatorQueriesRegistry
name|percolator
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|ESLogger
name|logger
decl_stmt|;
DECL|field|reader
specifier|private
name|LeafReader
name|reader
decl_stmt|;
DECL|method|QueriesLoaderCollector
name|QueriesLoaderCollector
parameter_list|(
name|PercolatorQueriesRegistry
name|percolator
parameter_list|,
name|ESLogger
name|logger
parameter_list|)
block|{
name|this
operator|.
name|percolator
operator|=
name|percolator
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
block|}
DECL|method|queries
specifier|public
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Query
argument_list|>
name|queries
parameter_list|()
block|{
return|return
name|this
operator|.
name|queries
return|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldsVisitor
operator|.
name|reset
argument_list|()
expr_stmt|;
name|reader
operator|.
name|document
argument_list|(
name|doc
argument_list|,
name|fieldsVisitor
argument_list|)
expr_stmt|;
specifier|final
name|Uid
name|uid
init|=
name|fieldsVisitor
operator|.
name|uid
argument_list|()
decl_stmt|;
try|try
block|{
comment|// id is only used for logging, if we fail we log the id in the catch statement
specifier|final
name|Query
name|parseQuery
init|=
name|percolator
operator|.
name|parsePercolatorDocument
argument_list|(
literal|null
argument_list|,
name|fieldsVisitor
operator|.
name|source
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|parseQuery
operator|!=
literal|null
condition|)
block|{
name|queries
operator|.
name|put
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|uid
operator|.
name|id
argument_list|()
argument_list|)
argument_list|,
name|parseQuery
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to add query [{}] - parser returned null"
argument_list|,
name|uid
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to add query [{}]"
argument_list|,
name|e
argument_list|,
name|uid
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|reader
operator|=
name|context
operator|.
name|reader
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

