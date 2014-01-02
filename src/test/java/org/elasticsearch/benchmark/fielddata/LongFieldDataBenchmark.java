begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.benchmark.fielddata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|benchmark
operator|.
name|fielddata
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
name|analysis
operator|.
name|core
operator|.
name|KeywordAnalyzer
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
operator|.
name|Store
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
name|document
operator|.
name|LongField
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
name|DirectoryReader
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
name|IndexWriter
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
name|IndexWriterConfig
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
name|SlowCompositeReaderWrapper
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
name|store
operator|.
name|RAMDirectory
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
name|RamUsageEstimator
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
name|Lucene
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
name|Index
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
name|AtomicNumericFieldData
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
name|IndexNumericFieldData
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
name|ContentPath
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
name|Mapper
operator|.
name|BuilderContext
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
name|core
operator|.
name|LongFieldMapper
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
name|fielddata
operator|.
name|breaker
operator|.
name|DummyCircuitBreakerService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_class
DECL|class|LongFieldDataBenchmark
specifier|public
class|class
name|LongFieldDataBenchmark
block|{
DECL|field|RANDOM
specifier|private
specifier|static
specifier|final
name|Random
name|RANDOM
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|SECONDS_PER_YEAR
specifier|private
specifier|static
specifier|final
name|int
name|SECONDS_PER_YEAR
init|=
literal|60
operator|*
literal|60
operator|*
literal|24
operator|*
literal|365
decl_stmt|;
DECL|enum|Data
specifier|public
specifier|static
enum|enum
name|Data
block|{
DECL|enum constant|SINGLE_VALUES_DENSE_ENUM
name|SINGLE_VALUES_DENSE_ENUM
block|{
specifier|public
name|int
name|numValues
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|nextValue
parameter_list|()
block|{
return|return
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|16
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|SINGLE_VALUED_DENSE_DATE
name|SINGLE_VALUED_DENSE_DATE
block|{
specifier|public
name|int
name|numValues
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|nextValue
parameter_list|()
block|{
comment|// somewhere in-between 2010 and 2012
return|return
literal|1000L
operator|*
operator|(
literal|40L
operator|*
name|SECONDS_PER_YEAR
operator|+
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|2
operator|*
name|SECONDS_PER_YEAR
argument_list|)
operator|)
return|;
block|}
block|}
block|,
DECL|enum constant|MULTI_VALUED_DATE
name|MULTI_VALUED_DATE
block|{
specifier|public
name|int
name|numValues
parameter_list|()
block|{
return|return
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|nextValue
parameter_list|()
block|{
comment|// somewhere in-between 2010 and 2012
return|return
literal|1000L
operator|*
operator|(
literal|40L
operator|*
name|SECONDS_PER_YEAR
operator|+
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|2
operator|*
name|SECONDS_PER_YEAR
argument_list|)
operator|)
return|;
block|}
block|}
block|,
DECL|enum constant|MULTI_VALUED_ENUM
name|MULTI_VALUED_ENUM
block|{
specifier|public
name|int
name|numValues
parameter_list|()
block|{
return|return
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|nextValue
parameter_list|()
block|{
return|return
literal|3
operator|+
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|8
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|SINGLE_VALUED_SPARSE_RANDOM
name|SINGLE_VALUED_SPARSE_RANDOM
block|{
specifier|public
name|int
name|numValues
parameter_list|()
block|{
return|return
name|RANDOM
operator|.
name|nextFloat
argument_list|()
operator|<
literal|0.1f
condition|?
literal|1
else|:
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|nextValue
parameter_list|()
block|{
return|return
name|RANDOM
operator|.
name|nextLong
argument_list|()
return|;
block|}
block|}
block|,
DECL|enum constant|MULTI_VALUED_SPARSE_RANDOM
name|MULTI_VALUED_SPARSE_RANDOM
block|{
specifier|public
name|int
name|numValues
parameter_list|()
block|{
return|return
name|RANDOM
operator|.
name|nextFloat
argument_list|()
operator|<
literal|0.1f
condition|?
literal|1
operator|+
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
else|:
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|nextValue
parameter_list|()
block|{
return|return
name|RANDOM
operator|.
name|nextLong
argument_list|()
return|;
block|}
block|}
block|,
DECL|enum constant|MULTI_VALUED_DENSE_RANDOM
name|MULTI_VALUED_DENSE_RANDOM
block|{
specifier|public
name|int
name|numValues
parameter_list|()
block|{
return|return
literal|1
operator|+
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|nextValue
parameter_list|()
block|{
return|return
name|RANDOM
operator|.
name|nextLong
argument_list|()
return|;
block|}
block|}
block|;
DECL|method|numValues
specifier|public
specifier|abstract
name|int
name|numValues
parameter_list|()
function_decl|;
DECL|method|nextValue
specifier|public
specifier|abstract
name|long
name|nextValue
parameter_list|()
function_decl|;
block|}
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|IndexWriterConfig
name|iwc
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|Lucene
operator|.
name|VERSION
argument_list|,
operator|new
name|KeywordAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|String
name|fieldName
init|=
literal|"f"
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
literal|1000000
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Data\tLoading time\tImplementation\tActual size\tExpected size"
argument_list|)
expr_stmt|;
for|for
control|(
name|Data
name|data
range|:
name|Data
operator|.
name|values
argument_list|()
control|)
block|{
specifier|final
name|RAMDirectory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
specifier|final
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
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
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numFields
init|=
name|data
operator|.
name|numValues
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numFields
condition|;
operator|++
name|j
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LongField
argument_list|(
name|fieldName
argument_list|,
name|data
operator|.
name|nextValue
argument_list|()
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|indexWriter
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|DirectoryReader
name|dr
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
specifier|final
name|IndexFieldDataService
name|fds
init|=
operator|new
name|IndexFieldDataService
argument_list|(
operator|new
name|Index
argument_list|(
literal|"dummy"
argument_list|)
argument_list|,
operator|new
name|DummyCircuitBreakerService
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|LongFieldMapper
name|mapper
init|=
operator|new
name|LongFieldMapper
operator|.
name|Builder
argument_list|(
name|fieldName
argument_list|)
operator|.
name|build
argument_list|(
operator|new
name|BuilderContext
argument_list|(
literal|null
argument_list|,
operator|new
name|ContentPath
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|IndexNumericFieldData
argument_list|<
name|AtomicNumericFieldData
argument_list|>
name|fd
init|=
name|fds
operator|.
name|getForField
argument_list|(
name|mapper
argument_list|)
decl_stmt|;
specifier|final
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
specifier|final
name|AtomicNumericFieldData
name|afd
init|=
name|fd
operator|.
name|loadDirect
argument_list|(
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|dr
argument_list|)
operator|.
name|getContext
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|long
name|loadingTimeMs
init|=
operator|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|start
operator|)
operator|/
literal|1000
operator|/
literal|1000
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|data
operator|+
literal|"\t"
operator|+
name|loadingTimeMs
operator|+
literal|"\t"
operator|+
name|afd
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"\t"
operator|+
name|RamUsageEstimator
operator|.
name|humanSizeOf
argument_list|(
name|afd
operator|.
name|getLongValues
argument_list|()
argument_list|)
operator|+
literal|"\t"
operator|+
name|RamUsageEstimator
operator|.
name|humanReadableUnits
argument_list|(
name|afd
operator|.
name|getMemorySizeInBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|dr
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

