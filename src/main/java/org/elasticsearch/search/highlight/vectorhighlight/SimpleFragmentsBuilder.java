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
name|XFieldFragList
operator|.
name|WeightedFragInfo
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

begin_comment
comment|/**  * Direct Subclass of Lucene's org.apache.lucene.search.vectorhighlight.SimpleFragmentsBuilder   * that corrects offsets for broken analysis chains.   */
end_comment

begin_class
DECL|class|SimpleFragmentsBuilder
specifier|public
class|class
name|SimpleFragmentsBuilder
extends|extends
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
block|{
DECL|field|mapper
specifier|protected
specifier|final
name|FieldMapper
argument_list|<
name|?
argument_list|>
name|mapper
decl_stmt|;
DECL|method|SimpleFragmentsBuilder
specifier|public
name|SimpleFragmentsBuilder
parameter_list|(
name|FieldMapper
argument_list|<
name|?
argument_list|>
name|mapper
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
block|}
annotation|@
name|Override
DECL|method|makeFragment
specifier|protected
name|String
name|makeFragment
parameter_list|(
name|StringBuilder
name|buffer
parameter_list|,
name|int
index|[]
name|index
parameter_list|,
name|Field
index|[]
name|values
parameter_list|,
name|WeightedFragInfo
name|fragInfo
parameter_list|,
name|String
index|[]
name|preTags
parameter_list|,
name|String
index|[]
name|postTags
parameter_list|,
name|Encoder
name|encoder
parameter_list|)
block|{
return|return
name|super
operator|.
name|makeFragment
argument_list|(
name|buffer
argument_list|,
name|index
argument_list|,
name|values
argument_list|,
name|FragmentBuilderHelper
operator|.
name|fixWeightedFragInfo
argument_list|(
name|mapper
argument_list|,
name|values
argument_list|,
name|fragInfo
argument_list|)
argument_list|,
name|preTags
argument_list|,
name|postTags
argument_list|,
name|encoder
argument_list|)
return|;
block|}
block|}
end_class

end_unit

