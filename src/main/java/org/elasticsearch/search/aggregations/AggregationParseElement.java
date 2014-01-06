begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
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
name|inject
operator|.
name|Inject
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
name|SearchParseElement
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

begin_comment
comment|/**  * The search parse element that is responsible for parsing the get part of the request.  *  * For example (in bold):  *<pre>  *      curl -XGET 'localhost:9200/_search?search_type=count' -d '{  *          query: {  *              match_all : {}  *          },  *          addAggregation : {  *              avg_price: {  *                  avg : { field : price }  *              },  *              categories: {  *                  terms : { field : category, size : 12 },  *                  addAggregation: {  *                      avg_price : { avg : { field : price }}  *                  }  *              }  *          }  *      }'  *</pre>  */
end_comment

begin_class
DECL|class|AggregationParseElement
specifier|public
class|class
name|AggregationParseElement
implements|implements
name|SearchParseElement
block|{
DECL|field|aggregatorParsers
specifier|private
specifier|final
name|AggregatorParsers
name|aggregatorParsers
decl_stmt|;
annotation|@
name|Inject
DECL|method|AggregationParseElement
specifier|public
name|AggregationParseElement
parameter_list|(
name|AggregatorParsers
name|aggregatorParsers
parameter_list|)
block|{
name|this
operator|.
name|aggregatorParsers
operator|=
name|aggregatorParsers
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|void
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|SearchContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|AggregatorFactories
name|factories
init|=
name|aggregatorParsers
operator|.
name|parseAggregators
argument_list|(
name|parser
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|context
operator|.
name|aggregations
argument_list|(
operator|new
name|SearchContextAggregations
argument_list|(
name|factories
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

