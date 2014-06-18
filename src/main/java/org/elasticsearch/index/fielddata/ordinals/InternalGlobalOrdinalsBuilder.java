begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata.ordinals
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|ordinals
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
name|index
operator|.
name|MultiDocValues
operator|.
name|OrdinalMap
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
name|TermsEnum
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
name|Settings
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
name|AbstractIndexComponent
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
name|AtomicFieldData
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
name|settings
operator|.
name|IndexSettings
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
name|CircuitBreakerService
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|InternalGlobalOrdinalsBuilder
specifier|public
class|class
name|InternalGlobalOrdinalsBuilder
extends|extends
name|AbstractIndexComponent
implements|implements
name|GlobalOrdinalsBuilder
block|{
DECL|method|InternalGlobalOrdinalsBuilder
specifier|public
name|InternalGlobalOrdinalsBuilder
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|IndexFieldData
operator|.
name|WithOrdinals
name|build
parameter_list|(
specifier|final
name|IndexReader
name|indexReader
parameter_list|,
name|IndexFieldData
operator|.
name|WithOrdinals
name|indexFieldData
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|CircuitBreakerService
name|breakerService
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|1
assert|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|final
name|AtomicFieldData
operator|.
name|WithOrdinals
argument_list|<
name|?
argument_list|>
index|[]
name|atomicFD
init|=
operator|new
name|AtomicFieldData
operator|.
name|WithOrdinals
index|[
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
specifier|final
name|TermsEnum
index|[]
name|subs
init|=
operator|new
name|TermsEnum
index|[
name|indexReader
operator|.
name|leaves
argument_list|()
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
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|atomicFD
index|[
name|i
index|]
operator|=
name|indexFieldData
operator|.
name|load
argument_list|(
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|subs
index|[
name|i
index|]
operator|=
name|atomicFD
index|[
name|i
index|]
operator|.
name|getBytesValues
argument_list|()
operator|.
name|getTermsEnum
argument_list|()
expr_stmt|;
block|}
specifier|final
name|OrdinalMap
name|ordinalMap
init|=
operator|new
name|OrdinalMap
argument_list|(
literal|null
argument_list|,
name|subs
argument_list|)
decl_stmt|;
specifier|final
name|long
name|memorySizeInBytes
init|=
name|ordinalMap
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
name|breakerService
operator|.
name|getBreaker
argument_list|()
operator|.
name|addWithoutBreaking
argument_list|(
name|memorySizeInBytes
argument_list|)
expr_stmt|;
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Global-ordinals[{}][{}] took {} ms"
argument_list|,
name|indexFieldData
operator|.
name|getFieldNames
argument_list|()
operator|.
name|fullName
argument_list|()
argument_list|,
name|ordinalMap
operator|.
name|getValueCount
argument_list|()
argument_list|,
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|InternalGlobalOrdinalsIndexFieldData
argument_list|(
name|indexFieldData
operator|.
name|index
argument_list|()
argument_list|,
name|settings
argument_list|,
name|indexFieldData
operator|.
name|getFieldNames
argument_list|()
argument_list|,
name|indexFieldData
operator|.
name|getFieldDataType
argument_list|()
argument_list|,
name|atomicFD
argument_list|,
name|ordinalMap
argument_list|,
name|memorySizeInBytes
argument_list|)
return|;
block|}
block|}
end_class

end_unit

