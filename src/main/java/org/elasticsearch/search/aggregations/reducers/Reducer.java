begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.reducers
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
name|base
operator|.
name|Function
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
name|ParseField
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
name|xcontent
operator|.
name|XContentParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|Aggregation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|InternalAggregation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|InternalAggregation
operator|.
name|ReduceContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|InternalAggregation
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|SearchContext
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

begin_class
DECL|class|Reducer
specifier|public
specifier|abstract
class|class
name|Reducer
implements|implements
name|Streamable
block|{
comment|/**      * Parses the reducer request and creates the appropriate reducer factory      * for it.      *       * @see {@link ReducerFactory}      */
DECL|interface|Parser
specifier|public
specifier|static
interface|interface
name|Parser
block|{
DECL|field|BUCKETS_PATH
specifier|public
specifier|static
specifier|final
name|ParseField
name|BUCKETS_PATH
init|=
operator|new
name|ParseField
argument_list|(
literal|"buckets_path"
argument_list|)
decl_stmt|;
comment|/**          * @return The reducer type this parser is associated with.          */
DECL|method|type
name|String
name|type
parameter_list|()
function_decl|;
comment|/**          * Returns the reducer factory with which this parser is associated.          *           * @param reducerName          *            The name of the reducer          * @param parser          *            The xcontent parser          * @param context          *            The search context          * @return The resolved reducer factory          * @throws java.io.IOException          *             When parsing fails          */
DECL|method|parse
name|ReducerFactory
name|parse
parameter_list|(
name|String
name|reducerName
parameter_list|,
name|XContentParser
name|parser
parameter_list|,
name|SearchContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|field|AGGREGATION_TRANFORM_FUNCTION
specifier|public
specifier|static
specifier|final
name|Function
argument_list|<
name|Aggregation
argument_list|,
name|InternalAggregation
argument_list|>
name|AGGREGATION_TRANFORM_FUNCTION
init|=
operator|new
name|Function
argument_list|<
name|Aggregation
argument_list|,
name|InternalAggregation
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InternalAggregation
name|apply
parameter_list|(
name|Aggregation
name|input
parameter_list|)
block|{
return|return
operator|(
name|InternalAggregation
operator|)
name|input
return|;
block|}
block|}
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|bucketsPaths
specifier|private
name|String
index|[]
name|bucketsPaths
decl_stmt|;
DECL|field|metaData
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
decl_stmt|;
DECL|method|Reducer
specifier|protected
name|Reducer
parameter_list|()
block|{
comment|// for Serialisation
block|}
DECL|method|Reducer
specifier|protected
name|Reducer
parameter_list|(
name|String
name|name
parameter_list|,
name|String
index|[]
name|bucketsPaths
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|bucketsPaths
operator|=
name|bucketsPaths
expr_stmt|;
name|this
operator|.
name|metaData
operator|=
name|metaData
expr_stmt|;
block|}
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|bucketsPaths
specifier|public
name|String
index|[]
name|bucketsPaths
parameter_list|()
block|{
return|return
name|bucketsPaths
return|;
block|}
DECL|method|metaData
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|()
block|{
return|return
name|metaData
return|;
block|}
DECL|method|type
specifier|public
specifier|abstract
name|Type
name|type
parameter_list|()
function_decl|;
DECL|method|reduce
specifier|public
specifier|abstract
name|InternalAggregation
name|reduce
parameter_list|(
name|InternalAggregation
name|aggregation
parameter_list|,
name|ReduceContext
name|reduceContext
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|writeTo
specifier|public
specifier|final
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
name|writeString
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeStringArray
argument_list|(
name|bucketsPaths
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeMap
argument_list|(
name|metaData
argument_list|)
expr_stmt|;
name|doWriteTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|doWriteTo
specifier|protected
specifier|abstract
name|void
name|doWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|readFrom
specifier|public
specifier|final
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|name
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|bucketsPaths
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|metaData
operator|=
name|in
operator|.
name|readMap
argument_list|()
expr_stmt|;
name|doReadFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|doReadFrom
specifier|protected
specifier|abstract
name|void
name|doReadFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

