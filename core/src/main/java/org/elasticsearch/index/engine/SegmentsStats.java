begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.engine
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|engine
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|cursors
operator|.
name|ObjectObjectCursor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|collect
operator|.
name|ImmutableOpenMap
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
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|unit
operator|.
name|ByteSizeValue
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
name|ToXContent
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
name|XContentBuilderString
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
name|Iterator
import|;
end_import

begin_class
DECL|class|SegmentsStats
specifier|public
class|class
name|SegmentsStats
implements|implements
name|Streamable
implements|,
name|ToXContent
block|{
DECL|field|count
specifier|private
name|long
name|count
decl_stmt|;
DECL|field|memoryInBytes
specifier|private
name|long
name|memoryInBytes
decl_stmt|;
DECL|field|termsMemoryInBytes
specifier|private
name|long
name|termsMemoryInBytes
decl_stmt|;
DECL|field|storedFieldsMemoryInBytes
specifier|private
name|long
name|storedFieldsMemoryInBytes
decl_stmt|;
DECL|field|termVectorsMemoryInBytes
specifier|private
name|long
name|termVectorsMemoryInBytes
decl_stmt|;
DECL|field|normsMemoryInBytes
specifier|private
name|long
name|normsMemoryInBytes
decl_stmt|;
DECL|field|docValuesMemoryInBytes
specifier|private
name|long
name|docValuesMemoryInBytes
decl_stmt|;
DECL|field|indexWriterMemoryInBytes
specifier|private
name|long
name|indexWriterMemoryInBytes
decl_stmt|;
DECL|field|indexWriterMaxMemoryInBytes
specifier|private
name|long
name|indexWriterMaxMemoryInBytes
decl_stmt|;
DECL|field|versionMapMemoryInBytes
specifier|private
name|long
name|versionMapMemoryInBytes
decl_stmt|;
DECL|field|bitsetMemoryInBytes
specifier|private
name|long
name|bitsetMemoryInBytes
decl_stmt|;
DECL|field|fileSizes
specifier|private
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|fileSizes
init|=
name|ImmutableOpenMap
operator|.
name|of
argument_list|()
decl_stmt|;
comment|/*      * A map to provide a best-effort approach describing Lucene index files.      *      * Ideally this should be in sync to what the current version of Lucene is using, but it's harmless to leave extensions out,      * they'll just miss a proper description in the stats      */
DECL|field|fileDescriptions
specifier|private
specifier|static
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fileDescriptions
init|=
name|ImmutableOpenMap
operator|.
expr|<
name|String
decl_stmt|,
name|String
decl|>
name|builder
argument_list|()
decl|.
name|fPut
argument_list|(
literal|"si"
argument_list|,
literal|"Segment Info"
argument_list|)
decl|.
name|fPut
argument_list|(
literal|"fnm"
argument_list|,
literal|"Fields"
argument_list|)
decl|.
name|fPut
argument_list|(
literal|"fdx"
argument_list|,
literal|"Field Index"
argument_list|)
decl|.
name|fPut
argument_list|(
literal|"fdt"
argument_list|,
literal|"Field Data"
argument_list|)
decl|.
name|fPut
argument_list|(
literal|"tim"
argument_list|,
literal|"Term Dictionary"
argument_list|)
decl|.
name|fPut
argument_list|(
literal|"tip"
argument_list|,
literal|"Term Index"
argument_list|)
decl|.
name|fPut
argument_list|(
literal|"doc"
argument_list|,
literal|"Frequencies"
argument_list|)
decl|.
name|fPut
argument_list|(
literal|"pos"
argument_list|,
literal|"Positions"
argument_list|)
decl|.
name|fPut
argument_list|(
literal|"pay"
argument_list|,
literal|"Payloads"
argument_list|)
decl|.
name|fPut
argument_list|(
literal|"nvd"
argument_list|,
literal|"Norms"
argument_list|)
decl|.
name|fPut
argument_list|(
literal|"nvm"
argument_list|,
literal|"Norms"
argument_list|)
decl|.
name|fPut
argument_list|(
literal|"dvd"
argument_list|,
literal|"DocValues"
argument_list|)
decl|.
name|fPut
argument_list|(
literal|"dvm"
argument_list|,
literal|"DocValues"
argument_list|)
decl|.
name|fPut
argument_list|(
literal|"tvx"
argument_list|,
literal|"Term Vector Index"
argument_list|)
decl|.
name|fPut
argument_list|(
literal|"tvd"
argument_list|,
literal|"Term Vector Documents"
argument_list|)
decl|.
name|fPut
argument_list|(
literal|"tvf"
argument_list|,
literal|"Term Vector Fields"
argument_list|)
decl|.
name|fPut
argument_list|(
literal|"liv"
argument_list|,
literal|"Live Documents"
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
DECL|method|SegmentsStats
specifier|public
name|SegmentsStats
parameter_list|()
block|{}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|long
name|count
parameter_list|,
name|long
name|memoryInBytes
parameter_list|)
block|{
name|this
operator|.
name|count
operator|+=
name|count
expr_stmt|;
name|this
operator|.
name|memoryInBytes
operator|+=
name|memoryInBytes
expr_stmt|;
block|}
DECL|method|addTermsMemoryInBytes
specifier|public
name|void
name|addTermsMemoryInBytes
parameter_list|(
name|long
name|termsMemoryInBytes
parameter_list|)
block|{
name|this
operator|.
name|termsMemoryInBytes
operator|+=
name|termsMemoryInBytes
expr_stmt|;
block|}
DECL|method|addStoredFieldsMemoryInBytes
specifier|public
name|void
name|addStoredFieldsMemoryInBytes
parameter_list|(
name|long
name|storedFieldsMemoryInBytes
parameter_list|)
block|{
name|this
operator|.
name|storedFieldsMemoryInBytes
operator|+=
name|storedFieldsMemoryInBytes
expr_stmt|;
block|}
DECL|method|addTermVectorsMemoryInBytes
specifier|public
name|void
name|addTermVectorsMemoryInBytes
parameter_list|(
name|long
name|termVectorsMemoryInBytes
parameter_list|)
block|{
name|this
operator|.
name|termVectorsMemoryInBytes
operator|+=
name|termVectorsMemoryInBytes
expr_stmt|;
block|}
DECL|method|addNormsMemoryInBytes
specifier|public
name|void
name|addNormsMemoryInBytes
parameter_list|(
name|long
name|normsMemoryInBytes
parameter_list|)
block|{
name|this
operator|.
name|normsMemoryInBytes
operator|+=
name|normsMemoryInBytes
expr_stmt|;
block|}
DECL|method|addDocValuesMemoryInBytes
specifier|public
name|void
name|addDocValuesMemoryInBytes
parameter_list|(
name|long
name|docValuesMemoryInBytes
parameter_list|)
block|{
name|this
operator|.
name|docValuesMemoryInBytes
operator|+=
name|docValuesMemoryInBytes
expr_stmt|;
block|}
DECL|method|addIndexWriterMemoryInBytes
specifier|public
name|void
name|addIndexWriterMemoryInBytes
parameter_list|(
name|long
name|indexWriterMemoryInBytes
parameter_list|)
block|{
name|this
operator|.
name|indexWriterMemoryInBytes
operator|+=
name|indexWriterMemoryInBytes
expr_stmt|;
block|}
DECL|method|addIndexWriterMaxMemoryInBytes
specifier|public
name|void
name|addIndexWriterMaxMemoryInBytes
parameter_list|(
name|long
name|indexWriterMaxMemoryInBytes
parameter_list|)
block|{
name|this
operator|.
name|indexWriterMaxMemoryInBytes
operator|+=
name|indexWriterMaxMemoryInBytes
expr_stmt|;
block|}
DECL|method|addVersionMapMemoryInBytes
specifier|public
name|void
name|addVersionMapMemoryInBytes
parameter_list|(
name|long
name|versionMapMemoryInBytes
parameter_list|)
block|{
name|this
operator|.
name|versionMapMemoryInBytes
operator|+=
name|versionMapMemoryInBytes
expr_stmt|;
block|}
DECL|method|addBitsetMemoryInBytes
specifier|public
name|void
name|addBitsetMemoryInBytes
parameter_list|(
name|long
name|bitsetMemoryInBytes
parameter_list|)
block|{
name|this
operator|.
name|bitsetMemoryInBytes
operator|+=
name|bitsetMemoryInBytes
expr_stmt|;
block|}
DECL|method|addFileSizes
specifier|public
name|void
name|addFileSizes
parameter_list|(
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|fileSizes
parameter_list|)
block|{
name|ImmutableOpenMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|map
init|=
name|ImmutableOpenMap
operator|.
name|builder
argument_list|(
name|this
operator|.
name|fileSizes
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|ObjectObjectCursor
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
name|it
init|=
name|fileSizes
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ObjectObjectCursor
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
name|entry
operator|.
name|key
argument_list|)
condition|)
block|{
name|Long
name|oldValue
init|=
name|map
operator|.
name|get
argument_list|(
name|entry
operator|.
name|key
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|entry
operator|.
name|key
argument_list|,
name|oldValue
operator|+
name|entry
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|map
operator|.
name|put
argument_list|(
name|entry
operator|.
name|key
argument_list|,
name|entry
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|fileSizes
operator|=
name|map
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|SegmentsStats
name|mergeStats
parameter_list|)
block|{
if|if
condition|(
name|mergeStats
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|add
argument_list|(
name|mergeStats
operator|.
name|count
argument_list|,
name|mergeStats
operator|.
name|memoryInBytes
argument_list|)
expr_stmt|;
name|addTermsMemoryInBytes
argument_list|(
name|mergeStats
operator|.
name|termsMemoryInBytes
argument_list|)
expr_stmt|;
name|addStoredFieldsMemoryInBytes
argument_list|(
name|mergeStats
operator|.
name|storedFieldsMemoryInBytes
argument_list|)
expr_stmt|;
name|addTermVectorsMemoryInBytes
argument_list|(
name|mergeStats
operator|.
name|termVectorsMemoryInBytes
argument_list|)
expr_stmt|;
name|addNormsMemoryInBytes
argument_list|(
name|mergeStats
operator|.
name|normsMemoryInBytes
argument_list|)
expr_stmt|;
name|addDocValuesMemoryInBytes
argument_list|(
name|mergeStats
operator|.
name|docValuesMemoryInBytes
argument_list|)
expr_stmt|;
name|addIndexWriterMemoryInBytes
argument_list|(
name|mergeStats
operator|.
name|indexWriterMemoryInBytes
argument_list|)
expr_stmt|;
name|addIndexWriterMaxMemoryInBytes
argument_list|(
name|mergeStats
operator|.
name|indexWriterMaxMemoryInBytes
argument_list|)
expr_stmt|;
name|addVersionMapMemoryInBytes
argument_list|(
name|mergeStats
operator|.
name|versionMapMemoryInBytes
argument_list|)
expr_stmt|;
name|addBitsetMemoryInBytes
argument_list|(
name|mergeStats
operator|.
name|bitsetMemoryInBytes
argument_list|)
expr_stmt|;
name|addFileSizes
argument_list|(
name|mergeStats
operator|.
name|fileSizes
argument_list|)
expr_stmt|;
block|}
comment|/**      * The number of segments.      */
DECL|method|getCount
specifier|public
name|long
name|getCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|count
return|;
block|}
comment|/**      * Estimation of the memory usage used by a segment.      */
DECL|method|getMemoryInBytes
specifier|public
name|long
name|getMemoryInBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|memoryInBytes
return|;
block|}
DECL|method|getMemory
specifier|public
name|ByteSizeValue
name|getMemory
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|memoryInBytes
argument_list|)
return|;
block|}
comment|/**      * Estimation of the terms dictionary memory usage by a segment.      */
DECL|method|getTermsMemoryInBytes
specifier|public
name|long
name|getTermsMemoryInBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|termsMemoryInBytes
return|;
block|}
DECL|method|getTermsMemory
specifier|public
name|ByteSizeValue
name|getTermsMemory
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|termsMemoryInBytes
argument_list|)
return|;
block|}
comment|/**      * Estimation of the stored fields memory usage by a segment.      */
DECL|method|getStoredFieldsMemoryInBytes
specifier|public
name|long
name|getStoredFieldsMemoryInBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|storedFieldsMemoryInBytes
return|;
block|}
DECL|method|getStoredFieldsMemory
specifier|public
name|ByteSizeValue
name|getStoredFieldsMemory
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|storedFieldsMemoryInBytes
argument_list|)
return|;
block|}
comment|/**      * Estimation of the term vectors memory usage by a segment.      */
DECL|method|getTermVectorsMemoryInBytes
specifier|public
name|long
name|getTermVectorsMemoryInBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|termVectorsMemoryInBytes
return|;
block|}
DECL|method|getTermVectorsMemory
specifier|public
name|ByteSizeValue
name|getTermVectorsMemory
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|termVectorsMemoryInBytes
argument_list|)
return|;
block|}
comment|/**      * Estimation of the norms memory usage by a segment.      */
DECL|method|getNormsMemoryInBytes
specifier|public
name|long
name|getNormsMemoryInBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|normsMemoryInBytes
return|;
block|}
DECL|method|getNormsMemory
specifier|public
name|ByteSizeValue
name|getNormsMemory
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|normsMemoryInBytes
argument_list|)
return|;
block|}
comment|/**      * Estimation of the doc values memory usage by a segment.      */
DECL|method|getDocValuesMemoryInBytes
specifier|public
name|long
name|getDocValuesMemoryInBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|docValuesMemoryInBytes
return|;
block|}
DECL|method|getDocValuesMemory
specifier|public
name|ByteSizeValue
name|getDocValuesMemory
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|docValuesMemoryInBytes
argument_list|)
return|;
block|}
comment|/**      * Estimation of the memory usage by index writer      */
DECL|method|getIndexWriterMemoryInBytes
specifier|public
name|long
name|getIndexWriterMemoryInBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|indexWriterMemoryInBytes
return|;
block|}
DECL|method|getIndexWriterMemory
specifier|public
name|ByteSizeValue
name|getIndexWriterMemory
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|indexWriterMemoryInBytes
argument_list|)
return|;
block|}
comment|/**      * Maximum memory index writer may use before it must write buffered documents to a new segment.      */
DECL|method|getIndexWriterMaxMemoryInBytes
specifier|public
name|long
name|getIndexWriterMaxMemoryInBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|indexWriterMaxMemoryInBytes
return|;
block|}
DECL|method|getIndexWriterMaxMemory
specifier|public
name|ByteSizeValue
name|getIndexWriterMaxMemory
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|indexWriterMaxMemoryInBytes
argument_list|)
return|;
block|}
comment|/**      * Estimation of the memory usage by version map      */
DECL|method|getVersionMapMemoryInBytes
specifier|public
name|long
name|getVersionMapMemoryInBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|versionMapMemoryInBytes
return|;
block|}
DECL|method|getVersionMapMemory
specifier|public
name|ByteSizeValue
name|getVersionMapMemory
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|versionMapMemoryInBytes
argument_list|)
return|;
block|}
comment|/**      * Estimation of how much the cached bit sets are taking. (which nested and p/c rely on)      */
DECL|method|getBitsetMemoryInBytes
specifier|public
name|long
name|getBitsetMemoryInBytes
parameter_list|()
block|{
return|return
name|bitsetMemoryInBytes
return|;
block|}
DECL|method|getBitsetMemory
specifier|public
name|ByteSizeValue
name|getBitsetMemory
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|bitsetMemoryInBytes
argument_list|)
return|;
block|}
DECL|method|getFileSizes
specifier|public
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|getFileSizes
parameter_list|()
block|{
return|return
name|fileSizes
return|;
block|}
DECL|method|readSegmentsStats
specifier|public
specifier|static
name|SegmentsStats
name|readSegmentsStats
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|SegmentsStats
name|stats
init|=
operator|new
name|SegmentsStats
argument_list|()
decl_stmt|;
name|stats
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|stats
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
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
name|Fields
operator|.
name|SEGMENTS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|COUNT
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|MEMORY_IN_BYTES
argument_list|,
name|Fields
operator|.
name|MEMORY
argument_list|,
name|memoryInBytes
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|TERMS_MEMORY_IN_BYTES
argument_list|,
name|Fields
operator|.
name|TERMS_MEMORY
argument_list|,
name|termsMemoryInBytes
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|STORED_FIELDS_MEMORY_IN_BYTES
argument_list|,
name|Fields
operator|.
name|STORED_FIELDS_MEMORY
argument_list|,
name|storedFieldsMemoryInBytes
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|TERM_VECTORS_MEMORY_IN_BYTES
argument_list|,
name|Fields
operator|.
name|TERM_VECTORS_MEMORY
argument_list|,
name|termVectorsMemoryInBytes
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|NORMS_MEMORY_IN_BYTES
argument_list|,
name|Fields
operator|.
name|NORMS_MEMORY
argument_list|,
name|normsMemoryInBytes
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|DOC_VALUES_MEMORY_IN_BYTES
argument_list|,
name|Fields
operator|.
name|DOC_VALUES_MEMORY
argument_list|,
name|docValuesMemoryInBytes
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|INDEX_WRITER_MEMORY_IN_BYTES
argument_list|,
name|Fields
operator|.
name|INDEX_WRITER_MEMORY
argument_list|,
name|indexWriterMemoryInBytes
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|INDEX_WRITER_MAX_MEMORY_IN_BYTES
argument_list|,
name|Fields
operator|.
name|INDEX_WRITER_MAX_MEMORY
argument_list|,
name|indexWriterMaxMemoryInBytes
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|VERSION_MAP_MEMORY_IN_BYTES
argument_list|,
name|Fields
operator|.
name|VERSION_MAP_MEMORY
argument_list|,
name|versionMapMemoryInBytes
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|FIXED_BIT_SET_MEMORY_IN_BYTES
argument_list|,
name|Fields
operator|.
name|FIXED_BIT_SET
argument_list|,
name|bitsetMemoryInBytes
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|FILE_SIZES
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|ObjectObjectCursor
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
name|it
init|=
name|fileSizes
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ObjectObjectCursor
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|entry
operator|.
name|key
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|SIZE_IN_BYTES
argument_list|,
name|Fields
operator|.
name|SIZE
argument_list|,
name|entry
operator|.
name|value
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|DESCRIPTION
argument_list|,
name|fileDescriptions
operator|.
name|getOrDefault
argument_list|(
name|entry
operator|.
name|key
argument_list|,
literal|"Others"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
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
return|return
name|builder
return|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|SEGMENTS
specifier|static
specifier|final
name|XContentBuilderString
name|SEGMENTS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"segments"
argument_list|)
decl_stmt|;
DECL|field|COUNT
specifier|static
specifier|final
name|XContentBuilderString
name|COUNT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"count"
argument_list|)
decl_stmt|;
DECL|field|MEMORY
specifier|static
specifier|final
name|XContentBuilderString
name|MEMORY
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"memory"
argument_list|)
decl_stmt|;
DECL|field|MEMORY_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|MEMORY_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"memory_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|TERMS_MEMORY
specifier|static
specifier|final
name|XContentBuilderString
name|TERMS_MEMORY
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"terms_memory"
argument_list|)
decl_stmt|;
DECL|field|TERMS_MEMORY_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|TERMS_MEMORY_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"terms_memory_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|STORED_FIELDS_MEMORY
specifier|static
specifier|final
name|XContentBuilderString
name|STORED_FIELDS_MEMORY
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"stored_fields_memory"
argument_list|)
decl_stmt|;
DECL|field|STORED_FIELDS_MEMORY_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|STORED_FIELDS_MEMORY_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"stored_fields_memory_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|TERM_VECTORS_MEMORY
specifier|static
specifier|final
name|XContentBuilderString
name|TERM_VECTORS_MEMORY
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"term_vectors_memory"
argument_list|)
decl_stmt|;
DECL|field|TERM_VECTORS_MEMORY_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|TERM_VECTORS_MEMORY_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"term_vectors_memory_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|NORMS_MEMORY
specifier|static
specifier|final
name|XContentBuilderString
name|NORMS_MEMORY
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"norms_memory"
argument_list|)
decl_stmt|;
DECL|field|NORMS_MEMORY_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|NORMS_MEMORY_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"norms_memory_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|DOC_VALUES_MEMORY
specifier|static
specifier|final
name|XContentBuilderString
name|DOC_VALUES_MEMORY
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"doc_values_memory"
argument_list|)
decl_stmt|;
DECL|field|DOC_VALUES_MEMORY_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|DOC_VALUES_MEMORY_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"doc_values_memory_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|INDEX_WRITER_MEMORY
specifier|static
specifier|final
name|XContentBuilderString
name|INDEX_WRITER_MEMORY
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"index_writer_memory"
argument_list|)
decl_stmt|;
DECL|field|INDEX_WRITER_MEMORY_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|INDEX_WRITER_MEMORY_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"index_writer_memory_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|INDEX_WRITER_MAX_MEMORY
specifier|static
specifier|final
name|XContentBuilderString
name|INDEX_WRITER_MAX_MEMORY
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"index_writer_max_memory"
argument_list|)
decl_stmt|;
DECL|field|INDEX_WRITER_MAX_MEMORY_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|INDEX_WRITER_MAX_MEMORY_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"index_writer_max_memory_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|VERSION_MAP_MEMORY
specifier|static
specifier|final
name|XContentBuilderString
name|VERSION_MAP_MEMORY
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"version_map_memory"
argument_list|)
decl_stmt|;
DECL|field|VERSION_MAP_MEMORY_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|VERSION_MAP_MEMORY_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"version_map_memory_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|FIXED_BIT_SET
specifier|static
specifier|final
name|XContentBuilderString
name|FIXED_BIT_SET
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"fixed_bit_set"
argument_list|)
decl_stmt|;
DECL|field|FIXED_BIT_SET_MEMORY_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|FIXED_BIT_SET_MEMORY_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"fixed_bit_set_memory_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|FILE_SIZES
specifier|static
specifier|final
name|XContentBuilderString
name|FILE_SIZES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"file_sizes"
argument_list|)
decl_stmt|;
DECL|field|SIZE
specifier|static
specifier|final
name|XContentBuilderString
name|SIZE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"size"
argument_list|)
decl_stmt|;
DECL|field|SIZE_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|SIZE_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"size_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|DESCRIPTION
specifier|static
specifier|final
name|XContentBuilderString
name|DESCRIPTION
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"description"
argument_list|)
decl_stmt|;
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
name|count
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|memoryInBytes
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|termsMemoryInBytes
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|storedFieldsMemoryInBytes
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|termVectorsMemoryInBytes
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|normsMemoryInBytes
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|docValuesMemoryInBytes
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|indexWriterMemoryInBytes
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|versionMapMemoryInBytes
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|indexWriterMaxMemoryInBytes
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|bitsetMemoryInBytes
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_5_0_0_alpha1
argument_list|)
condition|)
block|{
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|ImmutableOpenMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|map
init|=
name|ImmutableOpenMap
operator|.
name|builder
argument_list|(
name|size
argument_list|)
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|String
name|key
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|Long
name|value
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|fileSizes
operator|=
name|map
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|fileSizes
operator|=
name|ImmutableOpenMap
operator|.
name|of
argument_list|()
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
name|out
operator|.
name|writeVLong
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|memoryInBytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|termsMemoryInBytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|storedFieldsMemoryInBytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|termVectorsMemoryInBytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|normsMemoryInBytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|docValuesMemoryInBytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|indexWriterMemoryInBytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|versionMapMemoryInBytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|indexWriterMaxMemoryInBytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|bitsetMemoryInBytes
argument_list|)
expr_stmt|;
if|if
condition|(
name|out
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_5_0_0_alpha1
argument_list|)
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|fileSizes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|ObjectObjectCursor
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
name|it
init|=
name|fileSizes
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ObjectObjectCursor
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|entry
operator|.
name|key
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|entry
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

