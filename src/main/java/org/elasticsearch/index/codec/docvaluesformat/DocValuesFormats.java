begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.codec.docvaluesformat
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|codec
operator|.
name|docvaluesformat
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
name|ImmutableCollection
import|;
end_import

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
name|codecs
operator|.
name|DocValuesFormat
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
name|MapBuilder
import|;
end_import

begin_comment
comment|/**  * This class represents the set of Elasticsearch "built-in"  * {@link DocValuesFormatProvider.Factory doc values format factories}  */
end_comment

begin_class
DECL|class|DocValuesFormats
specifier|public
class|class
name|DocValuesFormats
block|{
DECL|field|builtInDocValuesFormats
specifier|private
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|PreBuiltDocValuesFormatProvider
operator|.
name|Factory
argument_list|>
name|builtInDocValuesFormats
decl_stmt|;
static|static
block|{
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|PreBuiltDocValuesFormatProvider
operator|.
name|Factory
argument_list|>
name|builtInDocValuesFormatsX
init|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|DocValuesFormat
operator|.
name|availableDocValuesFormats
argument_list|()
control|)
block|{
name|builtInDocValuesFormatsX
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|PreBuiltDocValuesFormatProvider
operator|.
name|Factory
argument_list|(
name|DocValuesFormat
operator|.
name|forName
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE UPGRADE: update those DVF if necessary
name|builtInDocValuesFormatsX
operator|.
name|put
argument_list|(
name|DocValuesFormatService
operator|.
name|DEFAULT_FORMAT
argument_list|,
operator|new
name|PreBuiltDocValuesFormatProvider
operator|.
name|Factory
argument_list|(
name|DocValuesFormatService
operator|.
name|DEFAULT_FORMAT
argument_list|,
name|DocValuesFormat
operator|.
name|forName
argument_list|(
literal|"Lucene49"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|builtInDocValuesFormatsX
operator|.
name|put
argument_list|(
literal|"memory"
argument_list|,
operator|new
name|PreBuiltDocValuesFormatProvider
operator|.
name|Factory
argument_list|(
literal|"memory"
argument_list|,
name|DocValuesFormat
operator|.
name|forName
argument_list|(
literal|"Memory"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|builtInDocValuesFormatsX
operator|.
name|put
argument_list|(
literal|"disk"
argument_list|,
operator|new
name|PreBuiltDocValuesFormatProvider
operator|.
name|Factory
argument_list|(
literal|"disk"
argument_list|,
name|DocValuesFormat
operator|.
name|forName
argument_list|(
literal|"Lucene49"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|builtInDocValuesFormatsX
operator|.
name|put
argument_list|(
literal|"Disk"
argument_list|,
operator|new
name|PreBuiltDocValuesFormatProvider
operator|.
name|Factory
argument_list|(
literal|"Disk"
argument_list|,
name|DocValuesFormat
operator|.
name|forName
argument_list|(
literal|"Lucene49"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|builtInDocValuesFormats
operator|=
name|builtInDocValuesFormatsX
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
block|}
DECL|method|getAsFactory
specifier|public
specifier|static
name|DocValuesFormatProvider
operator|.
name|Factory
name|getAsFactory
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|builtInDocValuesFormats
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|getAsProvider
specifier|public
specifier|static
name|DocValuesFormatProvider
name|getAsProvider
parameter_list|(
name|String
name|name
parameter_list|)
block|{
specifier|final
name|PreBuiltDocValuesFormatProvider
operator|.
name|Factory
name|factory
init|=
name|builtInDocValuesFormats
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|factory
operator|==
literal|null
condition|?
literal|null
else|:
name|factory
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|listFactories
specifier|public
specifier|static
name|ImmutableCollection
argument_list|<
name|PreBuiltDocValuesFormatProvider
operator|.
name|Factory
argument_list|>
name|listFactories
parameter_list|()
block|{
return|return
name|builtInDocValuesFormats
operator|.
name|values
argument_list|()
return|;
block|}
block|}
end_class

end_unit

