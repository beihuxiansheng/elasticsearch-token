begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata.fieldcomparator
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|fieldcomparator
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
name|FieldComparator
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
name|DoubleValues
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|DoubleValuesComparatorBase
specifier|abstract
class|class
name|DoubleValuesComparatorBase
parameter_list|<
name|T
extends|extends
name|Number
parameter_list|>
extends|extends
name|NumberComparatorBase
argument_list|<
name|T
argument_list|>
block|{
DECL|field|indexFieldData
specifier|protected
specifier|final
name|IndexNumericFieldData
argument_list|<
name|?
argument_list|>
name|indexFieldData
decl_stmt|;
DECL|field|missingValue
specifier|protected
specifier|final
name|double
name|missingValue
decl_stmt|;
DECL|field|bottom
specifier|protected
name|double
name|bottom
decl_stmt|;
DECL|field|readerValues
specifier|protected
name|DoubleValues
name|readerValues
decl_stmt|;
DECL|field|sortMode
specifier|protected
specifier|final
name|SortMode
name|sortMode
decl_stmt|;
DECL|method|DoubleValuesComparatorBase
specifier|public
name|DoubleValuesComparatorBase
parameter_list|(
name|IndexNumericFieldData
argument_list|<
name|?
argument_list|>
name|indexFieldData
parameter_list|,
name|double
name|missingValue
parameter_list|,
name|SortMode
name|sortMode
parameter_list|)
block|{
name|this
operator|.
name|indexFieldData
operator|=
name|indexFieldData
expr_stmt|;
name|this
operator|.
name|missingValue
operator|=
name|missingValue
expr_stmt|;
name|this
operator|.
name|sortMode
operator|=
name|sortMode
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareBottom
specifier|public
specifier|final
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|double
name|v2
init|=
name|sortMode
operator|.
name|getRelevantValue
argument_list|(
name|readerValues
argument_list|,
name|doc
argument_list|,
name|missingValue
argument_list|)
decl_stmt|;
return|return
name|compare
argument_list|(
name|bottom
argument_list|,
name|v2
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareTop
specifier|public
name|int
name|compareTop
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|compare
argument_list|(
name|top
operator|.
name|doubleValue
argument_list|()
argument_list|,
name|sortMode
operator|.
name|getRelevantValue
argument_list|(
name|readerValues
argument_list|,
name|doc
argument_list|,
name|missingValue
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
specifier|final
name|FieldComparator
argument_list|<
name|T
argument_list|>
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|readerValues
operator|=
name|indexFieldData
operator|.
name|load
argument_list|(
name|context
argument_list|)
operator|.
name|getDoubleValues
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|compareBottomMissing
specifier|public
name|int
name|compareBottomMissing
parameter_list|()
block|{
return|return
name|compare
argument_list|(
name|bottom
argument_list|,
name|missingValue
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareTopMissing
specifier|public
name|int
name|compareTopMissing
parameter_list|()
block|{
return|return
name|compare
argument_list|(
name|top
operator|.
name|doubleValue
argument_list|()
argument_list|,
name|missingValue
argument_list|)
return|;
block|}
DECL|method|compare
specifier|static
specifier|final
name|int
name|compare
parameter_list|(
name|double
name|left
parameter_list|,
name|double
name|right
parameter_list|)
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|left
argument_list|,
name|right
argument_list|)
return|;
block|}
block|}
end_class

end_unit

