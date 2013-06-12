begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|AtomicReader
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
name|Collector
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
name|common
operator|.
name|lucene
operator|.
name|HashedBytesRef
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
name|settings
operator|.
name|ImmutableSettings
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
name|BytesValues
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
name|FieldDataType
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
name|fieldvisitor
operator|.
name|JustSourceFieldsVisitor
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
name|index
operator|.
name|mapper
operator|.
name|internal
operator|.
name|IdFieldMapper
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
name|Collector
block|{
DECL|field|queries
specifier|private
specifier|final
name|Map
argument_list|<
name|HashedBytesRef
argument_list|,
name|Query
argument_list|>
name|queries
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|field|fieldsVisitor
specifier|private
specifier|final
name|JustSourceFieldsVisitor
name|fieldsVisitor
init|=
operator|new
name|JustSourceFieldsVisitor
argument_list|()
decl_stmt|;
DECL|field|percolator
specifier|private
specifier|final
name|PercolatorQueriesRegistry
name|percolator
decl_stmt|;
DECL|field|idFieldData
specifier|private
specifier|final
name|IndexFieldData
name|idFieldData
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|ESLogger
name|logger
decl_stmt|;
DECL|field|idValues
specifier|private
name|BytesValues
name|idValues
decl_stmt|;
DECL|field|reader
specifier|private
name|AtomicReader
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
parameter_list|,
name|IndexFieldDataService
name|indexFieldDataService
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
name|this
operator|.
name|idFieldData
operator|=
name|indexFieldDataService
operator|.
name|getForField
argument_list|(
operator|new
name|FieldMapper
operator|.
name|Names
argument_list|(
name|IdFieldMapper
operator|.
name|NAME
argument_list|)
argument_list|,
operator|new
name|FieldDataType
argument_list|(
literal|"string"
argument_list|,
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"format"
argument_list|,
literal|"paged_bytes"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|queries
specifier|public
name|Map
argument_list|<
name|HashedBytesRef
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
comment|// the _source is the query
name|BytesRef
name|id
init|=
name|idValues
operator|.
name|getValue
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
return|return;
block|}
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
name|HashedBytesRef
argument_list|(
name|idValues
operator|.
name|makeSafe
argument_list|(
name|id
argument_list|)
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
name|id
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
name|id
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
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
name|idValues
operator|=
name|idFieldData
operator|.
name|load
argument_list|(
name|context
argument_list|)
operator|.
name|getBytesValues
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
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

