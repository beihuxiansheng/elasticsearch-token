begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.reducers.movavg.models
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|reducers
operator|.
name|movavg
operator|.
name|models
package|;
end_package

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
name|*
import|;
end_import

begin_class
DECL|class|MovAvgModel
specifier|public
specifier|abstract
class|class
name|MovAvgModel
block|{
comment|/**      * Returns the next value in the series, according to the underlying smoothing model      *      * @param values    Collection of numerics to smooth, usually windowed      * @param<T>       Type of numeric      * @return          Returns a double, since most smoothing methods operate on floating points      */
DECL|method|next
specifier|public
specifier|abstract
parameter_list|<
name|T
extends|extends
name|Number
parameter_list|>
name|double
name|next
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|values
parameter_list|)
function_decl|;
comment|/**      * Write the model to the output stream      *      * @param out   Output stream      * @throws IOException      */
DECL|method|writeTo
specifier|public
specifier|abstract
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

